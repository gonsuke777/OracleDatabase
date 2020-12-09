-- Test table for sample program.
CREATE TABLE AC_TEST_TABLE (
    C1 NUMBER
  , C2 VARCHAR2(100)
  , C3 DATE
);

-- Application Continuity Enabled in Autonomous Database.
BEGIN
    DBMS_CLOUD_ADMIN.ENABLE_APP_CONT(
        service_name => 'xxxxxxxxxx_ATP.adb.oraclecloud.com'
    );
END;
/

SELECT name, drain_timeout FROM v$services;

