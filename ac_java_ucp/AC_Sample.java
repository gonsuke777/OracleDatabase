import java.sql.*;
import javax.sql.PooledConnection;

import oracle.jdbc.replay.OracleDataSourceFactory;
import oracle.jdbc.replay.OracleDataSource;
import oracle.jdbc.replay.OracleConnectionPoolDataSource;

import java.lang.Thread;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AC_Sample {
  final static String DB_URL=   "jdbc:oracle:thin:@ayuatp1_tp?TNS_ADMIN=C:/tools/wallet/Wallet_AYUATP1";
  final static String DB_USER = "ADMIN";
  final static String DB_PASSWORD = "xxx";

  public static void main(String args[]) throws Exception {
    // Get the OracleConnectionPoolDataSource
    OracleConnectionPoolDataSource pds = OracleDataSourceFactory.getOracleConnectionPoolDataSource();;

    // Set the connection property
    pds.setURL(DB_URL);
    pds.setUser(DB_USER);
    pds.setPassword(DB_PASSWORD);

    // DB Connection
    PooledConnection pc = pds.getPooledConnection();

    // Get the database connection from PooledConnection.
    try (Connection conn = pc.getConnection()) {
      // Displays db container name
      GetConainerName(conn);
      // Set Application Continuity test data
      System.out.println("Set AC Data start - " + FormatLocalDateTime(LocalDateTime.now()));
      SetACData(conn);
      System.out.println("Set AC Data end   - " + FormatLocalDateTime(LocalDateTime.now()));
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("ACSample - " + "SQLException occurred : " + e.getMessage());
      throw e;
    }

    // DB Connection close
    pc.close();
  }
 /*
  * Displays database container name.
  */
  public static void GetConainerName(Connection connection) throws SQLException {
    // Statement and ResultSet are AutoCloseable and closed automatically. 
    //final String SQL = "INSERT INTO AC_TEST VALUES(?, ?, SYSDATE)";
    connection.setAutoCommit(false);
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery("SELECT NAME FROM V$CONTAINERS")) {
        while (resultSet.next()) {
          System.out.println("Container Name - " + resultSet.getString(1));
        }
      }
    }   
  } 
 /*
  * Set Application Continuity test data.
  */
  public static void SetACData(Connection connection) throws SQLException {
    // Insert SQL.
    final String SQL = "INSERT INTO AC_TEST_TABLE VALUES(?, ?, SYSDATE)";
    connection.setAutoCommit(false);
    // Insert 100 rows by waiting 1 second for each row.
    try (PreparedStatement ps = connection.prepareStatement(SQL)) {
      for (int i = 1; i <= 100; i++) {
        System.out.println(i + "..." + FormatLocalDateTime(LocalDateTime.now()));
        ps.setInt(1, i);
        ps.setString(2, "Data" + i);
        ps.executeUpdate();
        try {
          Thread.sleep(1000);
        }
        catch(InterruptedException e) {
          e.printStackTrace();
          throw new RuntimeException("Unexpected interrupt", e);
        }
      }
    }
    connection.commit();
  } 
 /*
  * To formatted string yyyy/MM/dd HH:mm:ss.SSS from LocalDateTime.
  */
  public static String FormatLocalDateTime(LocalDateTime ldt) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    return ldt.format(dtf);
  }
}

