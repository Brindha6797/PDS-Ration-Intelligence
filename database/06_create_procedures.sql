-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 06: STORED PROCEDURES (ORACLE PL/SQL)
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;

PROMPT Creating Procedure: prc_generate_entitlements_for_month...;

CREATE OR REPLACE PROCEDURE prc_generate_entitlements_for_month (
    p_year            IN NUMBER,
    p_month           IN NUMBER,
    p_created_by      IN VARCHAR2,
    p_processed_count OUT NUMBER
)
IS
    v_count NUMBER := 0;
    v_qty   NUMBER(8,2);
BEGIN
    IF p_year IS NULL OR p_year < 2000 OR p_year > 2100 THEN
        RAISE_APPLICATION_ERROR(-20021, 'Invalid year. Must be between 2000 and 2100.');
    END IF;

    IF p_month < 1 OR p_month > 12 THEN
        RAISE_APPLICATION_ERROR(-20020, 'Invalid month. Must be between 1 and 12.');
    END IF;

    FOR c_card IN (
        SELECT card_id, card_type_id, total_family_members
        FROM card_holders
        WHERE card_status = 'ACTIVE'
    ) LOOP
        FOR c_comm IN (
            SELECT commodity_id
            FROM commodities
            WHERE is_active = 'Y'
        ) LOOP
            v_qty := monthly_entitlement(
                p_card_type_id => c_card.card_type_id,
                p_family_size  => c_card.total_family_members,
                p_commodity_id => c_comm.commodity_id,
                p_as_of_date   => TO_DATE(p_year || '-' || LPAD(p_month, 2, '0') || '-01', 'YYYY-MM-DD')
            );

            IF v_qty > 0 THEN
                BEGIN
                    INSERT INTO entitlements (
                        entitlement_id, card_id, commodity_id, allotment_year, allotment_month,
                        entitled_quantity, distributed_quantity, remaining_quantity,
                        status, created_at, updated_at
                    ) VALUES (
                        seq_entitlements.NEXTVAL, c_card.card_id, c_comm.commodity_id, p_year, p_month,
                        v_qty, 0.00, v_qty,
                        'AVAILABLE', SYSTIMESTAMP, SYSTIMESTAMP
                    );
                    v_count := v_count + 1;
                EXCEPTION
                    WHEN DUP_VAL_ON_INDEX THEN
                        -- Already generated for this card, commodity, and month; preserve existing distribution record
                        NULL;
                END;
            END IF;
        END LOOP;
    END LOOP;

    p_processed_count := v_count;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20029, 'Error generating entitlements: ' || SQLERRM);
END prc_generate_entitlements_for_month;
/

PROMPT Creating Procedure: prc_generate_shop_allotments...;

CREATE OR REPLACE PROCEDURE prc_generate_shop_allotments (
    p_year            IN NUMBER,
    p_month           IN NUMBER,
    p_created_by      IN VARCHAR2,
    p_allotment_count OUT NUMBER
)
IS
    v_count     NUMBER := 0;
    v_ent_count NUMBER := 0;
    v_has_ents  NUMBER := 0;
BEGIN
    IF p_year IS NULL OR p_year < 2000 OR p_year > 2100 THEN
        RAISE_APPLICATION_ERROR(-20031, 'Invalid year. Must be between 2000 and 2100.');
    END IF;

    IF p_month < 1 OR p_month > 12 THEN
        RAISE_APPLICATION_ERROR(-20030, 'Invalid month. Must be between 1 and 12.');
    END IF;

    -- Self-healing: verify if entitlements exist for period; if not, generate them automatically
    SELECT COUNT(*) INTO v_has_ents
    FROM entitlements
    WHERE allotment_year = p_year AND allotment_month = p_month;

    IF v_has_ents = 0 THEN
        prc_generate_entitlements_for_month(p_year, p_month, p_created_by, v_ent_count);
    END IF;

    -- Aggregate active cardholder entitlements for each shop and commodity
    FOR r_agg IN (
        SELECT ch.assigned_shop_id AS shop_id,
               e.commodity_id,
               SUM(e.entitled_quantity) AS total_needed
        FROM entitlements e
        JOIN card_holders ch ON e.card_id = ch.card_id
        WHERE e.allotment_year = p_year
          AND e.allotment_month = p_month
          AND ch.card_status = 'ACTIVE'
        GROUP BY ch.assigned_shop_id, e.commodity_id
    ) LOOP
        BEGIN
            INSERT INTO allotments (
                allotment_id, shop_id, commodity_id, allotment_year, allotment_month,
                allocated_quantity, dispatched_quantity, received_quantity,
                allotment_status, created_at, updated_at
            ) VALUES (
                seq_allotments.NEXTVAL, r_agg.shop_id, r_agg.commodity_id, p_year, p_month,
                r_agg.total_needed, 0.00, 0.00,
                'DRAFT', SYSTIMESTAMP, SYSTIMESTAMP
            );
            v_count := v_count + 1;
        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                -- If allotment row exists and is still in DRAFT, update requirement
                UPDATE allotments
                SET allocated_quantity = r_agg.total_needed,
                    updated_at = SYSTIMESTAMP
                WHERE shop_id = r_agg.shop_id
                  AND commodity_id = r_agg.commodity_id
                  AND allotment_year = p_year
                  AND allotment_month = p_month
                  AND allotment_status = 'DRAFT';
                v_count := v_count + SQL%ROWCOUNT;
        END;
    END LOOP;

    p_allotment_count := v_count;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20039, 'Error generating allotments: ' || SQLERRM);
