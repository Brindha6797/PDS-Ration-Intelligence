-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 07: PL/SQL PACKAGES
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;

PROMPT Creating Package Specification: pkg_pds_sales...;

CREATE OR REPLACE PACKAGE pkg_pds_sales AS
    -- Custom Exceptions
    e_entitlement_exhausted EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_entitlement_exhausted, -20001);

    e_insufficient_stock    EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_insufficient_stock, -20002);

    e_card_inactive         EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_card_inactive, -20003);

    e_shop_inactive         EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_shop_inactive, -20004);

    -- Types
    TYPE t_sale_item_rec IS RECORD (
        commodity_id NUMBER,
        quantity     NUMBER(8,2)
    );
    TYPE t_sale_item_tab IS TABLE OF t_sale_item_rec INDEX BY PLS_INTEGER;

    -- Procedure to process atomic ration sale
    PROCEDURE process_sale (
        p_shop_id          IN NUMBER,
        p_card_id          IN NUMBER,
        p_operator_user_id IN NUMBER,
        p_payment_mode     IN VARCHAR2,
        p_auth_mode        IN VARCHAR2,
        p_items_json       IN VARCHAR2, -- JSON string: [{"commodityId": 1, "quantity": 20.0}, ...]
        p_bill_number      OUT VARCHAR2,
        p_sale_id          OUT NUMBER,
        p_total_amount     OUT NUMBER,
        p_allotment_year   IN NUMBER DEFAULT NULL,
        p_allotment_month  IN NUMBER DEFAULT NULL
    );

    -- Procedure for single-item sale convenience
    PROCEDURE process_single_sale (
        p_shop_id          IN NUMBER,
        p_card_id          IN NUMBER,
        p_commodity_id     IN NUMBER,
        p_quantity         IN NUMBER,
        p_operator_user_id IN NUMBER,
        p_payment_mode     IN VARCHAR2,
        p_auth_mode        IN VARCHAR2,
        p_bill_number      OUT VARCHAR2,
        p_sale_id          OUT NUMBER,
        p_allotment_year   IN NUMBER DEFAULT NULL,
        p_allotment_month  IN NUMBER DEFAULT NULL
    );
END pkg_pds_sales;
/

PROMPT Creating Package Body: pkg_pds_sales...;

