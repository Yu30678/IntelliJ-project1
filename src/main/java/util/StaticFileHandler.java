package util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 一個簡單的靜態檔案伺服 handler，
 * 會把指定目錄裡的檔案依照 URL path 拿出來。
 *
 * 例如設定 createContext("/images", new StaticFileHandler("/opt/app/resources/images"))，
 * 那麼 GET /images/foo.jpg 就會去讀 /opt/app/resources/images/foo.jpg 回傳給客戶端。
 */
public class StaticFileHandler implements HttpHandler {
    private final Path baseDir;

    /**
     * @param baseDirPath 本機檔案系統上的根目錄
     */
    public StaticFileHandler(String baseDirPath) {
        this.baseDir = Paths.get(baseDirPath).toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. 只處理 GET
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // 2. 解析 URL path，去掉前綴 context
        String relPath = exchange.getRequestURI().getPath()
                .substring(exchange.getHttpContext().getPath().length());
        if (relPath.isEmpty() || relPath.equals("/")) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // 3. 組成本機檔案路徑，並作安全檢查（避免跳脫到父目錄）
        Path requested = baseDir.resolve(relPath.substring(1)).normalize();
        if (!requested.startsWith(baseDir) || !Files.exists(requested) || Files.isDirectory(requested)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // 4. 自動判斷 Content-Type
        String contentType = URLConnection.guessContentTypeFromName(requested.getFileName().toString());
        if (contentType == null) contentType = "application/octet-stream";
        exchange.getResponseHeaders().set("Content-Type", contentType);

        // 5. 回傳檔案
        byte[] bytes = Files.readAllBytes(requested);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}