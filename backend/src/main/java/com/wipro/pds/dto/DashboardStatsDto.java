package com.wipro.pds.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private long totalActiveCards;
    private long totalShops;
    private long lowStockShops;
    private long pendingAllotments;
    private long completedSalesCount;
    private BigDecimal totalSalesRevenue;
    private long openComplaints;
    private long unresolvedReconciliations;
    private BigDecimal godownRiceStockMt;
    private BigDecimal godownWheatStockMt;

    public DashboardStatsDto() {}

    public long getTotalActiveCards() { return totalActiveCards; }
    public void setTotalActiveCards(long totalActiveCards) { this.totalActiveCards = totalActiveCards; }
    public long getTotalShops() { return totalShops; }
    public void setTotalShops(long totalShops) { this.totalShops = totalShops; }
    public long getLowStockShops() { return lowStockShops; }
    public void setLowStockShops(long lowStockShops) { this.lowStockShops = lowStockShops; }
    public long getPendingAllotments() { return pendingAllotments; }
    public void setPendingAllotments(long pendingAllotments) { this.pendingAllotments = pendingAllotments; }
    public long getCompletedSalesCount() { return completedSalesCount; }
    public void setCompletedSalesCount(long completedSalesCount) { this.completedSalesCount = completedSalesCount; }
    public BigDecimal getTotalSalesRevenue() { return totalSalesRevenue; }
    public void setTotalSalesRevenue(BigDecimal totalSalesRevenue) { this.totalSalesRevenue = totalSalesRevenue; }
    public long getOpenComplaints() { return openComplaints; }
    public void setOpenComplaints(long openComplaints) { this.openComplaints = openComplaints; }
    public long getUnresolvedReconciliations() { return unresolvedReconciliations; }
    public void setUnresolvedReconciliations(long unresolvedReconciliations) { this.unresolvedReconciliations = unresolvedReconciliations; }
    public BigDecimal getGodownRiceStockMt() { return godownRiceStockMt; }
    public void setGodownRiceStockMt(BigDecimal godownRiceStockMt) { this.godownRiceStockMt = godownRiceStockMt; }
    public BigDecimal getGodownWheatStockMt() { return godownWheatStockMt; }
    public void setGodownWheatStockMt(BigDecimal godownWheatStockMt) { this.godownWheatStockMt = godownWheatStockMt; }
}
