package com.wipro.pds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COMPLAINT_HISTORY")
public class ComplaintHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_complaint_history")
    @SequenceGenerator(name = "seq_complaint_history", sequenceName = "seq_complaint_history", allocationSize = 1)
    @Column(name = "HISTORY_ID")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPLAINT_ID", nullable = false)
    @JsonIgnore
    private Complaint complaint;

    @Column(name = "PREVIOUS_STATUS", length = 25)
    private String previousStatus;

    @Column(name = "NEW_STATUS", nullable = false, length = 25)
    private String newStatus;

    @Column(name = "CHANGED_BY_USER", nullable = false, length = 50)
    private String changedByUser;

    @Column(name = "ACTION_TAKEN", nullable = false, length = 255)
    private String actionTaken;

    @Column(name = "ACTION_TIMESTAMP", nullable = false)
    private LocalDateTime actionTimestamp = LocalDateTime.now();

    @Column(name = "COMMENTS", length = 1000)
    private String comments;

    public ComplaintHistory() {}

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }
    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public String getChangedByUser() { return changedByUser; }
    public void setChangedByUser(String changedByUser) { this.changedByUser = changedByUser; }
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    public LocalDateTime getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
