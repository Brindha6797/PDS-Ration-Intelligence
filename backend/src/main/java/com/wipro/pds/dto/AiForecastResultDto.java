package com.wipro.pds.dto;

import java.math.BigDecimal;

public class AiForecastResultDto {
    private String shopCode;
    private String shopName;
    private String commodityName;
    private String forecastPeriod;
    private BigDecimal estimatedDemand;
    private BigDecimal availableStock;
    private BigDecimal dailyRunRate;
    private BigDecimal estimatedDaysRemaining;
    private String shortageRiskLevel; // SAFE, MODERATE, HIGH, CRITICAL
    private BigDecimal recommendedReplenishment;
    private String explanation;

    public AiForecastResultDto() {}

    public String getShopCode() { return shopCode; }
    public void setShopCode(String shopCode) { this.shopCode = shopCode; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getCommodityName() { return commodityName; }
    public void setCommodityName(String commodityName) { this.commodityName = commodityName; }
    public String getForecastPeriod() { return forecastPeriod; }
    public void setForecastPeriod(String forecastPeriod) { this.forecastPeriod = forecastPeriod; }
    public BigDecimal getEstimatedDemand() { return estimatedDemand; }
    public void setEstimatedDemand(BigDecimal estimatedDemand) { this.estimatedDemand = estimatedDemand; }
    public BigDecimal getAvailableStock() { return availableStock; }
    public void setAvailableStock(BigDecimal availableStock) { this.availableStock = availableStock; }
    public BigDecimal getDailyRunRate() { return dailyRunRate; }
    public void setDailyRunRate(BigDecimal dailyRunRate) { this.dailyRunRate = dailyRunRate; }
    public BigDecimal getEstimatedDaysRemaining() { return estimatedDaysRemaining; }
    public void setEstimatedDaysRemaining(BigDecimal estimatedDaysRemaining) { this.estimatedDaysRemaining = estimatedDaysRemaining; }
    public String getShortageRiskLevel() { return shortageRiskLevel; }
    public void setShortageRiskLevel(String shortageRiskLevel) { this.shortageRiskLevel = shortageRiskLevel; }
    public BigDecimal getRecommendedReplenishment() { return recommendedReplenishment; }
    public void setRecommendedReplenishment(BigDecimal recommendedReplenishment) { this.recommendedReplenishment = recommendedReplenishment; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
