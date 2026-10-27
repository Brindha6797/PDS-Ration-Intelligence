package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FAMILY_MEMBERS")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_family_members")
    @SequenceGenerator(name = "seq_family_members", sequenceName = "seq_family_members", allocationSize = 1)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_ID", nullable = false)
    private CardHolder cardHolder;

    @Column(name = "MEMBER_NAME", nullable = false, length = 150)
    private String memberName;

    @Column(name = "RELATIONSHIP_TO_HEAD", nullable = false, length = 50)
    private String relationshipToHead;

    @Column(name = "GENDER", nullable = false, length = 1)
    private String gender;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "AADHAAR_HASH", nullable = false, length = 64)
    private String aadhaarHash;

    @Column(name = "AADHAAR_LAST4", nullable = false, length = 4)
    private String aadhaarLast4;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public FamilyMember() {}

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public CardHolder getCardHolder() { return cardHolder; }
    public void setCardHolder(CardHolder cardHolder) { this.cardHolder = cardHolder; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getRelationshipToHead() { return relationshipToHead; }
    public void setRelationshipToHead(String relationshipToHead) { this.relationshipToHead = relationshipToHead; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getAadhaarHash() { return aadhaarHash; }
    public void setAadhaarHash(String aadhaarHash) { this.aadhaarHash = aadhaarHash; }
    public String getAadhaarLast4() { return aadhaarLast4; }
    public void setAadhaarLast4(String aadhaarLast4) { this.aadhaarLast4 = aadhaarLast4; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
