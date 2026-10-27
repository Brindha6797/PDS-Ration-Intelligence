package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CARD_HOLDERS")
public class CardHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_card_holders")
    @SequenceGenerator(name = "seq_card_holders", sequenceName = "seq_card_holders", allocationSize = 1)
    @Column(name = "CARD_ID")
    private Long cardId;

    @Column(name = "CARD_NUMBER", unique = true, nullable = false, length = 30)
    private String cardNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARD_TYPE_ID", nullable = false)
    private CardType cardType;

    @Column(name = "HEAD_OF_FAMILY_NAME", nullable = false, length = 150)
    private String headOfFamilyName;

    @Column(name = "AADHAAR_HASH", nullable = false, length = 64)
    private String aadhaarHash;

    @Column(name = "AADHAAR_LAST4", nullable = false, length = 4)
    private String aadhaarLast4;

    @Column(name = "MOBILE_NUMBER", length = 15)
    private String mobileNumber;

    @Column(name = "ADDRESS_LINE1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "TALUK_NAME", nullable = false, length = 100)
    private String talukName;

    @Column(name = "DISTRICT_NAME", nullable = false, length = 100)
    private String districtName;

    @Column(name = "PINCODE", nullable = false, length = 10)
    private String pincode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ASSIGNED_SHOP_ID", nullable = false)
    private Shop assignedShop;

    @Column(name = "TOTAL_FAMILY_MEMBERS", nullable = false)
    private Integer totalFamilyMembers = 1;

    @Column(name = "ANNUAL_INCOME", precision = 10, scale = 2)
    private BigDecimal annualIncome;

    @Column(name = "CARD_STATUS", nullable = false, length = 20)
    private String cardStatus = "ACTIVE";

    @Column(name = "EFFECTIVE_DATE", nullable = false)
    private LocalDate effectiveDate = LocalDate.now();

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "cardHolder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FamilyMember> members = new ArrayList<>();

    public CardHolder() {}

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    public String getHeadOfFamilyName() { return headOfFamilyName; }
    public void setHeadOfFamilyName(String headOfFamilyName) { this.headOfFamilyName = headOfFamilyName; }
    public String getAadhaarHash() { return aadhaarHash; }
    public void setAadhaarHash(String aadhaarHash) { this.aadhaarHash = aadhaarHash; }
    public String getAadhaarLast4() { return aadhaarLast4; }
    public void setAadhaarLast4(String aadhaarLast4) { this.aadhaarLast4 = aadhaarLast4; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getTalukName() { return talukName; }
    public void setTalukName(String talukName) { this.talukName = talukName; }
    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public Shop getAssignedShop() { return assignedShop; }
    public void setAssignedShop(Shop assignedShop) { this.assignedShop = assignedShop; }
    public Integer getTotalFamilyMembers() { return totalFamilyMembers; }
    public void setTotalFamilyMembers(Integer totalFamilyMembers) { this.totalFamilyMembers = totalFamilyMembers; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }
    public String getCardStatus() { return cardStatus; }
    public void setCardStatus(String cardStatus) { this.cardStatus = cardStatus; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<FamilyMember> getMembers() { return members; }
    public void setMembers(List<FamilyMember> members) { this.members = members; }
}
