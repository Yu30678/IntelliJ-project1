import com.sun.net.httpserver.HttpServer;
import controller.*;
import util.DBUtil;
import util.StaticFileHandler;
import util.CORSWrapperHandler;
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
        FileController fileController = new FileController();

        server.createContext("/member" , new CORSWrapperHandler(new memberController()));
        server.createContext("/product", new CORSWrapperHandler(new productController()));
        server.createContext("/cart", new CORSWrapperHandler(new cartController()));
        server.createContext("/order", new CORSWrapperHandler(new orderController()));
        server.createContext("/order_detail", new CORSWrapperHandler(new orderController()));
        server.createContext("/user", new CORSWrapperHandler(new userController()));
        server.createContext("/api/upload", fileController::handleFileUpload);
        // Docker環境下使用容器內的images目錄
        String imagesPath = "/app/images";
        System.out.println("🖼️ 圖片服務路徑: " + imagesPath);
        server.createContext("/images", new CORSWrapperHandler(
            new StaticFileHandler(imagesPath)
        ));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
