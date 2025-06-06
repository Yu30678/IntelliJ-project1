import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controller.*;
import util.DBUtil;
import util.StaticFileHandler;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    
    // CORS wrapper class
    static class CorsHandler implements HttpHandler {
        private final HttpHandler wrapped;
        private final String allowedOrigin;
        
        public CorsHandler(HttpHandler wrapped, String allowedOrigin) {
            this.wrapped = wrapped;
            this.allowedOrigin = allowedOrigin;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 設定 CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", allowedOrigin);
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
            exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
            
            // 處理 preflight requests (OPTIONS)
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            // 委託給原始 handler
            wrapped.handle(exchange);
        }
    }
    
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
        
        String allowedOrigin = "http://localhost:3000";

        server.createContext("/member" , new CorsHandler(new memberController(), allowedOrigin));
        server.createContext("/product", new CorsHandler(new productController(), allowedOrigin));
        server.createContext("/cart", new CorsHandler(new cartController(), allowedOrigin));
        server.createContext("/order", new CorsHandler(new orderController(), allowedOrigin));
        server.createContext("/order_detail", new CorsHandler(new orderController(), allowedOrigin));
        server.createContext("/user", new CorsHandler(new userController(), allowedOrigin));
        
        //server.createContext("/images", new StaticFileHandler("/opt/images"));
        server.createContext("/images", new CorsHandler(new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String relPath = exchange.getRequestURI().getPath().replaceFirst("/images/", "");
                Path file = Paths.get("/opt/images").resolve(relPath).normalize();
                if (!Files.exists(file)) {
                    // fallback 回本地
                    file = Paths.get("src/main/resources/images").resolve(relPath).normalize();
                }

                if (Files.exists(file) && !Files.isDirectory(file)) {
                    String mime = URLConnection.guessContentTypeFromName(file.toString());
                    if (mime == null) mime = "application/octet-stream";
                    exchange.getResponseHeaders().set("Content-Type", mime);
                    byte[] data = Files.readAllBytes(file);
                    exchange.sendResponseHeaders(200, data.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(data);
                    }
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
        }, allowedOrigin));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
        System.out.println("CORS enabled for: " + allowedOrigin);
    }
}
