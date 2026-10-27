package com.wipro.pds.service;

import com.wipro.pds.entity.*;
import com.wipro.pds.exception.BadRequestException;
import com.wipro.pds.exception.ResourceNotFoundException;
import com.wipro.pds.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AllotmentService {

    private final AllotmentRepository allotmentRepository;
    private final EntitlementRepository entitlementRepository;
    private final ShopRepository shopRepository;
    private final CommodityRepository commodityRepository;
    private final EntitlementService entitlementService;

    public AllotmentService(AllotmentRepository allotmentRepository,
                            EntitlementRepository entitlementRepository,
                            ShopRepository shopRepository,
                            CommodityRepository commodityRepository,
                            EntitlementService entitlementService) {
        this.allotmentRepository = allotmentRepository;
        this.entitlementRepository = entitlementRepository;
        this.shopRepository = shopRepository;
        this.commodityRepository = commodityRepository;
        this.entitlementService = entitlementService;
    }

    public List<Allotment> getAllotments(Integer year, Integer month, Long shopId) {
        if (shopId != null) {
            return allotmentRepository.findByShop_ShopIdAndAllotmentYearAndAllotmentMonth(shopId, year, month);
        }
        return allotmentRepository.findByAllotmentYearAndAllotmentMonth(year, month);
    }

    @Transactional
    public int calculateMonthlyAllotments(int year, int month) {
        // First ensure entitlements exist for this month
        entitlementService.generateMonthlyEntitlements(year, month);

        List<Shop> shops = shopRepository.findByShopStatus("ACTIVE");
        List<Commodity> commodities = commodityRepository.findByIsActive("Y");

        int count = 0;
        for (Shop shop : shops) {
            List<Entitlement> shopEntitlements = entitlementRepository.findByShopAndPeriod(shop.getShopId(), year, month);
            Map<Long, BigDecimal> commodityTotals = new HashMap<>();

            for (Entitlement e : shopEntitlements) {
                Long commId = e.getCommodity().getCommodityId();
                commodityTotals.put(commId, commodityTotals.getOrDefault(commId, BigDecimal.ZERO).add(e.getEntitledQuantity()));
            }

            for (Commodity comm : commodities) {
                BigDecimal needed = commodityTotals.getOrDefault(comm.getCommodityId(), BigDecimal.ZERO);
                if (needed.compareTo(BigDecimal.ZERO) > 0) {
                    Optional<Allotment> existing = allotmentRepository
                        .findByShop_ShopIdAndCommodity_CommodityIdAndAllotmentYearAndAllotmentMonth(
                            shop.getShopId(), comm.getCommodityId(), year, month);

                    if (existing.isPresent()) {
                        Allotment a = existing.get();
                        if ("DRAFT".equalsIgnoreCase(a.getAllotmentStatus())) {
                            a.setAllocatedQuantity(needed);
                            a.setUpdatedAt(LocalDateTime.now());
                            allotmentRepository.save(a);
                        }
                    } else {
                        Allotment a = new Allotment();
                        a.setShop(shop);
                        a.setCommodity(comm);
                        a.setAllotmentYear(year);
                        a.setAllotmentMonth(month);
                        a.setAllocatedQuantity(needed);
                        a.setDispatchedQuantity(BigDecimal.ZERO);
                        a.setReceivedQuantity(BigDecimal.ZERO);
                        a.setAllotmentStatus("DRAFT");
                        a.setCreatedAt(LocalDateTime.now());
                        a.setUpdatedAt(LocalDateTime.now());
                        allotmentRepository.save(a);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Transactional
    public Allotment approveAllotment(Long allotmentId, Long officerUserId) {
        Allotment allotment = allotmentRepository.findById(allotmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Allotment not found with ID: " + allotmentId));

        if (!"DRAFT".equalsIgnoreCase(allotment.getAllotmentStatus())) {
            throw new BadRequestException("Only DRAFT allotments can be approved. Current status: " + allotment.getAllotmentStatus());
        }

        allotment.setAllotmentStatus("APPROVED");
        allotment.setApprovedByUserId(officerUserId);
        allotment.setApprovedAt(LocalDateTime.now());
        allotment.setUpdatedAt(LocalDateTime.now());

        return allotmentRepository.save(allotment);
    }
}
