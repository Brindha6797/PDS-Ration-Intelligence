-- ====================================================================
-- PDS RATION SHOP STOCK & ENTITLEMENT SYSTEM
-- SCRIPT 01: CREATE SCHEMA OBJECTS & ENVIRONMENT CONFIGURATION
-- Target: Oracle Database 19c / 21c / 23c
-- ====================================================================

-- Set environment settings for SQL*Plus / SQL Developer
SET ECHO ON;
SET SERVEROUTPUT ON SIZE UNLIMITED;
SET LINESIZE 200;
SET PAGESIZE 50;

PROMPT ================================================================;
PROMPT INITIALIZING PDS SCHEMA ENVIRONMENT CONFIGURATION;
PROMPT ================================================================;

-- Optional: If running as DBA (SYS / SYSTEM), uncomment the lines below:
/*
CREATE USER c##pds_admin IDENTIFIED BY "PdsAdmin2026#Secure"
  DEFAULT TABLESPACE users
  TEMPORARY TABLESPACE temp
  QUOTA UNLIMITED ON users;

GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE PROCEDURE, CREATE SEQUENCE, CREATE TRIGGER TO c##pds_admin;
ALTER USER c##pds_admin DEFAULT ROLE ALL;
*/

-- Safe cleanup: Drop existing tables and views if re-running
BEGIN
  -- 1. Drop existing views
  FOR v IN (SELECT view_name FROM user_views WHERE view_name LIKE 'V_%') LOOP
    EXECUTE IMMEDIATE 'DROP VIEW ' || v.view_name;
    DBMS_OUTPUT.PUT_LINE('Dropped view: ' || v.view_name);
  END LOOP;

  -- 2. Drop existing tables if re-running schema deployment
  FOR t IN (SELECT table_name FROM user_tables WHERE table_name IN (
    'ERP_STAGING_MOVEMENTS', 'AUDIT_LOGS', 'USER_ROLES', 'ROLES', 'USERS',
    'COMPLAINT_HISTORY', 'COMPLAINTS', 'STOCK_RECONCILIATION', 'STOCK_MOVEMENTS',
    'SALE_ITEMS', 'SALES', 'DISPATCH_ITEMS', 'DISPATCHES', 'SHOP_STOCK',
    'ALLOTMENTS', 'ENTITLEMENTS', 'ENTITLEMENT_RULES', 'FAMILY_MEMBERS',
    'CARD_HOLDERS', 'SHOPS', 'GODOWN_STOCK', 'GODOWNS', 'COMMODITIES', 'CARD_TYPES'
  )) LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS PURGE';
    DBMS_OUTPUT.PUT_LINE('Dropped existing table: ' || t.table_name);
  END LOOP;
EXCEPTION
  WHEN OTHERS THEN
    NULL;
END;
/

PROMPT Schema objects preparation completed. Proceed to 02_create_tables.sql.
