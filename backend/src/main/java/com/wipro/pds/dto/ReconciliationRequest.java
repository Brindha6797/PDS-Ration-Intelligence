package com.wipro.pds.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ReconciliationRequest {
    @NotNull(message = "Shop ID is required")
    private Long shopId;

    @NotNull(message = "Commodity ID is required")
    private Long commodityId;

    @NotNull(message = "Physical count is required")
    @DecimalMin(value = "0.00", message = "Physical count cannot be negative")
    private BigDecimal physicalCount;

    private String remarks;

    public ReconciliationRequest() {}

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getCommodityId() { return commodityId; }
    public void setCommodityId(Long commodityId) { this.commodityId = commodityId; }
    public BigDecimal getPhysicalCount() { return physicalCount; }
    public void setPhysicalCount(BigDecimal physicalCount) { this.physicalCount = physicalCount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
