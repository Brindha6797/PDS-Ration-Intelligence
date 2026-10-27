-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 11: PL/SQL COMPREHENSIVE TEST SUITE & EXCEPTION HARNESS
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON SIZE UNLIMITED;

PROMPT ================================================================;
PROMPT EXECUTING PL/SQL AUTOMATED TEST SUITE;
PROMPT ================================================================;

-- TEST 1: ENTITLEMENT CALCULATION FUNCTION (monthly_entitlement)
DECLARE
    v_qty NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 1: Entitlement Calculation Rules ---');
    
    -- Case 1.1: AAY Rice (Fixed 35kg)
    v_qty := monthly_entitlement(p_card_type_id => 1, p_family_size => 4, p_commodity_id => 1);
    IF v_qty = 35.00 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: AAY Rice Quota = ' || v_qty || ' KG (Expected: 35.00)');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: AAY Rice Quota = ' || v_qty || ' (Expected: 35.00)');
    END IF;

    -- Case 1.2: PHH Rice (5kg/member, 3 members = 15kg)
    v_qty := monthly_entitlement(p_card_type_id => 2, p_family_size => 3, p_commodity_id => 1);
    IF v_qty = 15.00 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: PHH Rice Quota (3 members) = ' || v_qty || ' KG (Expected: 15.00)');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: PHH Rice Quota = ' || v_qty || ' (Expected: 15.00)');
    END IF;

    -- Case 1.3: PHH Rice Ceiling Enforcement (5kg/member, 6 members = 30kg, but ceiling is 20kg)
    v_qty := monthly_entitlement(p_card_type_id => 2, p_family_size => 6, p_commodity_id => 1);
    IF v_qty = 20.00 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: PHH Rice Ceiling Enforced = ' || v_qty || ' KG (Expected: 20.00)');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: PHH Rice Ceiling = ' || v_qty || ' (Expected: 20.00)');
    END IF;

    -- Case 1.4: Missing Rule Handling (Commodity #99 does not exist)
    v_qty := monthly_entitlement(p_card_type_id => 1, p_family_size => 3, p_commodity_id => 99);
    IF v_qty = 0.00 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Missing Rule Handled gracefully, returned 0.00');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: Missing Rule should return 0.00, returned: ' || v_qty);
    END IF;

    -- Case 1.5: PHH Rice Statutory Floor Enforcement (1 member * 5kg = 5kg, floor is 12kg)
    v_qty := monthly_entitlement(p_card_type_id => 2, p_family_size => 1, p_commodity_id => 1);
    IF v_qty = 12.00 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: PHH Rice Statutory Floor Enforced = ' || v_qty || ' KG (Expected: 12.00)');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: PHH Rice Statutory Floor = ' || v_qty || ' (Expected: 12.00)');
    END IF;

    -- Case 1.6: PER_MEMBER Ceiling Enforcement (Sugar rule 7: base 0.5kg + 0.25kg/member, max ceiling 2kg; 10 members = 3kg capped to 2kg)
    v_qty := monthly_entitlement(p_card_type_id => 2, p_family_size => 10, p_commodity_id => 3);
    IF v_qty = 2.00 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: PER_MEMBER Ceiling Enforced = ' || v_qty || ' KG (Expected: 2.00)');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: PER_MEMBER Ceiling = ' || v_qty || ' (Expected: 2.00)');
    END IF;
END;
/

