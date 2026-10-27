package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DISPATCHES")
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dispatches")
    @SequenceGenerator(name = "seq_dispatches", sequenceName = "seq_dispatches", allocationSize = 1)
    @Column(name = "DISPATCH_ID")
    private Long dispatchId;

    @Column(name = "DISPATCH_NUMBER", unique = true, nullable = false, length = 30)
    private String dispatchNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GODOWN_ID", nullable = false)
    private Godown godown;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @Column(name = "VEHICLE_NUMBER", nullable = false, length = 30)
    private String vehicleNumber;

    @Column(name = "DRIVER_NAME", nullable = false, length = 100)
    private String driverName;

    @Column(name = "DRIVER_PHONE", length = 15)
    private String driverPhone;

    @Column(name = "DISPATCH_STATUS", nullable = false, length = 20)
    private String dispatchStatus = "IN_TRANSIT";

    @Column(name = "DISPATCHED_AT", nullable = false)
    private LocalDateTime dispatchedAt;

    @Column(name = "RECEIVED_AT")
    private LocalDateTime receivedAt;

    @Column(name = "DISPATCHED_BY", nullable = false, length = 50)
    private String dispatchedBy;

    @Column(name = "RECEIVED_BY", length = 50)
    private String receivedBy;

    @Column(name = "REMARKS", length = 255)
    private String remarks;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DispatchItem> items = new ArrayList<>();

    public Dispatch() {}

    public Long getDispatchId() { return dispatchId; }
    public void setDispatchId(Long dispatchId) { this.dispatchId = dispatchId; }
    public String getDispatchNumber() { return dispatchNumber; }
    public void setDispatchNumber(String dispatchNumber) { this.dispatchNumber = dispatchNumber; }
    public Godown getGodown() { return godown; }
    public void setGodown(Godown godown) { this.godown = godown; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }
    public String getDispatchStatus() { return dispatchStatus; }
    public void setDispatchStatus(String dispatchStatus) { this.dispatchStatus = dispatchStatus; }
    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public void setDispatchedAt(LocalDateTime dispatchedAt) { this.dispatchedAt = dispatchedAt; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
    public String getDispatchedBy() { return dispatchedBy; }
    public void setDispatchedBy(String dispatchedBy) { this.dispatchedBy = dispatchedBy; }
    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<DispatchItem> getItems() { return items; }
    public void setItems(List<DispatchItem> items) { this.items = items; }
}
