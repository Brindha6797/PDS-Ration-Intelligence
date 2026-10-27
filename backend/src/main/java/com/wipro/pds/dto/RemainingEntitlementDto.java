package com.wipro.pds.dto;

import java.math.BigDecimal;

public class RemainingEntitlementDto {
    private Long commodityId;
    private String commodityCode;
    private String commodityName;
    private String measurementUnit;
    private BigDecimal entitledQuantity;
    private BigDecimal distributedQuantity;
    private BigDecimal remainingQuantity;
    private BigDecimal consumerRatePerUnit;
    private String status;

    public RemainingEntitlementDto() {}

    public Long getCommodityId() { return commodityId; }
    public void setCommodityId(Long commodityId) { this.commodityId = commodityId; }
    public String getCommodityCode() { return commodityCode; }
    public void setCommodityCode(String commodityCode) { this.commodityCode = commodityCode; }
    public String getCommodityName() { return commodityName; }
    public void setCommodityName(String commodityName) { this.commodityName = commodityName; }
    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }
    public BigDecimal getEntitledQuantity() { return entitledQuantity; }
    public void setEntitledQuantity(BigDecimal entitledQuantity) { this.entitledQuantity = entitledQuantity; }
    public BigDecimal getDistributedQuantity() { return distributedQuantity; }
    public void setDistributedQuantity(BigDecimal distributedQuantity) { this.distributedQuantity = distributedQuantity; }
    public BigDecimal getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; }
    public BigDecimal getConsumerRatePerUnit() { return consumerRatePerUnit; }
    public void setConsumerRatePerUnit(BigDecimal consumerRatePerUnit) { this.consumerRatePerUnit = consumerRatePerUnit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
