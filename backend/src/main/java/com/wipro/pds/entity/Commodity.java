package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMMODITIES")
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_commodities")
    @SequenceGenerator(name = "seq_commodities", sequenceName = "seq_commodities", allocationSize = 1)
    @Column(name = "COMMODITY_ID")
    private Long commodityId;

    @Column(name = "COMMODITY_CODE", unique = true, nullable = false, length = 20)
    private String commodityCode;

    @Column(name = "COMMODITY_NAME", nullable = false, length = 100)
    private String commodityName;

    @Column(name = "MEASUREMENT_UNIT", nullable = false, length = 20)
    private String measurementUnit;

    @Column(name = "CONSUMER_RATE_PER_UNIT", nullable = false, precision = 8, scale = 2)
    private BigDecimal consumerRatePerUnit = BigDecimal.ZERO;

    @Column(name = "GOVERNMENT_SUBSIDY_PER_UNIT", nullable = false, precision = 8, scale = 2)
    private BigDecimal governmentSubsidyPerUnit = BigDecimal.ZERO;

    @Column(name = "BUFFER_STOCK_THRESHOLD", nullable = false, precision = 10, scale = 2)
    private BigDecimal bufferStockThreshold = new BigDecimal("100.00");

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Commodity() {}

    public Long getCommodityId() { return commodityId; }
    public void setCommodityId(Long commodityId) { this.commodityId = commodityId; }
    public String getCommodityCode() { return commodityCode; }
    public void setCommodityCode(String commodityCode) { this.commodityCode = commodityCode; }
    public String getCommodityName() { return commodityName; }
    public void setCommodityName(String commodityName) { this.commodityName = commodityName; }
    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }
    public BigDecimal getConsumerRatePerUnit() { return consumerRatePerUnit; }
    public void setConsumerRatePerUnit(BigDecimal consumerRatePerUnit) { this.consumerRatePerUnit = consumerRatePerUnit; }
    public BigDecimal getGovernmentSubsidyPerUnit() { return governmentSubsidyPerUnit; }
    public void setGovernmentSubsidyPerUnit(BigDecimal governmentSubsidyPerUnit) { this.governmentSubsidyPerUnit = governmentSubsidyPerUnit; }
    public BigDecimal getBufferStockThreshold() { return bufferStockThreshold; }
    public void setBufferStockThreshold(BigDecimal bufferStockThreshold) { this.bufferStockThreshold = bufferStockThreshold; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
