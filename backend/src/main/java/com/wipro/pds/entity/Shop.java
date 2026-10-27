package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SHOPS")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_shops")
    @SequenceGenerator(name = "seq_shops", sequenceName = "seq_shops", allocationSize = 1)
    @Column(name = "SHOP_ID")
    private Long shopId;

    @Column(name = "SHOP_CODE", unique = true, nullable = false, length = 20)
    private String shopCode;

    @Column(name = "SHOP_NAME", nullable = false, length = 150)
    private String shopName;

    @Column(name = "LICENSE_NUMBER", unique = true, nullable = false, length = 50)
    private String licenseNumber;

    @Column(name = "OPERATOR_NAME", nullable = false, length = 150)
    private String operatorName;

    @Column(name = "CONTACT_NUMBER", nullable = false, length = 15)
    private String contactNumber;

    @Column(name = "TALUK_NAME", nullable = false, length = 100)
    private String talukName;

    @Column(name = "WARD_VILLAGE", nullable = false, length = 100)
    private String wardVillage;

    @Column(name = "ADDRESS", nullable = false, length = 255)
    private String address;

    @Column(name = "LATITUDE", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "SHOP_STATUS", nullable = false, length = 20)
    private String shopStatus = "ACTIVE";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Shop() {}

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getShopCode() { return shopCode; }
    public void setShopCode(String shopCode) { this.shopCode = shopCode; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getTalukName() { return talukName; }
    public void setTalukName(String talukName) { this.talukName = talukName; }
    public String getWardVillage() { return wardVillage; }
    public void setWardVillage(String wardVillage) { this.wardVillage = wardVillage; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getShopStatus() { return shopStatus; }
    public void setShopStatus(String shopStatus) { this.shopStatus = shopStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