END prc_generate_shop_allotments;
/

PROMPT Creating Procedure: prc_approve_allotment...;

CREATE OR REPLACE PROCEDURE prc_approve_allotment (
    p_allotment_id IN NUMBER,
    p_officer_id   IN NUMBER
)
IS
    v_status VARCHAR2(25);
BEGIN
    SELECT allotment_status INTO v_status
    FROM allotments
    WHERE allotment_id = p_allotment_id
    FOR UPDATE;

    IF v_status <> 'DRAFT' THEN
        RAISE_APPLICATION_ERROR(-20035, 'Only DRAFT allotments can be approved. Current status: ' || v_status);
    END IF;

    UPDATE allotments
    SET allotment_status = 'APPROVED',
        approved_by_user_id = p_officer_id,
        approved_at = SYSTIMESTAMP,
        updated_at = SYSTIMESTAMP
    WHERE allotment_id = p_allotment_id;

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20036, 'Allotment record not found for ID: ' || p_allotment_id);
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END prc_approve_allotment;
/

PROMPT Creating Procedure: prc_confirm_shop_receipt...;

CREATE OR REPLACE PROCEDURE prc_confirm_shop_receipt (
    p_dispatch_id  IN NUMBER,
    p_received_by  IN VARCHAR2,
    p_remarks      IN VARCHAR2 DEFAULT NULL
)
IS
    v_status     VARCHAR2(20);
    v_shop_id    NUMBER;
    v_godown_id  NUMBER;
    v_disp_num   VARCHAR2(30);
BEGIN
    -- Lock dispatch header
    SELECT dispatch_status, shop_id, godown_id, dispatch_number
    INTO v_status, v_shop_id, v_godown_id, v_disp_num
    FROM dispatches
    WHERE dispatch_id = p_dispatch_id
    FOR UPDATE;

    IF v_status <> 'IN_TRANSIT' AND v_status <> 'DISPATCHED' THEN
        RAISE_APPLICATION_ERROR(-20040, 'Dispatch cannot be received. Current status: ' || v_status);
    END IF;

    -- Process each item in the dispatch
    FOR item IN (
        SELECT commodity_id, dispatched_quantity
        FROM dispatch_items
        WHERE dispatch_id = p_dispatch_id
    ) LOOP
        -- Set received quantity = dispatched quantity
        UPDATE dispatch_items
        SET received_quantity = item.dispatched_quantity,
            discrepancy_quantity = 0.00
        WHERE dispatch_id = p_dispatch_id
          AND commodity_id = item.commodity_id;

        -- Update or insert shop_stock
        MERGE INTO shop_stock ss
        USING (SELECT v_shop_id AS shop_id, item.commodity_id AS commodity_id FROM DUAL) src
        ON (ss.shop_id = src.shop_id AND ss.commodity_id = src.commodity_id)
        WHEN MATCHED THEN
            UPDATE SET 
                ss.current_quantity = ss.current_quantity + item.dispatched_quantity,
                ss.total_received_month = ss.total_received_month + item.dispatched_quantity,
                ss.updated_at = SYSTIMESTAMP
        WHEN NOT MATCHED THEN
            INSERT (
                stock_id, shop_id, commodity_id, current_quantity,
                opening_month_balance, total_received_month, total_distributed_month,
                updated_at
            ) VALUES (
                seq_shop_stock.NEXTVAL, v_shop_id, item.commodity_id, item.dispatched_quantity,
                0.00, item.dispatched_quantity, 0.00,
                SYSTIMESTAMP
            );

        -- Record immutable stock movement
        INSERT INTO stock_movements (
            movement_id, movement_type, source_type, source_id,
            dest_type, dest_id, commodity_id, quantity,
            reference_doc_type, reference_doc_id, created_by, movement_timestamp
        ) VALUES (
            seq_stock_movements.NEXTVAL, 'SHOP_RECEIPT', 'GODOWN', v_godown_id,
            'SHOP', v_shop_id, item.commodity_id, item.dispatched_quantity,
            'DISPATCH', v_disp_num, p_received_by, SYSTIMESTAMP
        );

        -- Update corresponding allotment record received quantity
        UPDATE allotments
        SET received_quantity = received_quantity + item.dispatched_quantity,
            allotment_status = CASE 
                WHEN (received_quantity + item.dispatched_quantity) >= allocated_quantity THEN 'COMPLETED'
                ELSE 'PARTIALLY_DISPATCHED' END,
            updated_at = SYSTIMESTAMP
        WHERE shop_id = v_shop_id
          AND commodity_id = item.commodity_id
          AND allotment_year = TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY'))
          AND allotment_month = TO_NUMBER(TO_CHAR(SYSDATE, 'MM'));
    END LOOP;

    -- Update dispatch status
    UPDATE dispatches
    SET dispatch_status = 'RECEIVED',
        received_at = SYSTIMESTAMP,
        received_by = p_received_by,
        remarks = NVL(p_remarks, remarks)
    WHERE dispatch_id = p_dispatch_id;

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20041, 'Dispatch ID not found: ' || p_dispatch_id);
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END prc_confirm_shop_receipt;
/

