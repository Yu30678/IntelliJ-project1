package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // Cloud SQL instance name (for Cloud Run)
    private static final String INSTANCE = System.getenv("INSTANCE_CONNECTION_NAME");

    // 資料庫名稱（可改成從環境變數讀取）
    private static final String DB_NAME = System.getenv("DB_NAME") != null
            ? System.getenv("DB_NAME")
            : "Backend_side_project";

    // 資料庫使用者
    private static final String USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER")
            : "root";

    // 資料庫密碼
    private static final String PASS = System.getenv("DB_PASS") != null
            ? System.getenv("DB_PASS")
            : "he753951";

    // JDBC URL：Cloud SQL 或本地 fallback
    private static final String URL = (INSTANCE != null)
            ? String.format(
            "jdbc:mysql:///%s?cloudSqlInstance=%s&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            DB_NAME,
            INSTANCE
    )
            : String.format(
            "jdbc:mysql://localhost:3306/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            DB_NAME
    );

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println(">>> JDBC URL = " + URL);
            System.out.println(">>> DB USER = " + USER);
            System.out.flush(); // ← 強制 flush 到 log
        } catch (Exception e) {
            System.err.println("無法印出 JDBC URL：" + e.getMessage());
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}