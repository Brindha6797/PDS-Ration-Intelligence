package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_RECONCILIATION")
public class StockReconciliation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_stock_reconciliation")
    @SequenceGenerator(name = "seq_stock_reconciliation", sequenceName = "seq_stock_reconciliation", allocationSize = 1)
    @Column(name = "RECON_ID")
    private Long reconId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "RECON_DATE", nullable = false)
    private LocalDate reconDate = LocalDate.now();

    @Column(name = "RECORDED_SYSTEM_STOCK", nullable = false, precision = 10, scale = 2)
    private BigDecimal recordedSystemStock;

    @Column(name = "CALCULATED_EXPECTED_STOCK", nullable = false, precision = 10, scale = 2)
    private BigDecimal calculatedExpectedStock;

    @Column(name = "PHYSICAL_COUNTED_STOCK", nullable = false, precision = 10, scale = 2)
    private BigDecimal physicalCountedStock;

    @Column(name = "VARIANCE_QUANTITY", nullable = false, precision = 10, scale = 2)
    private BigDecimal varianceQuantity;

    @Column(name = "DISCREPANCY_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal discrepancyPercentage;

    @Column(name = "INVESTIGATION_STATUS", nullable = false, length = 25)
    private String investigationStatus = "PENDING_REVIEW";

    @Column(name = "INSPECTING_OFFICER_ID", nullable = false)
    private Long inspectingOfficerId;

    @Column(name = "OFFICER_REMARKS", length = 500)
    private String officerRemarks;

    @Column(name = "ADJUSTMENT_MOVEMENT_ID")
    private Long adjustmentMovementId;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StockReconciliation() {}

    public Long getReconId() { return reconId; }
    public void setReconId(Long reconId) { this.reconId = reconId; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public LocalDate getReconDate() { return reconDate; }
    public void setReconDate(LocalDate reconDate) { this.reconDate = reconDate; }
    public BigDecimal getRecordedSystemStock() { return recordedSystemStock; }
    public void setRecordedSystemStock(BigDecimal recordedSystemStock) { this.recordedSystemStock = recordedSystemStock; }
    public BigDecimal getCalculatedExpectedStock() { return calculatedExpectedStock; }
    public void setCalculatedExpectedStock(BigDecimal calculatedExpectedStock) { this.calculatedExpectedStock = calculatedExpectedStock; }
    public BigDecimal getPhysicalCountedStock() { return physicalCountedStock; }
    public void setPhysicalCountedStock(BigDecimal physicalCountedStock) { this.physicalCountedStock = physicalCountedStock; }
    public BigDecimal getVarianceQuantity() { return varianceQuantity; }
    public void setVarianceQuantity(BigDecimal varianceQuantity) { this.varianceQuantity = varianceQuantity; }
    public BigDecimal getDiscrepancyPercentage() { return discrepancyPercentage; }
    public void setDiscrepancyPercentage(BigDecimal discrepancyPercentage) { this.discrepancyPercentage = discrepancyPercentage; }
    public String getInvestigationStatus() { return investigationStatus; }
    public void setInvestigationStatus(String investigationStatus) { this.investigationStatus = investigationStatus; }
    public Long getInspectingOfficerId() { return inspectingOfficerId; }
    public void setInspectingOfficerId(Long inspectingOfficerId) { this.inspectingOfficerId = inspectingOfficerId; }
    public String getOfficerRemarks() { return officerRemarks; }
    public void setOfficerRemarks(String officerRemarks) { this.officerRemarks = officerRemarks; }
    public Long getAdjustmentMovementId() { return adjustmentMovementId; }
    public void setAdjustmentMovementId(Long adjustmentMovementId) { this.adjustmentMovementId = adjustmentMovementId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
