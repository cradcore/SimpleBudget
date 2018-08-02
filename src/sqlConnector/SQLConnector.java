package sqlConnector;

import java.sql.*;

public class SQLConnector {

    // Class variables
    private static String sqlURL = "jdbc:mysql://192.168.0.222:3306/simpleBudget";
    private static String[] userPass = {"root", "s3rver"};

    private static Statement connect() {
        Connection con = null;
        try {
            Statement stmnt;
            ResultSet rs;

            // Register the JDBC driver for MySQL
            Class.forName("com.mysql.jdbc.Driver");

            // Define URL of database server for 'cradcore' on the faure
            String url = sqlURL;

            // Get a connection to the database for cradcore
            con = DriverManager.getConnection(url, userPass[0], userPass[1]);

            // Get Statement object
            stmnt = con.createStatement();

            return stmnt;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet select(String command) {
        try {
            return connect().executeQuery(command);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int update(String command) {
        try {
            return connect().executeUpdate(command);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
