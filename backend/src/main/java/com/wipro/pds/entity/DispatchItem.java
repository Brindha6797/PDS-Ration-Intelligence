package com.wipro.pds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DISPATCH_ITEMS", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"DISPATCH_ID", "COMMODITY_ID"})
})
public class DispatchItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dispatch_items")
    @SequenceGenerator(name = "seq_dispatch_items", sequenceName = "seq_dispatch_items", allocationSize = 1)
    @Column(name = "DISPATCH_ITEM_ID")
    private Long dispatchItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPATCH_ID", nullable = false)
    @JsonIgnore
    private Dispatch dispatch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMMODITY_ID", nullable = false)
    private Commodity commodity;

    @Column(name = "DISPATCHED_QUANTITY", nullable = false, precision = 10, scale = 2)
    private BigDecimal dispatchedQuantity;

    @Column(name = "RECEIVED_QUANTITY", precision = 10, scale = 2)
    private BigDecimal receivedQuantity;

    @Column(name = "DISCREPANCY_QUANTITY", precision = 10, scale = 2)
    private BigDecimal discrepancyQuantity = BigDecimal.ZERO;

    @Column(name = "CONDITION_NOTES", length = 255)
    private String conditionNotes;

    public DispatchItem() {}

    public Long getDispatchItemId() { return dispatchItemId; }
    public void setDispatchItemId(Long dispatchItemId) { this.dispatchItemId = dispatchItemId; }
    public Dispatch getDispatch() { return dispatch; }
    public void setDispatch(Dispatch dispatch) { this.dispatch = dispatch; }
    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public BigDecimal getDispatchedQuantity() { return dispatchedQuantity; }
    public void setDispatchedQuantity(BigDecimal dispatchedQuantity) { this.dispatchedQuantity = dispatchedQuantity; }
    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }
    public BigDecimal getDiscrepancyQuantity() { return discrepancyQuantity; }
    public void setDiscrepancyQuantity(BigDecimal discrepancyQuantity) { this.discrepancyQuantity = discrepancyQuantity; }
    public String getConditionNotes() { return conditionNotes; }
    public void setConditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; }
}
