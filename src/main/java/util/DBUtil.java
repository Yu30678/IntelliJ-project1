package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/Backend_side_project";
    private static final String USER = "root";
    private static final String PASSWORD = "he753951";

    public static Connection getConnection() throws Exception {

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);

    }
}
