package com.wipro.pds.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleResponse {
    private Long saleId;
    private String billNumber;
    private String shopCode;
    private String cardNumber;
    private String headOfFamilyName;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private String paymentMode;
    private String authMode;
    private List<SaleItemResponse> items;

    public SaleResponse() {}

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    public String getShopCode() { return shopCode; }
    public void setShopCode(String shopCode) { this.shopCode = shopCode; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getHeadOfFamilyName() { return headOfFamilyName; }
    public void setHeadOfFamilyName(String headOfFamilyName) { this.headOfFamilyName = headOfFamilyName; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getAuthMode() { return authMode; }
    public void setAuthMode(String authMode) { this.authMode = authMode; }
    public List<SaleItemResponse> getItems() { return items; }
    public void setItems(List<SaleItemResponse> items) { this.items = items; }
}
