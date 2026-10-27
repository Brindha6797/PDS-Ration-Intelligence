-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 05: STORED FUNCTIONS (ORACLE PL/SQL)
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

SET SERVEROUTPUT ON;

PROMPT Creating Function: monthly_entitlement...;

CREATE OR REPLACE FUNCTION monthly_entitlement (
    p_card_type_id  IN NUMBER,
    p_family_size   IN NUMBER,
    p_commodity_id  IN NUMBER,
    p_as_of_date    IN DATE DEFAULT SYSDATE
) RETURN NUMBER
IS
    v_permitted_qty NUMBER(8,2) := 0.00;
    v_rule_found    BOOLEAN := FALSE;
    v_eff_date      DATE := TRUNC(NVL(p_as_of_date, SYSDATE));
    
    CURSOR c_rule IS
        SELECT calc_method,
               base_quantity,
               per_member_quantity,
               min_family_size,
               max_family_size,
               max_quantity_ceiling
        FROM entitlement_rules
        WHERE card_type_id = p_card_type_id
          AND commodity_id = p_commodity_id
          AND is_active = 'Y'
          AND effective_start_date <= v_eff_date
          AND (effective_end_date IS NULL OR effective_end_date >= v_eff_date)
          AND (min_family_size IS NULL OR p_family_size >= min_family_size)
          AND (max_family_size IS NULL OR p_family_size <= max_family_size)
        ORDER BY effective_start_date DESC;

    r_rule c_rule%ROWTYPE;
BEGIN
    -- Input Validation
    IF p_card_type_id IS NULL OR p_commodity_id IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'Card type ID and Commodity ID must not be null.');
    END IF;

    IF p_family_size IS NULL OR p_family_size < 1 THEN
        RAISE_APPLICATION_ERROR(-20011, 'Family size must be at least 1.');
    END IF;

    -- Fetch applicable policy rule
    OPEN c_rule;
    FETCH c_rule INTO r_rule;
    IF c_rule%FOUND THEN
        v_rule_found := TRUE;
    END IF;
    CLOSE c_rule;

    IF NOT v_rule_found THEN
        -- If no rule is configured for this commodity/card type, return 0.00
        RETURN 0.00;
    END IF;

    -- Calculate according to rule method
    CASE r_rule.calc_method
        WHEN 'FIXED_PER_CARD' THEN
            v_permitted_qty := r_rule.base_quantity;

        WHEN 'PER_MEMBER' THEN
            v_permitted_qty := r_rule.base_quantity + (NVL(r_rule.per_member_quantity, 0.00) * p_family_size);
            IF r_rule.max_quantity_ceiling IS NOT NULL AND v_permitted_qty > r_rule.max_quantity_ceiling THEN
                v_permitted_qty := r_rule.max_quantity_ceiling;
            END IF;

        WHEN 'PER_MEMBER_WITH_CEILING' THEN
            v_permitted_qty := (NVL(r_rule.per_member_quantity, 0.00) * p_family_size);
            -- Apply statutory minimum floor if base_quantity is specified
            IF r_rule.base_quantity > 0 AND v_permitted_qty < r_rule.base_quantity THEN
                v_permitted_qty := r_rule.base_quantity;
            END IF;
            -- Apply statutory ceiling if specified
            IF r_rule.max_quantity_ceiling IS NOT NULL AND v_permitted_qty > r_rule.max_quantity_ceiling THEN
                v_permitted_qty := r_rule.max_quantity_ceiling;
            END IF;

        WHEN 'SLAB_BASED' THEN
            -- Base quantity applies to the first member, per_member to additional members
            IF p_family_size = 1 THEN
                v_permitted_qty := r_rule.base_quantity;
            ELSE
                v_permitted_qty := r_rule.base_quantity + (NVL(r_rule.per_member_quantity, 0.00) * (p_family_size - 1));
            END IF;
            IF r_rule.max_quantity_ceiling IS NOT NULL AND v_permitted_qty > r_rule.max_quantity_ceiling THEN
                v_permitted_qty := r_rule.max_quantity_ceiling;
            END IF;

        ELSE
            -- Unknown method fallback
            v_permitted_qty := r_rule.base_quantity;
    END CASE;

    IF v_permitted_qty < 0.00 THEN
        v_permitted_qty := 0.00;
    END IF;

    RETURN v_permitted_qty;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE BETWEEN -20999 AND -20000 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20099, 'Error in monthly_entitlement: ' || SQLERRM);
        END IF;
END monthly_entitlement;
/

PROMPT Creating Function: fn_get_remaining_entitlement...;

CREATE OR REPLACE FUNCTION fn_get_remaining_entitlement (
    p_card_id      IN NUMBER,
    p_commodity_id IN NUMBER,
    p_year         IN NUMBER,
    p_month        IN NUMBER
) RETURN NUMBER
IS
    v_remaining NUMBER(8,2) := 0.00;
BEGIN
    SELECT NVL(remaining_quantity, 0.00)
    INTO v_remaining
    FROM entitlements
    WHERE card_id = p_card_id
      AND commodity_id = p_commodity_id
      AND allotment_year = p_year
      AND allotment_month = p_month;
      
    RETURN v_remaining;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0.00;
    WHEN OTHERS THEN
        RETURN 0.00;
END fn_get_remaining_entitlement;
/

PROMPT Creating Function: fn_calculate_expected_stock...;

CREATE OR REPLACE FUNCTION fn_calculate_expected_stock (
    p_shop_id      IN NUMBER,
    p_commodity_id IN NUMBER,
    p_as_of_date   IN DATE DEFAULT SYSDATE
) RETURN NUMBER
IS
    v_opening   NUMBER(10,2) := 0.00;
    v_receipts  NUMBER(10,2) := 0.00;
    v_sales     NUMBER(10,2) := 0.00;
    v_expected  NUMBER(10,2) := 0.00;
    v_start_month DATE := TRUNC(NVL(p_as_of_date, SYSDATE), 'MM');
BEGIN
    -- 1. Get opening balance of the month
    BEGIN
        SELECT NVL(opening_month_balance, 0.00)
        INTO v_opening
        FROM shop_stock
        WHERE shop_id = p_shop_id
          AND commodity_id = p_commodity_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_opening := 0.00;
    END;

    -- 2. Sum valid receipts confirmed this month
    SELECT NVL(SUM(di.received_quantity), 0.00)
    INTO v_receipts
    FROM dispatches d
    JOIN dispatch_items di ON d.dispatch_id = di.dispatch_id
    WHERE d.shop_id = p_shop_id
      AND di.commodity_id = p_commodity_id
      AND d.dispatch_status = 'RECEIVED'
      AND d.received_at >= v_start_month
      AND d.received_at <= p_as_of_date;

    -- 3. Sum valid sales this month
    SELECT NVL(SUM(si.quantity), 0.00)
    INTO v_sales
    FROM sales s
    JOIN sale_items si ON s.sale_id = si.sale_id
    WHERE s.shop_id = p_shop_id
      AND si.commodity_id = p_commodity_id
      AND s.sale_status = 'COMPLETED'
      AND s.sale_date >= v_start_month
      AND s.sale_date <= p_as_of_date;

    v_expected := v_opening + v_receipts - v_sales;
    IF v_expected < 0 THEN
        v_expected := 0.00;
    END IF;

    RETURN v_expected;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0.00;
END fn_calculate_expected_stock;
/

PROMPT Functions created successfully.
