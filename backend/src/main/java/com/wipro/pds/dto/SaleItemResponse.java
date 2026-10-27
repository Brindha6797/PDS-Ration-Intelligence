package com.wipro.pds.dto;

import java.math.BigDecimal;

public class SaleItemResponse {
    private Long commodityId;
    private String commodityName;
    private String measurementUnit;
    private BigDecimal quantity;
    private BigDecimal unitRate;
    private BigDecimal totalPrice;
    private BigDecimal subsidyAmount;

    public SaleItemResponse() {}

    public Long getCommodityId() { return commodityId; }
    public void setCommodityId(Long commodityId) { this.commodityId = commodityId; }
    public String getCommodityName() { return commodityName; }
    public void setCommodityName(String commodityName) { this.commodityName = commodityName; }
    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getUnitRate() { return unitRate; }
    public void setUnitRate(BigDecimal unitRate) { this.unitRate = unitRate; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public BigDecimal getSubsidyAmount() { return subsidyAmount; }
    public void setSubsidyAmount(BigDecimal subsidyAmount) { this.subsidyAmount = subsidyAmount; }
}
