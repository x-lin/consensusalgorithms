package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author LinX
 */
public class DatabaseConnector {
    private static final String DB_PATH = "jdbc:mysql://localhost:3306/defect_report?serverTimezone=UTC";

    private static final String USER = "root";

    private static final String PASSWORD = "";

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection( DB_PATH, USER, PASSWORD );
    }
}
