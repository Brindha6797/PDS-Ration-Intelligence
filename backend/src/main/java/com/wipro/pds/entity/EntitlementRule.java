package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ENTITLEMENT_RULES")
public class EntitlementRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_entitlement_rules")
    @SequenceGenerator(name = "seq_entitlement_rules", sequenceName = "seq_entitlement_rules", allocationSize = 1)
    @Column(name = "RULE_ID")
    private Long ruleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARD_TYPE_ID", nullable = false)
    private CardType cardType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "CALC_METHOD", nullable = false, length = 30)
    private String calcMethod;

    @Column(name = "BASE_QUANTITY", nullable = false, precision = 8, scale = 2)
    private BigDecimal baseQuantity = BigDecimal.ZERO;

    @Column(name = "PER_MEMBER_QUANTITY", nullable = false, precision = 8, scale = 2)
    private BigDecimal perMemberQuantity = BigDecimal.ZERO;

    @Column(name = "MIN_FAMILY_SIZE")
    private Integer minFamilySize = 1;

    @Column(name = "MAX_FAMILY_SIZE")
    private Integer maxFamilySize;

    @Column(name = "MAX_QUANTITY_CEILING", precision = 8, scale = 2)
    private BigDecimal maxQuantityCeiling;

    @Column(name = "EFFECTIVE_START_DATE", nullable = false)
    private LocalDate effectiveStartDate;

    @Column(name = "EFFECTIVE_END_DATE")
    private LocalDate effectiveEndDate;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public EntitlementRule() {}

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public String getCalcMethod() { return calcMethod; }
    public void setCalcMethod(String calcMethod) { this.calcMethod = calcMethod; }
    public BigDecimal getBaseQuantity() { return baseQuantity; }
    public void setBaseQuantity(BigDecimal baseQuantity) { this.baseQuantity = baseQuantity; }
    public BigDecimal getPerMemberQuantity() { return perMemberQuantity; }
    public void setPerMemberQuantity(BigDecimal perMemberQuantity) { this.perMemberQuantity = perMemberQuantity; }
    public Integer getMinFamilySize() { return minFamilySize; }
    public void setMinFamilySize(Integer minFamilySize) { this.minFamilySize = minFamilySize; }
    public Integer getMaxFamilySize() { return maxFamilySize; }
    public void setMaxFamilySize(Integer maxFamilySize) { this.maxFamilySize = maxFamilySize; }
    public BigDecimal getMaxQuantityCeiling() { return maxQuantityCeiling; }
    public void setMaxQuantityCeiling(BigDecimal maxQuantityCeiling) { this.maxQuantityCeiling = maxQuantityCeiling; }
    public LocalDate getEffectiveStartDate() { return effectiveStartDate; }
    public void setEffectiveStartDate(LocalDate effectiveStartDate) { this.effectiveStartDate = effectiveStartDate; }
    public LocalDate getEffectiveEndDate() { return effectiveEndDate; }
    public void setEffectiveEndDate(LocalDate effectiveEndDate) { this.effectiveEndDate = effectiveEndDate; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
