package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMPLAINTS")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_complaints")
    @SequenceGenerator(name = "seq_complaints", sequenceName = "seq_complaints", allocationSize = 1)
    @Column(name = "COMPLAINT_ID")
    private Long complaintId;

    @Column(name = "COMPLAINT_REF", unique = true, nullable = false, length = 30)
    private String complaintRef;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CARD_ID")
    private CardHolder cardHolder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID")
    private Shop shop;

    @Column(name = "CATEGORY", nullable = false, length = 50)
    private String category;

    @Column(name = "DESCRIPTION", nullable = false, length = 2000)
    private String description;

    @Column(name = "LANGUAGE", nullable = false, length = 10)
    private String language = "en";

    @Column(name = "PRIORITY", nullable = false, length = 20)
    private String priority = "MEDIUM";

    @Column(name = "STATUS", nullable = false, length = 25)
    private String status = "REGISTERED";

    @Column(name = "ASSIGNED_OFFICER_ID")
    private Long assignedOfficerId;

    @Column(name = "RESOLUTION_NOTES", length = 2000)
    private String resolutionNotes;

    @Column(name = "RESOLVED_AT")
    private LocalDateTime resolvedAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComplaintHistory> history = new ArrayList<>();

    public Complaint() {}

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    public String getComplaintRef() { return complaintRef; }
    public void setComplaintRef(String complaintRef) { this.complaintRef = complaintRef; }
    public CardHolder getCardHolder() { return cardHolder; }
    public void setCardHolder(CardHolder cardHolder) { this.cardHolder = cardHolder; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getAssignedOfficerId() { return assignedOfficerId; }
    public void setAssignedOfficerId(Long assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<ComplaintHistory> getHistory() { return history; }
    public void setHistory(List<ComplaintHistory> history) { this.history = history; }
}