-- TEST 2: INVALID ENTITLEMENT INPUTS VALIDATION
DECLARE
    v_qty NUMBER;
    v_caught BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 2: Invalid Entitlement Inputs Validation ---');
    BEGIN
        v_qty := monthly_entitlement(p_card_type_id => NULL, p_family_size => 3, p_commodity_id => 1);
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20010 THEN
                v_caught := TRUE;
                DBMS_OUTPUT.PUT_LINE('PASS: Null Card Type rejected with ORA-20010 as expected.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('FAIL: Unexpected error code: ' || SQLCODE);
            END IF;
    END;

    IF NOT v_caught THEN
        DBMS_OUTPUT.PUT_LINE('FAIL: Null Card Type was not rejected!');
    END IF;
END;
/

-- TEST 3: ALLOTMENT CALCULATION & APPROVAL WORKFLOW
DECLARE
    v_processed NUMBER;
    v_allot_id  NUMBER;
    v_status    VARCHAR2(25);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 3: Allotment Calculation & Approval ---');
    
    prc_generate_entitlements_for_month(2026, 11, 'TEST_RUNNER', v_processed);
    DBMS_OUTPUT.PUT_LINE('Info: Entitlements generated count: ' || v_processed);

    prc_generate_shop_allotments(2026, 11, 'TEST_RUNNER', v_processed);
    DBMS_OUTPUT.PUT_LINE('Info: Shop Allotments created count: ' || v_processed);

    -- Fetch an allotment in DRAFT
    SELECT allotment_id, allotment_status INTO v_allot_id, v_status
    FROM (SELECT allotment_id, allotment_status FROM allotments WHERE allotment_status = 'DRAFT' ORDER BY allotment_id)
    WHERE ROWNUM = 1;

    DBMS_OUTPUT.PUT_LINE('Draft Allotment ID: ' || v_allot_id || ', Status: ' || v_status);
    
    -- Approve allotment
    prc_approve_allotment(p_allotment_id => v_allot_id, p_officer_id => 2);

    SELECT allotment_status INTO v_status FROM allotments WHERE allotment_id = v_allot_id;
    IF v_status = 'APPROVED' THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Allotment successfully approved. Status: ' || v_status);
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: Allotment approval failed. Status: ' || v_status);
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('INFO: No draft allotments found to approve.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('FAIL in Allotment Test: ' || SQLERRM);
END;
/

-- TEST 4: SAFE SALE & CUSTOM EXCEPTION e_entitlement_exhausted
DECLARE
    v_bill_num VARCHAR2(35);
    v_sale_id  NUMBER;
    v_caught   BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 4: Custom Exception: e_entitlement_exhausted ---');
    
    -- Card 1001 only has 15kg remaining rice for 2026-10. Attempting to buy 50kg must fail.
    BEGIN
        pkg_pds_sales.process_single_sale(
            p_shop_id          => 1,
            p_card_id          => 1001,
            p_commodity_id     => 1,
            p_quantity         => 50.00,
            p_operator_user_id => 4,
            p_payment_mode     => 'CASH',
            p_auth_mode        => 'AADHAAR_OTP',
            p_bill_number      => v_bill_num,
            p_sale_id          => v_sale_id,
            p_allotment_year   => 2026,
            p_allotment_month  => 10
        );
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20001 THEN
                v_caught := TRUE;
                DBMS_OUTPUT.PUT_LINE('PASS: Caught custom exception e_entitlement_exhausted (ORA-20001): ' || SQLERRM);
            ELSE
                DBMS_OUTPUT.PUT_LINE('FAIL: Expected ORA-20001, but got: ' || SQLCODE || ' - ' || SQLERRM);
            END IF;
    END;

    IF NOT v_caught THEN
        DBMS_OUTPUT.PUT_LINE('FAIL: Sale exceeding remaining entitlement was allowed!');
    END IF;
END;
/

-- TEST 5: CUSTOM EXCEPTION e_insufficient_stock
DECLARE
    v_bill_num VARCHAR2(35);
    v_sale_id  NUMBER;
    v_caught   BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 5: Custom Exception: e_insufficient_stock ---');
    
    -- Temporarily set shop stock to 2kg for commodity 2 (wheat), then request 5kg
    UPDATE shop_stock SET current_quantity = 2.00 WHERE shop_id = 1 AND commodity_id = 2;
    COMMIT;

    BEGIN
        pkg_pds_sales.process_single_sale(
            p_shop_id          => 1,
            p_card_id          => 1002,
            p_commodity_id     => 2,
            p_quantity         => 5.00,
            p_operator_user_id => 4,
            p_payment_mode     => 'CASH',
            p_auth_mode        => 'AADHAAR_OTP',
            p_bill_number      => v_bill_num,
            p_sale_id          => v_sale_id,
            p_allotment_year   => 2026,
            p_allotment_month  => 10
        );
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20002 THEN
                v_caught := TRUE;
                DBMS_OUTPUT.PUT_LINE('PASS: Caught custom exception e_insufficient_stock (ORA-20002): ' || SQLERRM);
            ELSE
                DBMS_OUTPUT.PUT_LINE('FAIL: Expected ORA-20002, but got: ' || SQLCODE || ' - ' || SQLERRM);
            END IF;
    END;

    -- Restore stock level
    UPDATE shop_stock SET current_quantity = 1200.00 WHERE shop_id = 1 AND commodity_id = 2;
    COMMIT;

    IF NOT v_caught THEN
        DBMS_OUTPUT.PUT_LINE('FAIL: Sale exceeding shop stock was allowed!');
    END IF;
END;
/

-- TEST 6: SUCCESSFUL ATOMIC SALE & LEDGER AUDIT
DECLARE
    v_bill_num  VARCHAR2(35);
    v_sale_id   NUMBER;
    v_rem_after NUMBER;
    v_mv_count  NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 6: Successful Atomic Sale & Stock Movement Audit ---');
    
    -- Card 1002 has 5kg wheat remaining. Buy 2kg wheat.
    pkg_pds_sales.process_single_sale(
        p_shop_id          => 1,
        p_card_id          => 1002,
        p_commodity_id     => 2,
        p_quantity         => 2.00,
        p_operator_user_id => 4,
        p_payment_mode     => 'CASH',
        p_auth_mode        => 'AADHAAR_OTP',
        p_bill_number      => v_bill_num,
        p_sale_id          => v_sale_id,
        p_allotment_year   => 2026,
        p_allotment_month  => 10
    );

    DBMS_OUTPUT.PUT_LINE('Sale Completed. Bill Number: ' || v_bill_num || ', Sale ID: ' || v_sale_id);

    -- Verify remaining entitlement decremented to 3.00
    SELECT remaining_quantity INTO v_rem_after
    FROM entitlements
    WHERE card_id = 1002 AND commodity_id = 2 AND allotment_year = 2026 AND allotment_month = 10;

    IF v_rem_after = 3.00 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Remaining entitlement correctly updated to 3.00 KG.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: Remaining entitlement expected 3.00, got: ' || v_rem_after);
    END IF;

    -- Verify stock movement ledger entry
    SELECT COUNT(*) INTO v_mv_count
    FROM stock_movements
    WHERE reference_doc_type = 'BILL' AND reference_doc_id = v_bill_num;

    IF v_mv_count = 1 THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Immutable stock movement recorded for bill ' || v_bill_num);
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: No stock movement entry found for sale!');
    END IF;
END;
/

-- TEST 7: STOCK RECONCILIATION & DISCREPANCY CALCULATION
DECLARE
    v_recon_id NUMBER;
    v_variance NUMBER;
    v_status   VARCHAR2(25);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 7: Physical Stock Reconciliation ---');
    
    -- Current stock of Shop #1, Commodity #1 is ~4850kg. Record physical count as 4820kg (30kg variance)
    prc_record_stock_reconciliation(
        p_shop_id        => 1,
        p_commodity_id   => 1,
        p_physical_count => 4820.00,
        p_officer_id     => 2,
        p_remarks        => 'Automated test physical weighment',
        p_recon_id       => v_recon_id
    );

    SELECT variance_quantity, investigation_status
    INTO v_variance, v_status
    FROM stock_reconciliation
    WHERE recon_id = v_recon_id;

    IF v_variance = -30.00 AND v_status = 'PENDING_REVIEW' THEN
        DBMS_OUTPUT.PUT_LINE('PASS: Reconciliation calculated variance = -30.00 KG, Status = PENDING_REVIEW (Recon ID: ' || v_recon_id || ')');
    ELSE
        DBMS_OUTPUT.PUT_LINE('FAIL: Variance: ' || v_variance || ', Status: ' || v_status);
    END IF;
END;
/

-- TEST 8: EMPTY ITEM PAYLOAD REJECTION (ORA-20019)
DECLARE
    v_bill_num VARCHAR2(35);
    v_sale_id  NUMBER;
    v_total    NUMBER;
    v_caught   BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 8: Empty JSON Payload Rejection ---');
    BEGIN
        pkg_pds_sales.process_sale(
            p_shop_id          => 1,
            p_card_id          => 1001,
            p_operator_user_id => 4,
            p_payment_mode     => 'CASH',
            p_auth_mode        => 'AADHAAR_OTP',
            p_items_json       => '[]',
            p_bill_number      => v_bill_num,
            p_sale_id          => v_sale_id,
            p_total_amount     => v_total,
            p_allotment_year   => 2026,
            p_allotment_month  => 10
        );
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20019 THEN
                v_caught := TRUE;
                DBMS_OUTPUT.PUT_LINE('PASS: Caught empty payload error (ORA-20019): ' || SQLERRM);
            ELSE
                DBMS_OUTPUT.PUT_LINE('FAIL: Expected ORA-20019, but got: ' || SQLCODE || ' - ' || SQLERRM);
            END IF;
    END;

    IF NOT v_caught THEN
        DBMS_OUTPUT.PUT_LINE('FAIL: Empty items sale was committed!');
    END IF;
END;
/

PROMPT ================================================================;
PROMPT PL/SQL TEST SUITE EXECUTION COMPLETED;
PROMPT ================================================================;
