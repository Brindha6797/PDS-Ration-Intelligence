package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SALES")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sales")
    @SequenceGenerator(name = "seq_sales", sequenceName = "seq_sales", allocationSize = 1)
    @Column(name = "SALE_ID")
    private Long saleId;

    @Column(name = "BILL_NUMBER", unique = true, nullable = false, length = 35)
    private String billNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARD_ID", nullable = false)
    private CardHolder cardHolder;

    @Column(name = "SALE_DATE", nullable = false)
    private LocalDateTime saleDate = LocalDateTime.now();

    @Column(name = "SALE_MONTH", nullable = false)
    private Integer saleMonth;

    @Column(name = "SALE_YEAR", nullable = false)
    private Integer saleYear;

    @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 8, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "PAYMENT_MODE", nullable = false, length = 20)
    private String paymentMode = "CASH";

    @Column(name = "AUTH_MODE", nullable = false, length = 20)
    private String authMode = "AADHAAR_OTP";

    @Column(name = "OPERATOR_USER_ID", nullable = false)
    private Long operatorUserId;

    @Column(name = "SALE_STATUS", nullable = false, length = 20)
    private String saleStatus = "COMPLETED";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {}

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public CardHolder getCardHolder() { return cardHolder; }
    public void setCardHolder(CardHolder cardHolder) { this.cardHolder = cardHolder; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    public Integer getSaleMonth() { return saleMonth; }
    public void setSaleMonth(Integer saleMonth) { this.saleMonth = saleMonth; }
    public Integer getSaleYear() { return saleYear; }
    public void setSaleYear(Integer saleYear) { this.saleYear = saleYear; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getAuthMode() { return authMode; }
    public void setAuthMode(String authMode) { this.authMode = authMode; }
    public Long getOperatorUserId() { return operatorUserId; }
    public void setOperatorUserId(Long operatorUserId) { this.operatorUserId = operatorUserId; }
    public String getSaleStatus() { return saleStatus; }
    public void setSaleStatus(String saleStatus) { this.saleStatus = saleStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
}
