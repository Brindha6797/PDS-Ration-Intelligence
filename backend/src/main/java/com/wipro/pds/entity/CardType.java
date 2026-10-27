package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARD_TYPES")
public class CardType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_card_types")
    @SequenceGenerator(name = "seq_card_types", sequenceName = "seq_card_types", allocationSize = 1)
    @Column(name = "CARD_TYPE_ID")
    private Long cardTypeId;

    @Column(name = "TYPE_CODE", unique = true, nullable = false, length = 20)
    private String typeCode;

    @Column(name = "TYPE_NAME", nullable = false, length = 100)
    private String typeName;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "COLOR_CODE", length = 20)
    private String colorCode;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public CardType() {}

    public CardType(Long cardTypeId, String typeCode, String typeName) {
        this.cardTypeId = cardTypeId;
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public Long getCardTypeId() { return cardTypeId; }
    public void setCardTypeId(Long cardTypeId) { this.cardTypeId = cardTypeId; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
