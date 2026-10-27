package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GODOWNS")
public class Godown {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_godowns")
    @SequenceGenerator(name = "seq_godowns", sequenceName = "seq_godowns", allocationSize = 1)
    @Column(name = "GODOWN_ID")
    private Long godownId;

    @Column(name = "GODOWN_CODE", unique = true, nullable = false, length = 20)
    private String godownCode;

    @Column(name = "GODOWN_NAME", nullable = false, length = 150)
    private String godownName;

    @Column(name = "TALUK_NAME", nullable = false, length = 100)
    private String talukName;

    @Column(name = "STORAGE_CAPACITY_MT", nullable = false, precision = 10, scale = 2)
    private BigDecimal storageCapacityMt;

    @Column(name = "MANAGER_NAME", nullable = false, length = 150)
    private String managerName;

    @Column(name = "CONTACT_NUMBER", nullable = false, length = 15)
    private String contactNumber;

    @Column(name = "ADDRESS", nullable = false, length = 255)
    private String address;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Godown() {}

    public Long getGodownId() { return godownId; }
    public void setGodownId(Long godownId) { this.godownId = godownId; }
    public String getGodownCode() { return godownCode; }
    public void setGodownCode(String godownCode) { this.godownCode = godownCode; }
    public String getGodownName() { return godownName; }
    public void setGodownName(String godownName) { this.godownName = godownName; }
    public String getTalukName() { return talukName; }
    public void setTalukName(String talukName) { this.talukName = talukName; }
    public BigDecimal getStorageCapacityMt() { return storageCapacityMt; }
    public void setStorageCapacityMt(BigDecimal storageCapacityMt) { this.storageCapacityMt = storageCapacityMt; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