PROMPT Creating Procedure: prc_record_stock_reconciliation...;

CREATE OR REPLACE PROCEDURE prc_record_stock_reconciliation (
    p_shop_id         IN NUMBER,
    p_commodity_id    IN NUMBER,
    p_physical_count  IN NUMBER,
    p_officer_id      IN NUMBER,
    p_remarks         IN VARCHAR2,
    p_recon_id        OUT NUMBER
)
IS
    v_recorded_stock  NUMBER(10,2) := 0.00;
    v_expected_stock  NUMBER(10,2) := 0.00;
    v_variance        NUMBER(10,2) := 0.00;
    v_discrepancy_pct NUMBER(5,2) := 0.00;
    v_status          VARCHAR2(25);
    v_recon_id        NUMBER;
BEGIN
    IF p_physical_count < 0 THEN
        RAISE_APPLICATION_ERROR(-20050, 'Physical count cannot be negative.');
    END IF;

    -- Fetch current recorded stock
    SELECT NVL(current_quantity, 0.00)
    INTO v_recorded_stock
    FROM shop_stock
    WHERE shop_id = p_shop_id
      AND commodity_id = p_commodity_id;

    -- Calculate expected stock from movements
    v_expected_stock := fn_calculate_expected_stock(p_shop_id, p_commodity_id, SYSDATE);

    -- Compute variance = Physical count - System recorded
    v_variance := p_physical_count - v_recorded_stock;

    IF v_recorded_stock > 0 THEN
        v_discrepancy_pct := ROUND((ABS(v_variance) / v_recorded_stock) * 100, 2);
    ELSE
        IF v_variance <> 0 THEN
            v_discrepancy_pct := 100.00;
        ELSE
            v_discrepancy_pct := 0.00;
        END IF;
    END IF;

    IF v_variance = 0 THEN
        v_status := 'MATCHED';
    ELSE
        v_status := 'PENDING_REVIEW';
    END IF;

    v_recon_id := seq_stock_reconciliation.NEXTVAL;

    INSERT INTO stock_reconciliation (
        recon_id, shop_id, commodity_id, recon_date,
        recorded_system_stock, calculated_expected_stock, physical_counted_stock,
        variance_quantity, discrepancy_percentage, investigation_status,
        inspecting_officer_id, officer_remarks, created_at
    ) VALUES (
        v_recon_id, p_shop_id, p_commodity_id, TRUNC(SYSDATE),
        v_recorded_stock, v_expected_stock, p_physical_count,
        v_variance, v_discrepancy_pct, v_status,
        p_officer_id, p_remarks, SYSTIMESTAMP
    );

    UPDATE shop_stock
    SET last_reconciliation_date = TRUNC(SYSDATE),
        updated_at = SYSTIMESTAMP
    WHERE shop_id = p_shop_id
      AND commodity_id = p_commodity_id;

    p_recon_id := v_recon_id;
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20051, 'Shop or Commodity stock entry not found.');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END prc_record_stock_reconciliation;
/

PROMPT Procedures created successfully.
