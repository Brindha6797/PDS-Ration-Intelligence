package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ENTITLEMENTS", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"CARD_ID", "COMMODITY_ID", "ALLOTMENT_YEAR", "ALLOTMENT_MONTH"})
})
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_entitlements")
    @SequenceGenerator(name = "seq_entitlements", sequenceName = "seq_entitlements", allocationSize = 1)
    @Column(name = "ENTITLEMENT_ID")
    private Long entitlementId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARD_ID", nullable = false)
    private CardHolder cardHolder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "ALLOTMENT_YEAR", nullable = false)
    private Integer allotmentYear;

    @Column(name = "ALLOTMENT_MONTH", nullable = false)
    private Integer allotmentMonth;

    @Column(name = "ENTITLED_QUANTITY", nullable = false, precision = 8, scale = 2)
    private BigDecimal entitledQuantity;

    @Column(name = "DISTRIBUTED_QUANTITY", nullable = false, precision = 8, scale = 2)
    private BigDecimal distributedQuantity = BigDecimal.ZERO;

    @Column(name = "REMAINING_QUANTITY", nullable = false, precision = 8, scale = 2)
    private BigDecimal remainingQuantity;

    @Column(name = "LAST_DISTRIBUTED_AT")
    private LocalDateTime lastDistributedAt;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status = "AVAILABLE";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Entitlement() {}

    public Long getEntitlementId() { return entitlementId; }
    public void setEntitlementId(Long entitlementId) { this.entitlementId = entitlementId; }
    public CardHolder getCardHolder() { return cardHolder; }
    public void setCardHolder(CardHolder cardHolder) { this.cardHolder = cardHolder; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public Integer getAllotmentYear() { return allotmentYear; }
    public void setAllotmentYear(Integer allotmentYear) { this.allotmentYear = allotmentYear; }
    public Integer getAllotmentMonth() { return allotmentMonth; }
    public void setAllotmentMonth(Integer allotmentMonth) { this.allotmentMonth = allotmentMonth; }
    public BigDecimal getEntitledQuantity() { return entitledQuantity; }
    public void setEntitledQuantity(BigDecimal entitledQuantity) { this.entitledQuantity = entitledQuantity; }
    public BigDecimal getDistributedQuantity() { return distributedQuantity; }
    public void setDistributedQuantity(BigDecimal distributedQuantity) { this.distributedQuantity = distributedQuantity; }
    public BigDecimal getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; }
    public LocalDateTime getLastDistributedAt() { return lastDistributedAt; }
    public void setLastDistributedAt(LocalDateTime lastDistributedAt) { this.lastDistributedAt = lastDistributedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
