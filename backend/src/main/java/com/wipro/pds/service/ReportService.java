package com.wipro.pds.service;

import com.wipro.pds.dto.DashboardStatsDto;
import com.wipro.pds.entity.*;
import com.wipro.pds.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReportService {

    private final CardHolderRepository cardHolderRepository;
    private final ShopRepository shopRepository;
    private final ShopStockRepository shopStockRepository;
    private final AllotmentRepository allotmentRepository;
    private final SaleRepository saleRepository;
    private final ComplaintRepository complaintRepository;
    private final StockReconciliationRepository reconciliationRepository;
    private final GodownStockRepository godownStockRepository;
    private final AuditLogRepository auditLogRepository;

    public ReportService(CardHolderRepository cardHolderRepository,
                         ShopRepository shopRepository,
                         ShopStockRepository shopStockRepository,
                         AllotmentRepository allotmentRepository,
                         SaleRepository saleRepository,
                         ComplaintRepository complaintRepository,
                         StockReconciliationRepository reconciliationRepository,
                         GodownStockRepository godownStockRepository,
                         AuditLogRepository auditLogRepository) {
        this.cardHolderRepository = cardHolderRepository;
        this.shopRepository = shopRepository;
        this.shopStockRepository = shopStockRepository;
        this.allotmentRepository = allotmentRepository;
        this.saleRepository = saleRepository;
        this.complaintRepository = complaintRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.godownStockRepository = godownStockRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto dto = new DashboardStatsDto();

        dto.setTotalActiveCards(cardHolderRepository.count());
        dto.setTotalShops(shopRepository.count());

        // Count low-stock shops
        long lowStockCount = shopStockRepository.findAll().stream()
            .filter(s -> s.getCurrentQuantity().compareTo(s.getCommodity().getBufferStockThreshold()) < 0)
            .map(s -> s.getShop().getShopId())
            .distinct()
            .count();
        dto.setLowStockShops(lowStockCount);

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        long pendingAllot = allotmentRepository.findByAllotmentYearAndAllotmentMonth(currentYear, currentMonth).stream()
            .filter(a -> "DRAFT".equalsIgnoreCase(a.getAllotmentStatus()))
            .count();
        dto.setPendingAllotments(pendingAllot);

        List<Sale> allSales = saleRepository.findAll();
        dto.setCompletedSalesCount(allSales.size());

        BigDecimal revenue = allSales.stream()
            .map(Sale::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalSalesRevenue(revenue);

        long openComplaints = complaintRepository.findAll().stream()
            .filter(c -> !"RESOLVED".equalsIgnoreCase(c.getStatus()) && !"REJECTED".equalsIgnoreCase(c.getStatus()))
            .count();
        dto.setOpenComplaints(openComplaints);

        long pendingRecon = reconciliationRepository.findAll().stream()
            .filter(r -> "PENDING_REVIEW".equalsIgnoreCase(r.getInvestigationStatus()) || "INVESTIGATION_OPENED".equalsIgnoreCase(r.getInvestigationStatus()))
            .count();
        dto.setUnresolvedReconciliations(pendingRecon);

        // Godown Rice & Wheat stock in MT
        BigDecimal riceStockKg = godownStockRepository.findAll().stream()
            .filter(gs -> gs.getCommodity().getCommodityCode().contains("RICE"))
            .map(GodownStock::getCurrentQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setGodownRiceStockMt(riceStockKg.divide(new BigDecimal("1000.00"), 2, RoundingMode.HALF_UP));

        BigDecimal wheatStockKg = godownStockRepository.findAll().stream()
            .filter(gs -> gs.getCommodity().getCommodityCode().contains("WHEAT"))
            .map(GodownStock::getCurrentQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setGodownWheatStockMt(wheatStockKg.divide(new BigDecimal("1000.00"), 2, RoundingMode.HALF_UP));

        return dto;
    }

    public List<Map<String, Object>> getMonthlySalesMatrix(int year, int month) {
        List<Shop> shops = shopRepository.findAll();
        List<Sale> sales = saleRepository.findAll().stream()
            .filter(s -> s.getSaleYear() == year && s.getSaleMonth() == month && "COMPLETED".equalsIgnoreCase(s.getSaleStatus()))
            .toList();

        List<Map<String, Object>> matrix = new ArrayList<>();
        for (Shop s : shops) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("shopId", s.getShopId());
            row.put("shopCode", s.getShopCode());
            row.put("shopName", s.getShopName());
            row.put("wardVillage", s.getWardVillage());

            BigDecimal riceTotal = BigDecimal.ZERO;
            BigDecimal wheatTotal = BigDecimal.ZERO;
            BigDecimal sugarTotal = BigDecimal.ZERO;
            BigDecimal oilTotal = BigDecimal.ZERO;
            BigDecimal dalTotal = BigDecimal.ZERO;
            BigDecimal revenue = BigDecimal.ZERO;
            int billCount = 0;

            for (Sale sale : sales) {
                if (sale.getShop().getShopId().equals(s.getShopId())) {
                    billCount++;
                    revenue = revenue.add(sale.getTotalAmount());
                    for (SaleItem item : sale.getItems()) {
                        String code = item.getCommodity().getCommodityCode();
                        if (code.contains("RICE")) riceTotal = riceTotal.add(item.getQuantity());
                        else if (code.contains("WHEAT")) wheatTotal = wheatTotal.add(item.getQuantity());
                        else if (code.contains("SUGAR")) sugarTotal = sugarTotal.add(item.getQuantity());
                        else if (code.contains("OIL")) oilTotal = oilTotal.add(item.getQuantity());
                        else if (code.contains("DAL")) dalTotal = dalTotal.add(item.getQuantity());
                    }
                }
            }

            row.put("billCount", billCount);
            row.put("riceDistributedKg", riceTotal);
            row.put("wheatDistributedKg", wheatTotal);
            row.put("sugarDistributedKg", sugarTotal);
            row.put("oilDistributedPkts", oilTotal);
            row.put("dalDistributedKg", dalTotal);
            row.put("revenue", revenue);
            matrix.add(row);
        }
        return matrix;
    }

    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findAll().stream()
            .sorted((a, b) -> b.getActionTimestamp().compareTo(a.getActionTimestamp()))
            .limit(100)
            .toList();
    }
}
