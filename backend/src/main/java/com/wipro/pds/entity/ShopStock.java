package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SHOP_STOCK", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"SHOP_ID", "COMMODITY_ID"})
})
public class ShopStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_shop_stock")
    @SequenceGenerator(name = "seq_shop_stock", sequenceName = "seq_shop_stock", allocationSize = 1)
    @Column(name = "STOCK_ID")
    private Long stockId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "CURRENT_QUANTITY", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentQuantity = BigDecimal.ZERO;

    @Column(name = "OPENING_MONTH_BALANCE", nullable = false, precision = 10, scale = 2)
    private BigDecimal openingMonthBalance = BigDecimal.ZERO;

    @Column(name = "TOTAL_RECEIVED_MONTH", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalReceivedMonth = BigDecimal.ZERO;

    @Column(name = "TOTAL_DISTRIBUTED_MONTH", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDistributedMonth = BigDecimal.ZERO;

    @Column(name = "LAST_SALE_DATE")
    private LocalDateTime lastSaleDate;

    @Column(name = "LAST_RECONCILIATION_DATE")
    private LocalDate lastReconciliationDate;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ShopStock() {}

    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public BigDecimal getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(BigDecimal currentQuantity) { this.currentQuantity = currentQuantity; }
    public BigDecimal getOpeningMonthBalance() { return openingMonthBalance; }
    public void setOpeningMonthBalance(BigDecimal openingMonthBalance) { this.openingMonthBalance = openingMonthBalance; }
    public BigDecimal getTotalReceivedMonth() { return totalReceivedMonth; }
    public void setTotalReceivedMonth(BigDecimal totalReceivedMonth) { this.totalReceivedMonth = totalReceivedMonth; }
    public BigDecimal getTotalDistributedMonth() { return totalDistributedMonth; }
    public void setTotalDistributedMonth(BigDecimal totalDistributedMonth) { this.totalDistributedMonth = totalDistributedMonth; }
    public LocalDateTime getLastSaleDate() { return lastSaleDate; }
    public void setLastSaleDate(LocalDateTime lastSaleDate) { this.lastSaleDate = lastSaleDate; }
    public LocalDate getLastReconciliationDate() { return lastReconciliationDate; }
    public void setLastReconciliationDate(LocalDate lastReconciliationDate) { this.lastReconciliationDate = lastReconciliationDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
