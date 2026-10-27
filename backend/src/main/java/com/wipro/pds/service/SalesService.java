package com.wipro.pds.service;

import com.wipro.pds.dto.SaleItemRequest;
import com.wipro.pds.dto.SaleItemResponse;
import com.wipro.pds.dto.SaleRequest;
import com.wipro.pds.dto.SaleResponse;
import com.wipro.pds.entity.*;
import com.wipro.pds.exception.BadRequestException;
import com.wipro.pds.exception.EntitlementExhaustedException;
import com.wipro.pds.exception.InsufficientStockException;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesService {

    private final SaleRepository saleRepository;
    private final ShopRepository shopRepository;
    private final CardHolderRepository cardHolderRepository;
    private final CommodityRepository commodityRepository;
    private final ShopStockRepository shopStockRepository;
    private final EntitlementRepository entitlementRepository;
    private final StockMovementRepository stockMovementRepository;
    private final AuditLogRepository auditLogRepository;

    public SalesService(SaleRepository saleRepository,
                        ShopRepository shopRepository,
                        CardHolderRepository cardHolderRepository,
                        CommodityRepository commodityRepository,
                        ShopStockRepository shopStockRepository,
                        EntitlementRepository entitlementRepository,
                        StockMovementRepository stockMovementRepository,
                        AuditLogRepository auditLogRepository) {
        this.saleRepository = saleRepository;
        this.shopRepository = shopRepository;
        this.cardHolderRepository = cardHolderRepository;
        this.commodityRepository = commodityRepository;
        this.shopStockRepository = shopStockRepository;
        this.entitlementRepository = entitlementRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<Sale> getSalesByShop(Long shopId) {
        return saleRepository.findByShop_ShopIdOrderBySaleDateDesc(shopId);
    }

    public List<Sale> getSalesByCard(Long cardId) {
        return saleRepository.findByCardHolder_CardIdOrderBySaleDateDesc(cardId);
    }

    public Sale getSaleById(Long saleId) {
        return saleRepository.findById(saleId)
            .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SaleResponse processSale(SaleRequest request, Long operatorUserId) {
        // 1. Validate Cardholder
        CardHolder cardHolder = cardHolderRepository.findById(request.getCardId())
            .orElseThrow(() -> new ResourceNotFoundException("Ration Card not found with ID: " + request.getCardId()));

        if (!"ACTIVE".equalsIgnoreCase(cardHolder.getCardStatus())) {
            throw new BadRequestException("Ration Card " + cardHolder.getCardNumber() + " is " + cardHolder.getCardStatus() + ". Cannot distribute ration.");
        }

        // 2. Validate Shop
        Shop shop = shopRepository.findById(request.getShopId())
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + request.getShopId()));

        if (!"ACTIVE".equalsIgnoreCase(shop.getShopStatus())) {
            throw new BadRequestException("Fair-Price Shop " + shop.getShopCode() + " is " + shop.getShopStatus() + ". Sales are halted.");
        }

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        // 3. Create Sale Header
        Sale sale = new Sale();
        String billNumber = "BILL-" + shop.getShopCode() + "-" + currentYear + String.format("%02d", currentMonth) + "-" + (System.currentTimeMillis() % 1000000);
        sale.setBillNumber(billNumber);
        sale.setShop(shop);
        sale.setCardHolder(cardHolder);
        sale.setSaleDate(LocalDateTime.now());
        sale.setSaleMonth(currentMonth);
        sale.setSaleYear(currentYear);
        sale.setPaymentMode(request.getPaymentMode() != null ? request.getPaymentMode() : "CASH");
        sale.setAuthMode(request.getAuthMode() != null ? request.getAuthMode() : "AADHAAR_OTP");
        sale.setOperatorUserId(operatorUserId);
        sale.setSaleStatus("COMPLETED");
        sale.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleItemResponse> itemResponses = new ArrayList<>();

        // 4. Validate & Process Each Item with Row-Level Locking
        for (SaleItemRequest itemReq : request.getItems()) {
            Commodity comm = commodityRepository.findById(itemReq.getCommodityId())
                .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with ID: " + itemReq.getCommodityId()));

            // A. Acquire pessimistic lock on Shop Stock row (SELECT ... FOR UPDATE)
            ShopStock stock = shopStockRepository.findForUpdate(shop.getShopId(), comm.getCommodityId())
                .orElseThrow(() -> new InsufficientStockException("No stock inventory found for '" + comm.getCommodityName() + "' in this shop."));

            if (stock.getCurrentQuantity().compareTo(itemReq.getQuantity()) < 0) {
                throw new InsufficientStockException("Insufficient shop stock for " + comm.getCommodityName() 
                    + ". Available: " + stock.getCurrentQuantity() + " " + comm.getMeasurementUnit() 
                    + ", Requested: " + itemReq.getQuantity() + " " + comm.getMeasurementUnit());
            }

            // B. Check Entitlement Quota for current month
            Entitlement entitlement = entitlementRepository
                .findByCardHolder_CardIdAndCommodity_CommodityIdAndAllotmentYearAndAllotmentMonth(
                    cardHolder.getCardId(), comm.getCommodityId(), currentYear, currentMonth)
                .orElseThrow(() -> new EntitlementExhaustedException("No entitlement record found for " + comm.getCommodityName() 
                    + " for this card in month " + currentMonth + "/" + currentYear));

            if (entitlement.getRemainingQuantity().compareTo(itemReq.getQuantity()) < 0) {
                throw new EntitlementExhaustedException("Entitlement exhausted for " + comm.getCommodityName() 
                    + ". Permitted remaining: " + entitlement.getRemainingQuantity() + " " + comm.getMeasurementUnit() 
                    + ", Requested: " + itemReq.getQuantity() + " " + comm.getMeasurementUnit());
            }

            // C. Create Line Item
            BigDecimal itemPrice = comm.getConsumerRatePerUnit().multiply(itemReq.getQuantity());
            BigDecimal subsidy = comm.getGovernmentSubsidyPerUnit().multiply(itemReq.getQuantity());
            totalAmount = totalAmount.add(itemPrice);

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setCommodity(comm);
            saleItem.setQuantity(itemReq.getQuantity());
            saleItem.setUnitRate(comm.getConsumerRatePerUnit());
            saleItem.setTotalPrice(itemPrice);
            saleItem.setSubsidyAmount(subsidy);
            sale.getItems().add(saleItem);

            // D. Decrement Shop Inventory
            stock.setCurrentQuantity(stock.getCurrentQuantity().subtract(itemReq.getQuantity()));
            stock.setTotalDistributedMonth(stock.getTotalDistributedMonth().add(itemReq.getQuantity()));
            stock.setLastSaleDate(LocalDateTime.now());
            stock.setUpdatedAt(LocalDateTime.now());
            shopStockRepository.save(stock);

            // E. Decrement Cardholder Entitlement
            entitlement.setDistributedQuantity(entitlement.getDistributedQuantity().add(itemReq.getQuantity()));
            entitlement.setRemainingQuantity(entitlement.getRemainingQuantity().subtract(itemReq.getQuantity()));
            entitlement.setLastDistributedAt(LocalDateTime.now());
            if (entitlement.getRemainingQuantity().compareTo(new BigDecimal("0.001")) <= 0) {
                entitlement.setStatus("EXHAUSTED");
            } else {
                entitlement.setStatus("PARTIALLY_DRAWN");
            }
            entitlement.setUpdatedAt(LocalDateTime.now());
            entitlementRepository.save(entitlement);

            // F. Append to Immutable Stock Movement Ledger
            StockMovement sm = new StockMovement();
            sm.setMovementType("SALE");
            sm.setSourceType("SHOP");
            sm.setSourceId(shop.getShopId());
            sm.setDestType("CARDHOLDER");
            sm.setDestId(cardHolder.getCardId());
            sm.setCommodity(comm);
            sm.setQuantity(itemReq.getQuantity());
            sm.setReferenceDocType("BILL");
            sm.setReferenceDocId(billNumber);
            sm.setCreatedBy("USER_" + operatorUserId);
            sm.setMovementTimestamp(LocalDateTime.now());
            stockMovementRepository.save(sm);

            // Item response
            SaleItemResponse itemRes = new SaleItemResponse();
            itemRes.setCommodityId(comm.getCommodityId());
            itemRes.setCommodityName(comm.getCommodityName());
            itemRes.setMeasurementUnit(comm.getMeasurementUnit());
            itemRes.setQuantity(itemReq.getQuantity());
            itemRes.setUnitRate(comm.getConsumerRatePerUnit());
            itemRes.setTotalPrice(itemPrice);
            itemRes.setSubsidyAmount(subsidy);
            itemResponses.add(itemRes);
        }

        sale.setTotalAmount(totalAmount);
        Sale savedSale = saleRepository.save(sale);

        // G. Audit Log
        AuditLog audit = new AuditLog();
        audit.setEntityName("SALES");
        audit.setEntityId(savedSale.getSaleId().toString());
        audit.setActionType("INSERT");
        audit.setPerformedBy("USER_" + operatorUserId);
        audit.setNewValues("{\"billNumber\":\"" + billNumber + "\",\"totalAmount\":" + totalAmount + "}");
        audit.setStatusResult("SUCCESS");
        auditLogRepository.save(audit);

        // H. Build response
        SaleResponse response = new SaleResponse();
        response.setSaleId(savedSale.getSaleId());
        response.setBillNumber(savedSale.getBillNumber());
        response.setShopCode(shop.getShopCode());
        response.setCardNumber(cardHolder.getCardNumber());
        response.setHeadOfFamilyName(cardHolder.getHeadOfFamilyName());
        response.setSaleDate(savedSale.getSaleDate());
        response.setTotalAmount(savedSale.getTotalAmount());
        response.setPaymentMode(savedSale.getPaymentMode());
        response.setAuthMode(savedSale.getAuthMode());
        response.setItems(itemResponses);

        return response;
    }
}
