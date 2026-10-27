package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GODOWN_STOCK", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"GODOWN_ID", "COMMODITY_ID"})
})
public class GodownStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_godown_stock")
    @SequenceGenerator(name = "seq_godown_stock", sequenceName = "seq_godown_stock", allocationSize = 1)
    @Column(name = "GODOWN_STOCK_ID")
    private Long godownStockId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GODOWN_ID", nullable = false)
    private Godown godown;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "CURRENT_QUANTITY", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentQuantity = BigDecimal.ZERO;

    @Column(name = "RESERVED_QUANTITY", nullable = false, precision = 12, scale = 2)
    private BigDecimal reservedQuantity = BigDecimal.ZERO;

    @Column(name = "LAST_REPLENISHED_DATE")
    private LocalDate lastReplenishedDate;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public GodownStock() {}

    public Long getGodownStockId() { return godownStockId; }
    public void setGodownStockId(Long godownStockId) { this.godownStockId = godownStockId; }
    public Godown getGodown() { return godown; }
    public void setGodown(Godown godown) { this.godown = godown; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public BigDecimal getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(BigDecimal currentQuantity) { this.currentQuantity = currentQuantity; }
    public BigDecimal getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(BigDecimal reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    public LocalDate getLastReplenishedDate() { return lastReplenishedDate; }
    public void setLastReplenishedDate(LocalDate lastReplenishedDate) { this.lastReplenishedDate = lastReplenishedDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
