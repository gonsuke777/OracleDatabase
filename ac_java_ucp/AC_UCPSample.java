import java.sql.*;
import oracle.ucp.jdbc.*;
import java.lang.Thread;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AC_UCPSample {
  final static String DB_URL=   "jdbc:oracle:thin:@ayuatp1_tp?TNS_ADMIN=/home/opc/work/wallet";
  final static String DB_USER = "ADMIN";
  final static String DB_PASSWORD = "xxx";
  final static String CONN_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";

  /*
   * The sample demonstrates UCP as client side connection pool.
   */
  public static void main(String args[]) throws Exception {
    // Get the PoolDataSource for UCP
    PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();

    // Set the connection factory first before all other properties
    pds.setConnectionFactoryClassName(CONN_FACTORY_CLASS_NAME);
    pds.setURL(DB_URL);
    pds.setUser(DB_USER);
    pds.setPassword(DB_PASSWORD);
    pds.setConnectionPoolName("JDBC_UCP_POOL");

    // Default is 0. Set the initial number of connections to be
    // created when UCP is started.
    pds.setInitialPoolSize(1);

    // Default is 0. Set the minimum number of connections
    // that is maintained by UCP at runtime.
    pds.setMinPoolSize(1);

    // Default is Integer.MAX_VALUE (2147483647). Set the maximum
    // number of connections allowed on the connection pool.
    pds.setMaxPoolSize(1);

    // Default is 30secs. Set the frequency in seconds to enforce
    // the timeout properties. Applies to
    // inactiveConnectionTimeout(int secs),
    // AbandonedConnectionTimeout(secs)&
    //TimeToLiveConnectionTimeout(int secs).
    // Range of valid values is 0 to Integer.MAX_VALUE.
    pds.setTimeoutCheckInterval(10);

    // Default is 0. Set the maximum time, in seconds, that a
    // connection remains available in the connection pool.
    pds.setInactiveConnectionTimeout(10);

    // Set seconds to wait on query.
    pds.setQueryTimeout(300);
    //pds.setFastConnectionFailoverEnabled(true);

    System.out.println("Available connections before checkout: " + pds.getAvailableConnectionsCount());
    System.out.println("Borrowed connections before checkout: " + pds.getBorrowedConnectionsCount());
    // Get the database connection from UCP.
    try (Connection conn = pds.getConnection()) {
      System.out.println("Available connections after checkout: " + pds.getAvailableConnectionsCount());
      System.out.println("Borrowed connections after checkout: " + pds.getBorrowedConnectionsCount());
      // Displays db container name
      GetConainerName(conn);
      // Set Application Continuity test data
      System.out.println("Set AC Data start - " + FormatLocalDateTime(LocalDateTime.now()));
      SetACData(conn);
      System.out.println("Set AC Data end   - " + FormatLocalDateTime(LocalDateTime.now()));
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("UCPSample - " + "SQLException occurred : " + e.getMessage());
      throw e;
    }
    System.out.println("Available connections after checkin: " + pds.getAvailableConnectionsCount());
    System.out.println("Borrowed connections after checkin: " + pds.getBorrowedConnectionsCount());
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
