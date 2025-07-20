package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // Railway 環境變數配置，沒設定就回本機預設
    private static final String HOST = System.getenv("MYSQLHOST") != null
            ? System.getenv("MYSQLHOST")
            : "localhost";
    private static final String PORT = System.getenv("MYSQLPORT") != null
            ? System.getenv("MYSQLPORT")
            : "3306";
    private static final String DATABASE = System.getenv("MYSQLDATABASE") != null
            ? System.getenv("MYSQLDATABASE")
            : "Backend_side_project";
    private static final String USER = System.getenv("MYSQLUSER") != null
            ? System.getenv("MYSQLUSER")
            : "root";
    private static final String PASS = System.getenv("MYSQLPASSWORD") != null
            ? System.getenv("MYSQLPASSWORD")
            : "he753951";  // 本機 MySQL root 密碼
    
    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            HOST, PORT, DATABASE);



    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to DB → URL=" + URL + ", USER=" + USER);
        if (USER == null || PASS == null) {
            throw new IllegalStateException("Missing MYSQL environment variables");
        }
        System.out.println("Trying to connect to " + URL);
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
