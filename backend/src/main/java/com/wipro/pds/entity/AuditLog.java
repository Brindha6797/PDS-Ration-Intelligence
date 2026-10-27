package com.wipro.pds.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUDIT_LOGS")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_audit_logs")
    @SequenceGenerator(name = "seq_audit_logs", sequenceName = "seq_audit_logs", allocationSize = 1)
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "ENTITY_NAME", nullable = false, length = 50)
    private String entityName;

    @Column(name = "ENTITY_ID", nullable = false, length = 50)
    private String entityId;

    @Column(name = "ACTION_TYPE", nullable = false, length = 20)
    private String actionType;

    @Column(name = "PERFORMED_BY", nullable = false, length = 50)
    private String performedBy;

    @Column(name = "ACTION_TIMESTAMP", nullable = false)
    private LocalDateTime actionTimestamp = LocalDateTime.now();

    @Lob
    @Column(name = "OLD_VALUES")
    private String oldValues;

    @Lob
    @Column(name = "NEW_VALUES")
    private String newValues;

    @Column(name = "CLIENT_IP", length = 45)
    private String clientIp;

    @Column(name = "STATUS_RESULT", nullable = false, length = 20)
    private String statusResult = "SUCCESS";

    public AuditLog() {}

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; }
    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }
    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getStatusResult() { return statusResult; }
    public void setStatusResult(String statusResult) { this.statusResult = statusResult; }
}
