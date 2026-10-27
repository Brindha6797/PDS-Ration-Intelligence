-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 08: ORACLE DATABASE TRIGGERS
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;

PROMPT Creating Trigger: trg_family_member_count_sync...;

-- Trigger to sync total_family_members in CARD_HOLDERS (Non-mutating delta update)
-- Card starts with 1 (Head of Family). Each dependent family member added increments count.
CREATE OR REPLACE TRIGGER trg_family_member_count_sync
AFTER INSERT OR UPDATE OR DELETE ON family_members
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        IF :NEW.is_active = 'Y' AND UPPER(:NEW.relationship_to_head) <> 'SELF' THEN
            UPDATE card_holders
            SET total_family_members = total_family_members + 1,
                updated_at = SYSTIMESTAMP
            WHERE card_id = :NEW.card_id;
        END IF;
    ELSIF DELETING THEN
        IF :OLD.is_active = 'Y' AND UPPER(:OLD.relationship_to_head) <> 'SELF' THEN
            BEGIN
                UPDATE card_holders
                SET total_family_members = GREATEST(1, total_family_members - 1),
                    updated_at = SYSTIMESTAMP
                WHERE card_id = :OLD.card_id;
            EXCEPTION
                WHEN OTHERS THEN
                    -- Guard against mutating table error if the parent card is being cascade deleted
                    NULL;
            END;
        END IF;
    ELSIF UPDATING THEN
        IF UPPER(:NEW.relationship_to_head) <> 'SELF' THEN
            -- Dependent member deactivated ('Y' -> 'N')
            IF :OLD.is_active = 'Y' AND :NEW.is_active = 'N' THEN
                UPDATE card_holders
                SET total_family_members = GREATEST(1, total_family_members - 1),
                    updated_at = SYSTIMESTAMP
                WHERE card_id = :NEW.card_id;
            -- Dependent member reactivated ('N' -> 'Y')
            ELSIF :OLD.is_active = 'N' AND :NEW.is_active = 'Y' THEN
                UPDATE card_holders
                SET total_family_members = total_family_members + 1,
                    updated_at = SYSTIMESTAMP
                WHERE card_id = :NEW.card_id;
            END IF;
        END IF;
    END IF;
END;
/

PROMPT Creating Trigger: trg_complaint_status_history...;

-- Trigger to record status transitions in COMPLAINT_HISTORY
CREATE OR REPLACE TRIGGER trg_complaint_status_history
AFTER INSERT OR UPDATE ON complaints
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO complaint_history (
            history_id, complaint_id, previous_status, new_status,
            changed_by_user, action_taken, action_timestamp, comments
        ) VALUES (
            seq_complaint_history.NEXTVAL, :NEW.complaint_id, NULL, :NEW.status,
            'SYSTEM', 'Complaint registered with reference ' || :NEW.complaint_ref,
            SYSTIMESTAMP, 'Initial registration'
        );
    ELSIF UPDATING('status') AND (:OLD.status IS NULL OR :OLD.status <> :NEW.status) THEN
        INSERT INTO complaint_history (
            history_id, complaint_id, previous_status, new_status,
            changed_by_user, action_taken, action_timestamp, comments
        ) VALUES (
            seq_complaint_history.NEXTVAL, :NEW.complaint_id, :OLD.status, :NEW.status,
            'OFFICER_PANEL', 'Status updated from ' || :OLD.status || ' to ' || :NEW.status,
            SYSTIMESTAMP, :NEW.resolution_notes
        );
    END IF;
END;
/

PROMPT Creating Trigger: trg_audit_card_status...;

-- Trigger to log ration card lifecycle modifications
CREATE OR REPLACE TRIGGER trg_audit_card_status
AFTER UPDATE OF card_status ON card_holders
FOR EACH ROW
WHEN (OLD.card_status <> NEW.card_status)
BEGIN
    INSERT INTO audit_logs (
        log_id, entity_name, entity_id, action_type,
        performed_by, action_timestamp, old_values, new_values, status_result
    ) VALUES (
        seq_audit_logs.NEXTVAL, 'CARD_HOLDERS', TO_CHAR(:NEW.card_id), 'UPDATE',
        'SYSTEM_OFFICER', SYSTIMESTAMP,
        '{"cardStatus":"' || :OLD.card_status || '"}',
        '{"cardStatus":"' || :NEW.card_status || '"}',
        'SUCCESS'
    );
END;
/

PROMPT Creating Trigger: trg_audit_shop_status...;

-- Trigger to log shop license / status suspensions
CREATE OR REPLACE TRIGGER trg_audit_shop_status
AFTER UPDATE OF shop_status ON shops
FOR EACH ROW
WHEN (OLD.shop_status <> NEW.shop_status)
BEGIN
    INSERT INTO audit_logs (
        log_id, entity_name, entity_id, action_type,
        performed_by, action_timestamp, old_values, new_values, status_result
    ) VALUES (
        seq_audit_logs.NEXTVAL, 'SHOPS', TO_CHAR(:NEW.shop_id), 'UPDATE',
        'SYSTEM_OFFICER', SYSTIMESTAMP,
        '{"shopStatus":"' || :OLD.shop_status || '"}',
        '{"shopStatus":"' || :NEW.shop_status || '"}',
        'SUCCESS'
    );
END;
/

PROMPT Triggers created successfully.
