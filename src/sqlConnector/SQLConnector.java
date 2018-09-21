package sqlConnector;

import java.sql.*;

public class SQLConnector {

    // Class variables
    private String sqlURL = "jdbc:mysql://192.168.0.222:3306/simpleBudget";
    private String[] userPass = {"root", "s3rver"};
    private Statement stmnt;

    public SQLConnector() {
        connect();
    }

    private Statement connect() {
        Connection con = null;
        try {

            // Register the JDBC driver for MySQL
            Class.forName("com.mysql.jdbc.Driver");

            // Define URL of database server for 'cradcore'
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

    public ResultSet select(String command) {
        try {
            return stmnt.executeQuery(command);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(command);
        }
        return null;
    }

    public int update(String command) {
        try {
            return stmnt.executeUpdate(command);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
