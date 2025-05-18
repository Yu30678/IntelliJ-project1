import com.sun.net.httpserver.HttpServer;
import controller.*;
import util.DBUtil;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException {
        System.out.println(">>> Java backend server 啟動中...");
        System.out.flush();
        System.out.println("啟動伺服器中...");
        // ✅ 加這行強制觸發 DBUtil
        try {
            DBUtil.getConnection().close();
        } catch (Exception e) {
            System.err.println("DB 測試連線失敗：" + e.getMessage());
            e.printStackTrace();
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/member" , new memberController());
        server.createContext("/product", new productController());
        server.createContext("/cart", new cartController());
        server.createContext("/order", new orderController());
        server.createContext("/order_detail", new orderController());
        server.createContext("/user", new userController());

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
