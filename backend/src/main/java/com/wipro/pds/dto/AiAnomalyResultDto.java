package com.wipro.pds.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiAnomalyResultDto {
    private String shopCode;
    private String shopName;
    private String commodityName;
    private BigDecimal recordedStock;
    private BigDecimal physicalStock;
    private BigDecimal varianceQuantity;
    private BigDecimal discrepancyPercentage;
    private String alertReason;
    private String riskIndicator; // LOW, MEDIUM, HIGH, CRITICAL
    private List<String> recommendedSteps;

    public AiAnomalyResultDto() {}

    public String getShopCode() { return shopCode; }
    public void setShopCode(String shopCode) { this.shopCode = shopCode; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getCommodityName() { return commodityName; }
    public void setCommodityName(String commodityName) { this.commodityName = commodityName; }
    public BigDecimal getRecordedStock() { return recordedStock; }
    public void setRecordedStock(BigDecimal recordedStock) { this.recordedStock = recordedStock; }
    public BigDecimal getPhysicalStock() { return physicalStock; }
    public void setPhysicalStock(BigDecimal physicalStock) { this.physicalStock = physicalStock; }
    public BigDecimal getVarianceQuantity() { return varianceQuantity; }
    public void setVarianceQuantity(BigDecimal varianceQuantity) { this.varianceQuantity = varianceQuantity; }
    public BigDecimal getDiscrepancyPercentage() { return discrepancyPercentage; }
    public void setDiscrepancyPercentage(BigDecimal discrepancyPercentage) { this.discrepancyPercentage = discrepancyPercentage; }
    public String getAlertReason() { return alertReason; }
    public void setAlertReason(String alertReason) { this.alertReason = alertReason; }
    public String getRiskIndicator() { return riskIndicator; }
    public void setRiskIndicator(String riskIndicator) { this.riskIndicator = riskIndicator; }
    public List<String> getRecommendedSteps() { return recommendedSteps; }
    public void setRecommendedSteps(List<String> recommendedSteps) { this.recommendedSteps = recommendedSteps; }
}
