package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ALLOTMENTS", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"SHOP_ID", "COMMODITY_ID", "ALLOTMENT_YEAR", "ALLOTMENT_MONTH"})
})
public class Allotment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_allotments")
    @SequenceGenerator(name = "seq_allotments", sequenceName = "seq_allotments", allocationSize = 1)
    @Column(name = "ALLOTMENT_ID")
    private Long allotmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "ALLOTMENT_YEAR", nullable = false)
    private Integer allotmentYear;

    @Column(name = "ALLOTMENT_MONTH", nullable = false)
    private Integer allotmentMonth;

    @Column(name = "ALLOCATED_QUANTITY", nullable = false, precision = 10, scale = 2)
    private BigDecimal allocatedQuantity;

    @Column(name = "DISPATCHED_QUANTITY", nullable = false, precision = 10, scale = 2)
    private BigDecimal dispatchedQuantity = BigDecimal.ZERO;

    @Column(name = "RECEIVED_QUANTITY", nullable = false, precision = 10, scale = 2)
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    @Column(name = "ALLOTMENT_STATUS", nullable = false, length = 25)
    private String allotmentStatus = "DRAFT";

    @Column(name = "APPROVED_BY_USER_ID")
    private Long approvedByUserId;

    @Column(name = "APPROVED_AT")
    private LocalDateTime approvedAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Allotment() {}

    public Long getAllotmentId() { return allotmentId; }
    public void setAllotmentId(Long allotmentId) { this.allotmentId = allotmentId; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public Integer getAllotmentYear() { return allotmentYear; }
    public void setAllotmentYear(Integer allotmentYear) { this.allotmentYear = allotmentYear; }
    public Integer getAllotmentMonth() { return allotmentMonth; }
    public void setAllotmentMonth(Integer allotmentMonth) { this.allotmentMonth = allotmentMonth; }
    public BigDecimal getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(BigDecimal allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
    public BigDecimal getDispatchedQuantity() { return dispatchedQuantity; }
    public void setDispatchedQuantity(BigDecimal dispatchedQuantity) { this.dispatchedQuantity = dispatchedQuantity; }
    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }
    public String getAllotmentStatus() { return allotmentStatus; }
    public void setAllotmentStatus(String allotmentStatus) { this.allotmentStatus = allotmentStatus; }
    public Long getApprovedByUserId() { return approvedByUserId; }
    public void setApprovedByUserId(Long approvedByUserId) { this.approvedByUserId = approvedByUserId; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