CREATE OR REPLACE PACKAGE BODY pkg_pds_sales AS

    PROCEDURE process_single_sale (
        p_shop_id          IN NUMBER,
        p_card_id          IN NUMBER,
        p_commodity_id     IN NUMBER,
        p_quantity         IN NUMBER,
        p_operator_user_id IN NUMBER,
        p_payment_mode     IN VARCHAR2,
        p_auth_mode        IN VARCHAR2,
        p_bill_number      OUT VARCHAR2,
        p_sale_id          OUT NUMBER,
        p_allotment_year   IN NUMBER DEFAULT NULL,
        p_allotment_month  IN NUMBER DEFAULT NULL
    ) IS
        v_items_json VARCHAR2(200);
        v_total      NUMBER;
    BEGIN
        v_items_json := '[{"commodityId": ' || p_commodity_id || ', "quantity": ' || p_quantity || '}]';
        process_sale(
            p_shop_id          => p_shop_id,
            p_card_id          => p_card_id,
            p_operator_user_id => p_operator_user_id,
            p_payment_mode     => p_payment_mode,
            p_auth_mode        => p_auth_mode,
            p_items_json       => v_items_json,
            p_bill_number      => p_bill_number,
            p_sale_id          => p_sale_id,
            p_total_amount     => v_total,
            p_allotment_year   => p_allotment_year,
            p_allotment_month  => p_allotment_month
        );
    END process_single_sale;

    PROCEDURE process_sale (
        p_shop_id          IN NUMBER,
        p_card_id          IN NUMBER,
        p_operator_user_id IN NUMBER,
        p_payment_mode     IN VARCHAR2,
        p_auth_mode        IN VARCHAR2,
        p_items_json       IN VARCHAR2,
        p_bill_number      OUT VARCHAR2,
        p_sale_id          OUT NUMBER,
        p_total_amount     OUT NUMBER,
        p_allotment_year   IN NUMBER DEFAULT NULL,
        p_allotment_month  IN NUMBER DEFAULT NULL
    ) IS
        v_card_status       VARCHAR2(20);
        v_card_shop_id      NUMBER;
        v_card_num          VARCHAR2(30);
        v_shop_status       VARCHAR2(20);
        v_shop_code         VARCHAR2(20);
        v_year              NUMBER(4);
        v_month             NUMBER(2);
        v_sale_id           NUMBER;
        v_bill_num          VARCHAR2(35);
        v_overall_total     NUMBER(8,2) := 0.00;
        v_item_count        NUMBER := 0;
        
        -- JSON parsing variables
        v_comm_id           NUMBER;
        v_qty               NUMBER(8,2);
        v_unit_rate         NUMBER(8,2);
        v_subsidy_rate      NUMBER(8,2);
        v_item_total        NUMBER(8,2);
        v_comm_name         VARCHAR2(100);
        
        -- Row lock variables
        v_shop_stock_qty    NUMBER(10,2);
        v_rem_quota         NUMBER(8,2);
        v_ent_id            NUMBER;
    BEGIN
        v_year := NVL(p_allotment_year, TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY')));
        v_month := NVL(p_allotment_month, TO_NUMBER(TO_CHAR(SYSDATE, 'MM')));

        -- 1. Validate Cardholder and acquire row-level lock on the card
        BEGIN
            SELECT card_status, assigned_shop_id, card_number
            INTO v_card_status, v_card_shop_id, v_card_num
            FROM card_holders
            WHERE card_id = p_card_id
            FOR UPDATE;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20015, 'Invalid Ration Card ID: ' || p_card_id);
        END;

        IF v_card_status <> 'ACTIVE' THEN
            RAISE_APPLICATION_ERROR(-20003, 'Ration Card ' || v_card_num || ' is ' || v_card_status || '. Distribution not permitted.');
        END IF;

        IF v_card_shop_id <> p_shop_id THEN
            -- In standard taluk rules, cards are assigned to specific shops
            -- Can be relaxed for portability, but enforced here by default
            RAISE_APPLICATION_ERROR(-20016, 'Card ' || v_card_num || ' is assigned to Shop #' || v_card_shop_id || ', not Shop #' || p_shop_id);
        END IF;

        -- 2. Validate Shop
        BEGIN
            SELECT shop_status, shop_code
            INTO v_shop_status, v_shop_code
            FROM shops
            WHERE shop_id = p_shop_id;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20017, 'Invalid Shop ID: ' || p_shop_id);
        END;

        IF v_shop_status <> 'ACTIVE' THEN
            RAISE_APPLICATION_ERROR(-20004, 'Fair-Price Shop ' || v_shop_code || ' is ' || v_shop_status || '. Sales halted.');
        END IF;

        -- 3. Create Sale Header
        v_sale_id := seq_sales.NEXTVAL;
        v_bill_num := 'BILL-' || v_shop_code || '-' || TO_CHAR(SYSDATE, 'YYYYMM') || '-' || LPAD(v_sale_id, 6, '0');

        INSERT INTO sales (
            sale_id, bill_number, shop_id, card_id, sale_date,
            sale_month, sale_year, total_amount, payment_mode,
            auth_mode, operator_user_id, sale_status, created_at
        ) VALUES (
            v_sale_id, v_bill_num, p_shop_id, p_card_id, SYSTIMESTAMP,
            v_month, v_year, 0.00, p_payment_mode,
            p_auth_mode, p_operator_user_id, 'COMPLETED', SYSTIMESTAMP
        );

        -- 4. Process Items with Row-Level Locking (SELECT FOR UPDATE)
        -- Order items deterministically by commodity_id to eliminate cross-transaction deadlocks (ORA-00060)
        FOR item IN (
            SELECT jt.commodity_id, jt.quantity
            FROM JSON_TABLE(p_items_json, '$[*]'
                COLUMNS (
                    commodity_id NUMBER PATH '$.commodityId',
                    quantity     NUMBER PATH '$.quantity'
                )
            ) jt
            ORDER BY jt.commodity_id
        ) LOOP
            v_item_count := v_item_count + 1;
            v_comm_id := item.commodity_id;
            v_qty := item.quantity;

            IF v_qty <= 0 THEN
                RAISE_APPLICATION_ERROR(-20018, 'Sale quantity must be positive.');
            END IF;

            -- Fetch commodity rates
            SELECT commodity_name, consumer_rate_per_unit, government_subsidy_per_unit
            INTO v_comm_name, v_unit_rate, v_subsidy_rate
            FROM commodities
            WHERE commodity_id = v_comm_id AND is_active = 'Y';

            -- A. Lock Shop Stock row and verify sufficient inventory
            BEGIN
                SELECT current_quantity
                INTO v_shop_stock_qty
                FROM shop_stock
                WHERE shop_id = p_shop_id
                  AND commodity_id = v_comm_id
                FOR UPDATE;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    RAISE_APPLICATION_ERROR(-20002, 'Insufficient stock: No inventory record for ' || v_comm_name || ' in Shop #' || p_shop_id);
            END;

            IF v_shop_stock_qty < v_qty THEN
                RAISE_APPLICATION_ERROR(-20002, 'Insufficient stock for ' || v_comm_name || '. Available in shop: ' || v_shop_stock_qty || ', Requested: ' || v_qty);
            END IF;

            -- B. Lock Entitlement row and verify cardholder quota
            BEGIN
                SELECT entitlement_id, remaining_quantity
                INTO v_ent_id, v_rem_quota
                FROM entitlements
                WHERE card_id = p_card_id
                  AND commodity_id = v_comm_id
                  AND allotment_year = v_year
                  AND allotment_month = v_month
                FOR UPDATE;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    RAISE_APPLICATION_ERROR(-20001, 'No monthly entitlement quota found for ' || v_comm_name || ' for Card ' || v_card_num || ' in period ' || v_year || '-' || LPAD(v_month, 2, '0'));
            END;

            IF v_rem_quota < v_qty THEN
                RAISE_APPLICATION_ERROR(-20001, 'Entitlement exhausted for ' || v_comm_name || '. Remaining balance: ' || v_rem_quota || ', Requested: ' || v_qty);
            END IF;

            -- C. Insert Sale Item
            v_item_total := ROUND(v_qty * v_unit_rate, 2);
            v_overall_total := v_overall_total + v_item_total;

            INSERT INTO sale_items (
                sale_item_id, sale_id, commodity_id, quantity,
                unit_rate, total_price, subsidy_amount
            ) VALUES (
                seq_sale_items.NEXTVAL, v_sale_id, v_comm_id, v_qty,
                v_unit_rate, v_item_total, ROUND(v_qty * v_subsidy_rate, 2)
            );

            -- D. Atomically Update Shop Inventory
            UPDATE shop_stock
            SET current_quantity = current_quantity - v_qty,
                total_distributed_month = total_distributed_month + v_qty,
                last_sale_date = SYSTIMESTAMP,
                updated_at = SYSTIMESTAMP
            WHERE shop_id = p_shop_id
              AND commodity_id = v_comm_id;

            -- E. Atomically Update Cardholder Entitlement
            UPDATE entitlements
            SET distributed_quantity = distributed_quantity + v_qty,
                remaining_quantity = remaining_quantity - v_qty,
                last_distributed_at = SYSTIMESTAMP,
                status = CASE 
                    WHEN (remaining_quantity - v_qty) <= 0.001 THEN 'EXHAUSTED'
                    ELSE 'PARTIALLY_DRAWN' END,
                updated_at = SYSTIMESTAMP
            WHERE entitlement_id = v_ent_id;

            -- F. Append to Immutable Stock Movement Ledger
            INSERT INTO stock_movements (
                movement_id, movement_type, source_type, source_id,
                dest_type, dest_id, commodity_id, quantity,
                reference_doc_type, reference_doc_id, created_by, movement_timestamp
            ) VALUES (
                seq_stock_movements.NEXTVAL, 'SALE', 'SHOP', p_shop_id,
                'CARDHOLDER', p_card_id, v_comm_id, v_qty,
                'BILL', v_bill_num, 'USER_' || p_operator_user_id, SYSTIMESTAMP
            );
        END LOOP;

        -- Ensure sale contained at least one valid line item
        IF v_item_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20019, 'Cannot process sale: No valid sale items provided in JSON payload.');
        END IF;

        -- Update header total
        UPDATE sales
        SET total_amount = v_overall_total
        WHERE sale_id = v_sale_id;

        -- Write Audit Log
        INSERT INTO audit_logs (
            log_id, entity_name, entity_id, action_type,
            performed_by, action_timestamp, new_values, status_result
        ) VALUES (
            seq_audit_logs.NEXTVAL, 'SALES', TO_CHAR(v_sale_id), 'INSERT',
            'USER_' || p_operator_user_id, SYSTIMESTAMP,
            '{"billNumber":"' || v_bill_num || '","totalAmount":' || v_overall_total || ',"cardId":' || p_card_id || '}',
            'SUCCESS'
        );

        p_bill_number  := v_bill_num;
        p_sale_id      := v_sale_id;
        p_total_amount := v_overall_total;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END process_sale;

