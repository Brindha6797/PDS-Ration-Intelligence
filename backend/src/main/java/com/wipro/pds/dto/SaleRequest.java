package com.wipro.pds.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SaleRequest {
    @NotNull(message = "Shop ID is required")
    private Long shopId;

    @NotNull(message = "Card ID is required")
    private Long cardId;

    private String paymentMode = "CASH";
    private String authMode = "AADHAAR_OTP";

    @NotEmpty(message = "Sale items cannot be empty")
    private List<SaleItemRequest> items;

    public SaleRequest() {}

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getAuthMode() { return authMode; }
    public void setAuthMode(String authMode) { this.authMode = authMode; }
    public List<SaleItemRequest> getItems() { return items; }
    public void setItems(List<SaleItemRequest> items) { this.items = items; }
}
