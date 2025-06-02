package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // 先嘗試讀環境變數，沒設定就回本機預設
    private static final String URL  = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:mysql://localhost:3306/Backend_side_project?useSSL=false&serverTimezone=UTC";
    private static final String USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER")
            : "root";
    private static final String PASS = System.getenv("DB_PASS") != null
            ? System.getenv("DB_PASS")
            : "he753951";  // 本機 MySQL root 密碼



    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to DB → URL=" + URL + ", USER=" + USER);
        if (URL == null || USER == null || PASS == null) {
            throw new IllegalStateException("Missing DB_URL / DB_USER / DB_PASS env vars");
        }
        System.out.println("Trying to connect to " + URL);
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