END pkg_pds_sales;
/

PROMPT Creating Package Specification: pkg_pds_allotment...;

CREATE OR REPLACE PACKAGE pkg_pds_allotment AS
    PROCEDURE generate_taluk_allotments (
        p_year            IN NUMBER,
        p_month           IN NUMBER,
        p_officer_id      IN NUMBER,
        p_generated_count OUT NUMBER
    );

    PROCEDURE create_godown_dispatch (
        p_godown_id     IN NUMBER,
        p_shop_id       IN NUMBER,
        p_vehicle_num   IN VARCHAR2,
        p_driver_name   IN VARCHAR2,
        p_driver_phone  IN VARCHAR2,
        p_dispatched_by IN VARCHAR2,
        p_items_json    IN VARCHAR2, -- [{"commodityId": 1, "quantity": 5000.0}, ...]
        p_dispatch_id   OUT NUMBER,
        p_dispatch_num  OUT VARCHAR2
    );
END pkg_pds_allotment;
/

PROMPT Creating Package Body: pkg_pds_allotment...;

CREATE OR REPLACE PACKAGE BODY pkg_pds_allotment AS

    PROCEDURE generate_taluk_allotments (
        p_year            IN NUMBER,
        p_month           IN NUMBER,
        p_officer_id      IN NUMBER,
        p_generated_count OUT NUMBER
    ) IS
        v_ent_count NUMBER;
    BEGIN
        -- 1. Ensure card entitlements are generated
        prc_generate_entitlements_for_month(p_year, p_month, 'OFFICER_' || p_officer_id, v_ent_count);

        -- 2. Aggregate into shop allotments
        prc_generate_shop_allotments(p_year, p_month, 'OFFICER_' || p_officer_id, p_generated_count);
    END generate_taluk_allotments;

    PROCEDURE create_godown_dispatch (
        p_godown_id     IN NUMBER,
        p_shop_id       IN NUMBER,
        p_vehicle_num   IN VARCHAR2,
        p_driver_name   IN VARCHAR2,
        p_driver_phone  IN VARCHAR2,
        p_dispatched_by IN VARCHAR2,
        p_items_json    IN VARCHAR2,
        p_dispatch_id   OUT NUMBER,
        p_dispatch_num  OUT VARCHAR2
    ) IS
        v_disp_id       NUMBER;
        v_disp_num      VARCHAR2(30);
        v_gdn_stock_qty NUMBER(12,2);
        v_comm_id       NUMBER;
        v_qty           NUMBER(10,2);
        v_comm_name     VARCHAR2(100);
    BEGIN
        v_disp_id := seq_dispatches.NEXTVAL;
        v_disp_num := 'DSP-' || TO_CHAR(SYSDATE, 'YYYYMM') || '-' || LPAD(v_disp_id, 5, '0');

        INSERT INTO dispatches (
            dispatch_id, dispatch_number, godown_id, shop_id,
            vehicle_number, driver_name, driver_phone, dispatch_status,
            dispatched_at, dispatched_by, created_at
        ) VALUES (
            v_disp_id, v_disp_num, p_godown_id, p_shop_id,
            p_vehicle_num, p_driver_name, p_driver_phone, 'IN_TRANSIT',
            SYSTIMESTAMP, p_dispatched_by, SYSTIMESTAMP
        );

        -- Process each dispatch item
        FOR item IN (
            SELECT jt.commodity_id, jt.quantity
            FROM JSON_TABLE(p_items_json, '$[*]'
                COLUMNS (
                    commodity_id NUMBER PATH '$.commodityId',
                    quantity     NUMBER PATH '$.quantity'
                )
            ) jt
        ) LOOP
            v_comm_id := item.commodity_id;
            v_qty := item.quantity;

            SELECT commodity_name INTO v_comm_name
            FROM commodities WHERE commodity_id = v_comm_id;

            -- Lock and verify Godown Stock
            SELECT current_quantity
            INTO v_gdn_stock_qty
            FROM godown_stock
            WHERE godown_id = p_godown_id AND commodity_id = v_comm_id
            FOR UPDATE;

            IF v_gdn_stock_qty < v_qty THEN
                RAISE_APPLICATION_ERROR(-20045, 'Insufficient stock in Godown #' || p_godown_id || ' for ' || v_comm_name || '. Available: ' || v_gdn_stock_qty || ', Requested: ' || v_qty);
            END IF;

            -- Insert line item
            INSERT INTO dispatch_items (
                dispatch_item_id, dispatch_id, commodity_id,
                dispatched_quantity, discrepancy_quantity
            ) VALUES (
                seq_dispatch_items.NEXTVAL, v_disp_id, v_comm_id,
                v_qty, 0.00
            );

            -- Decrement godown stock
            UPDATE godown_stock
            SET current_quantity = current_quantity - v_qty,
                updated_at = SYSTIMESTAMP
            WHERE godown_id = p_godown_id AND commodity_id = v_comm_id;

            -- Record stock movement
            INSERT INTO stock_movements (
                movement_id, movement_type, source_type, source_id,
                dest_type, dest_id, commodity_id, quantity,
                reference_doc_type, reference_doc_id, created_by, movement_timestamp
            ) VALUES (
                seq_stock_movements.NEXTVAL, 'GODOWN_DISPATCH', 'GODOWN', p_godown_id,
                'SHOP', p_shop_id, v_comm_id, v_qty,
                'DISPATCH', v_disp_num, p_dispatched_by, SYSTIMESTAMP
            );

            -- Update allotment dispatched quantity
            UPDATE allotments
            SET dispatched_quantity = dispatched_quantity + v_qty,
                allotment_status = CASE 
                    WHEN allotment_status = 'COMPLETED' THEN 'COMPLETED'
                    ELSE 'PARTIALLY_DISPATCHED' END,
                updated_at = SYSTIMESTAMP
            WHERE shop_id = p_shop_id
              AND commodity_id = v_comm_id
              AND allotment_year = TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY'))
              AND allotment_month = TO_NUMBER(TO_CHAR(SYSDATE, 'MM'));
        END LOOP;

        p_dispatch_id := v_disp_id;
        p_dispatch_num := v_disp_num;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END create_godown_dispatch;

END pkg_pds_allotment;
/

PROMPT Packages created successfully.
