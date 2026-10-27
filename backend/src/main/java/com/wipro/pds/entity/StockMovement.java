package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_MOVEMENTS")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_stock_movements")
    @SequenceGenerator(name = "seq_stock_movements", sequenceName = "seq_stock_movements", allocationSize = 1)
    @Column(name = "MOVEMENT_ID")
    private Long movementId;

    @Column(name = "MOVEMENT_TYPE", nullable = false, length = 30)
    private String movementType;

    @Column(name = "SOURCE_TYPE", length = 20)
    private String sourceType;

    @Column(name = "SOURCE_ID")
    private Long sourceId;

    @Column(name = "DEST_TYPE", length = 20)
    private String destType;

    @Column(name = "DEST_ID")
    private Long destId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "QUANTITY", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "REFERENCE_DOC_TYPE", length = 30)
    private String referenceDocType;

    @Column(name = "REFERENCE_DOC_ID", length = 50)
    private String referenceDocId;

    @Column(name = "CREATED_BY", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "MOVEMENT_TIMESTAMP", nullable = false)
    private LocalDateTime movementTimestamp = LocalDateTime.now();

    public StockMovement() {}

    public Long getMovementId() { return movementId; }
    public void setMovementId(Long movementId) { this.movementId = movementId; }
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
    public String getDestType() { return destType; }
    public void setDestType(String destType) { this.destType = destType; }
    public Long getDestId() { return destId; }
    public void setDestId(Long destId) { this.destId = destId; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getReferenceDocType() { return referenceDocType; }
    public void setReferenceDocType(String referenceDocType) { this.referenceDocType = referenceDocType; }
    public String getReferenceDocId() { return referenceDocId; }
    public void setReferenceDocId(String referenceDocId) { this.referenceDocId = referenceDocId; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getMovementTimestamp() { return movementTimestamp; }
    public void setMovementTimestamp(LocalDateTime movementTimestamp) { this.movementTimestamp = movementTimestamp; }
}
