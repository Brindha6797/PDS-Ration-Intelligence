package com.wipro.pds.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DispatchItemRequest {
    @NotNull(message = "Commodity ID is required")
    private Long commodityId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Dispatched quantity must be greater than zero")
    private BigDecimal quantity;

    public DispatchItemRequest() {}
    public DispatchItemRequest(Long commodityId, BigDecimal quantity) {
        this.commodityId = commodityId;
        this.quantity = quantity;
    }

    public Long getCommodityId() { return commodityId; }
    public void setCommodityId(Long commodityId) { this.commodityId = commodityId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
