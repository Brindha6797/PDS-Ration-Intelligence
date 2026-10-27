package com.wipro.pds.service;

import com.wipro.pds.dto.ReconciliationRequest;
import com.wipro.pds.entity.*;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReconciliationService {

    private final StockReconciliationRepository reconciliationRepository;
    private final ShopRepository shopRepository;
    private final CommodityRepository commodityRepository;
    private final ShopStockRepository shopStockRepository;

    public ReconciliationService(StockReconciliationRepository reconciliationRepository,
                                 ShopRepository shopRepository,
                                 CommodityRepository commodityRepository,
                                 ShopStockRepository shopStockRepository) {
        this.reconciliationRepository = reconciliationRepository;
        this.shopRepository = shopRepository;
        this.commodityRepository = commodityRepository;
        this.shopStockRepository = shopStockRepository;
    }

    public List<StockReconciliation> getReconciliationsByShop(Long shopId) {
        return reconciliationRepository.findByShop_ShopIdOrderByReconDateDesc(shopId);
    }

    public List<StockReconciliation> getAllReconciliations() {
        return reconciliationRepository.findAll();
    }

    @Transactional
    public StockReconciliation recordReconciliation(ReconciliationRequest request, Long officerId) {
        Shop shop = shopRepository.findById(request.getShopId())
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + request.getShopId()));
        Commodity commodity = commodityRepository.findById(request.getCommodityId())
            .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with ID: " + request.getCommodityId()));

        ShopStock stock = shopStockRepository.findByShop_ShopIdAndCommodity_CommodityId(shop.getShopId(), commodity.getCommodityId())
            .orElseThrow(() -> new ResourceNotFoundException("No inventory record found for commodity in this shop"));

        BigDecimal recorded = stock.getCurrentQuantity();
        BigDecimal expected = stock.getOpeningMonthBalance()
            .add(stock.getTotalReceivedMonth())
            .subtract(stock.getTotalDistributedMonth());

        BigDecimal physical = request.getPhysicalCount();
        BigDecimal variance = physical.subtract(recorded);

        BigDecimal discrepancyPct = BigDecimal.ZERO;
        if (recorded.compareTo(BigDecimal.ZERO) > 0) {
            discrepancyPct = variance.abs()
                .divide(recorded, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100.00"))
                .setScale(2, RoundingMode.HALF_UP);
        } else if (variance.compareTo(BigDecimal.ZERO) != 0) {
            discrepancyPct = new BigDecimal("100.00");
        }

        String status = variance.compareTo(BigDecimal.ZERO) == 0 ? "MATCHED" : "PENDING_REVIEW";

        StockReconciliation recon = new StockReconciliation();
        recon.setShop(shop);
        recon.setCommodity(commodity);
        recon.setReconDate(LocalDate.now());
        recon.setRecordedSystemStock(recorded);
        recon.setCalculatedExpectedStock(expected);
        recon.setPhysicalCountedStock(physical);
        recon.setVarianceQuantity(variance);
        recon.setDiscrepancyPercentage(discrepancyPct);
        recon.setInvestigationStatus(status);
        recon.setInspectingOfficerId(officerId);
        recon.setOfficerRemarks(request.getRemarks());
        recon.setCreatedAt(LocalDateTime.now());

        // Update shop stock last reconciliation date
        stock.setLastReconciliationDate(LocalDate.now());
        stock.setUpdatedAt(LocalDateTime.now());
        shopStockRepository.save(stock);

        return reconciliationRepository.save(recon);
    }

    @Transactional
    public StockReconciliation updateStatus(Long reconId, String status, String remarks) {
        StockReconciliation recon = reconciliationRepository.findById(reconId)
            .orElseThrow(() -> new ResourceNotFoundException("Reconciliation record not found with ID: " + reconId));

        recon.setInvestigationStatus(status);
        if (remarks != null && !remarks.isBlank()) {
            recon.setOfficerRemarks(remarks);
        }
        return reconciliationRepository.save(recon);
    }
}
