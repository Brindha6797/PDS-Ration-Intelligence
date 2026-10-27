package com.wipro.pds.service;

import com.wipro.pds.dto.DispatchItemRequest;
import com.wipro.pds.dto.DispatchRequest;
import com.wipro.pds.entity.*;
import com.wipro.pds.exception.BadRequestException;
import com.wipro.pds.exception.InsufficientStockException;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final GodownRepository godownRepository;
    private final ShopRepository shopRepository;
    private final CommodityRepository commodityRepository;
    private final GodownStockRepository godownStockRepository;
    private final ShopStockRepository shopStockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final AllotmentRepository allotmentRepository;

    public DispatchService(DispatchRepository dispatchRepository,
                           GodownRepository godownRepository,
                           ShopRepository shopRepository,
                           CommodityRepository commodityRepository,
                           GodownStockRepository godownStockRepository,
                           ShopStockRepository shopStockRepository,
                           StockMovementRepository stockMovementRepository,
                           AllotmentRepository allotmentRepository) {
        this.dispatchRepository = dispatchRepository;
        this.godownRepository = godownRepository;
        this.shopRepository = shopRepository;
        this.commodityRepository = commodityRepository;
        this.godownStockRepository = godownStockRepository;
        this.shopStockRepository = shopStockRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.allotmentRepository = allotmentRepository;
    }

    public List<Dispatch> getDispatchesByShop(Long shopId) {
        return dispatchRepository.findByShop_ShopIdOrderByDispatchedAtDesc(shopId);
    }

    public List<Dispatch> getDispatchesByGodown(Long godownId) {
        return dispatchRepository.findByGodown_GodownIdOrderByDispatchedAtDesc(godownId);
    }

    public Dispatch getDispatchById(Long dispatchId) {
        return dispatchRepository.findById(dispatchId)
            .orElseThrow(() -> new ResourceNotFoundException("Dispatch not found with ID: " + dispatchId));
    }

    @Transactional
    public Dispatch createDispatch(DispatchRequest request, String username) {
        Godown godown = godownRepository.findById(request.getGodownId())
            .orElseThrow(() -> new ResourceNotFoundException("Godown not found with ID: " + request.getGodownId()));
        Shop shop = shopRepository.findById(request.getShopId())
            .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + request.getShopId()));

        Dispatch dispatch = new Dispatch();
        String dispatchNum = "DSP-" + System.currentTimeMillis() % 1000000;
        dispatch.setDispatchNumber(dispatchNum);
        dispatch.setGodown(godown);
        dispatch.setShop(shop);
        dispatch.setVehicleNumber(request.getVehicleNumber());
        dispatch.setDriverName(request.getDriverName());
        dispatch.setDriverPhone(request.getDriverPhone());
        dispatch.setRemarks(request.getRemarks());
        dispatch.setDispatchStatus("IN_TRANSIT");
        dispatch.setDispatchedAt(LocalDateTime.now());
        dispatch.setDispatchedBy(username);
        dispatch.setCreatedAt(LocalDateTime.now());

        Dispatch savedDispatch = dispatchRepository.save(dispatch);

        for (DispatchItemRequest itemReq : request.getItems()) {
            Commodity comm = commodityRepository.findById(itemReq.getCommodityId())
                .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with ID: " + itemReq.getCommodityId()));

            // Verify and decrement Godown Stock
            GodownStock gStock = godownStockRepository
                .findByGodown_GodownIdAndCommodity_CommodityId(godown.getGodownId(), comm.getCommodityId())
                .orElseThrow(() -> new InsufficientStockException("No stock record for commodity '" + comm.getCommodityName() + "' in godown"));

            if (gStock.getCurrentQuantity().compareTo(itemReq.getQuantity()) < 0) {
                throw new InsufficientStockException("Insufficient stock for '" + comm.getCommodityName() + "' in Godown. Available: " 
                    + gStock.getCurrentQuantity() + ", Requested: " + itemReq.getQuantity());
            }

            gStock.setCurrentQuantity(gStock.getCurrentQuantity().subtract(itemReq.getQuantity()));
            gStock.setUpdatedAt(LocalDateTime.now());
            godownStockRepository.save(gStock);

            DispatchItem item = new DispatchItem();
            item.setDispatch(savedDispatch);
            item.setCommodity(comm);
            item.setDispatchedQuantity(itemReq.getQuantity());
            item.setDiscrepancyQuantity(BigDecimal.ZERO);
            savedDispatch.getItems().add(item);

            // Record immutable stock movement
            StockMovement sm = new StockMovement();
            sm.setMovementType("GODOWN_DISPATCH");
            sm.setSourceType("GODOWN");
            sm.setSourceId(godown.getGodownId());
            sm.setDestType("SHOP");
            sm.setDestId(shop.getShopId());
            sm.setCommodity(comm);
            sm.setQuantity(itemReq.getQuantity());
            sm.setReferenceDocType("DISPATCH");
            sm.setReferenceDocId(dispatchNum);
            sm.setCreatedBy(username);
            sm.setMovementTimestamp(LocalDateTime.now());
            stockMovementRepository.save(sm);
        }

        return dispatchRepository.save(savedDispatch);
    }

    @Transactional
    public Dispatch confirmReceipt(Long dispatchId, String username, String remarks) {
        Dispatch dispatch = getDispatchById(dispatchId);

        if (!"IN_TRANSIT".equalsIgnoreCase(dispatch.getDispatchStatus()) && !"DISPATCHED".equalsIgnoreCase(dispatch.getDispatchStatus())) {
            throw new BadRequestException("Dispatch is not in transit. Current status: " + dispatch.getDispatchStatus());
        }

        for (DispatchItem item : dispatch.getItems()) {
            item.setReceivedQuantity(item.getDispatchedQuantity());
            item.setDiscrepancyQuantity(BigDecimal.ZERO);

            // Increment Shop Stock
            ShopStock sStock = shopStockRepository
                .findByShop_ShopIdAndCommodity_CommodityId(dispatch.getShop().getShopId(), item.getCommodity().getCommodityId())
                .orElseGet(() -> {
                    ShopStock fresh = new ShopStock();
                    fresh.setShop(dispatch.getShop());
                    fresh.setCommodity(item.getCommodity());
                    fresh.setCurrentQuantity(BigDecimal.ZERO);
                    fresh.setOpeningMonthBalance(BigDecimal.ZERO);
                    fresh.setTotalReceivedMonth(BigDecimal.ZERO);
                    fresh.setTotalDistributedMonth(BigDecimal.ZERO);
                    fresh.setUpdatedAt(LocalDateTime.now());
                    return fresh;
                });

            sStock.setCurrentQuantity(sStock.getCurrentQuantity().add(item.getDispatchedQuantity()));
            sStock.setTotalReceivedMonth(sStock.getTotalReceivedMonth().add(item.getDispatchedQuantity()));
            sStock.setUpdatedAt(LocalDateTime.now());
            shopStockRepository.save(sStock);

            // Record immutable stock movement
            StockMovement sm = new StockMovement();
            sm.setMovementType("SHOP_RECEIPT");
            sm.setSourceType("GODOWN");
            sm.setSourceId(dispatch.getGodown().getGodownId());
            sm.setDestType("SHOP");
            sm.setDestId(dispatch.getShop().getShopId());
            sm.setCommodity(item.getCommodity());
            sm.setQuantity(item.getDispatchedQuantity());
            sm.setReferenceDocType("DISPATCH");
            sm.setReferenceDocId(dispatch.getDispatchNumber());
            sm.setCreatedBy(username);
            sm.setMovementTimestamp(LocalDateTime.now());
            stockMovementRepository.save(sm);

            // Synchronize with monthly Allotment fulfillment
            allotmentRepository.findByShop_ShopIdAndCommodity_CommodityIdAndAllotmentYearAndAllotmentMonth(
                dispatch.getShop().getShopId(), item.getCommodity().getCommodityId(), 
                LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue()
            ).ifPresent(allot -> {
                BigDecimal currentReceived = allot.getReceivedQuantity() != null ? allot.getReceivedQuantity() : BigDecimal.ZERO;
                allot.setReceivedQuantity(currentReceived.add(item.getDispatchedQuantity()));
                allotmentRepository.save(allot);
            });
        }

        dispatch.setDispatchStatus("RECEIVED");
        dispatch.setReceivedAt(LocalDateTime.now());
        dispatch.setReceivedBy(username);
        if (remarks != null) {
            dispatch.setRemarks(remarks);
        }

        return dispatchRepository.save(dispatch);
    }
}
