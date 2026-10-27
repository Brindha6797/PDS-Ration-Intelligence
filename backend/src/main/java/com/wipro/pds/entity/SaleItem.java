package com.wipro.pds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SALE_ITEMS", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"SALE_ID", "COMMODITY_ID"})
})
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sale_items")
    @SequenceGenerator(name = "seq_sale_items", sequenceName = "seq_sale_items", allocationSize = 1)
    @Column(name = "SALE_ITEM_ID")
    private Long saleItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALE_ID", nullable = false)
    @JsonIgnore
    private Sale sale;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "QUANTITY", nullable = false, precision = 8, scale = 2)
    private BigDecimal quantity;

    @Column(name = "UNIT_RATE", nullable = false, precision = 8, scale = 2)
    private BigDecimal unitRate;

    @Column(name = "TOTAL_PRICE", nullable = false, precision = 8, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "SUBSIDY_AMOUNT", precision = 8, scale = 2)
    private BigDecimal subsidyAmount = BigDecimal.ZERO;

    public SaleItem() {}

    public Long getSaleItemId() { return saleItemId; }
    public void setSaleItemId(Long saleItemId) { this.saleItemId = saleItemId; }
    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getUnitRate() { return unitRate; }
    public void setUnitRate(BigDecimal unitRate) { this.unitRate = unitRate; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public BigDecimal getSubsidyAmount() { return subsidyAmount; }
    public void setSubsidyAmount(BigDecimal subsidyAmount) { this.subsidyAmount = subsidyAmount; }
}
