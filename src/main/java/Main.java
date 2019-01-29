import model.Workshop;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author LinX
 */
public class Main {
    private static final String DB_PATH = "jdbc:mysql://localhost:3306/defect_report?serverTimezone=UTC";

    private static final String USER = "root";

    private static final String PASSWORD = "";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try (Connection c = DriverManager.getConnection
                (DB_PATH, USER, PASSWORD)) {
            String sql = "select * from " + Workshop.WORKSHOP_TABLE;

            DSL.using(c)
                    .fetch(sql)
                    .map(Workshop::new).forEach(System.out::println);
        }
    }
}
