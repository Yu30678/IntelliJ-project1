package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件上傳控制器 - 專門用於商品圖片上傳
 * 支援 form-data 和 binary 兩種上傳方式
 * 儲存路徑：/app/images (Docker容器內)
 */
public class FileController {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // 圖片儲存路徑 - Docker環境下使用容器內的images目錄
    private static final String IMAGE_STORAGE_PATH = "/app/images";
    
    // 支援的圖片格式
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    
    // 最大檔案大小 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 處理檔案上傳請求
     */
    public void handleFileUpload(HttpExchange exchange) throws IOException {
        JsonObject response = new JsonObject();
        int statusCode;

        try {
            if (!"POST".equals(exchange.getRequestMethod())) {
                statusCode = 405;
                response.addProperty("status", statusCode);
                response.addProperty("message", "只允許 POST 請求");
                response.add("data", JsonNull.INSTANCE);
            } else {
                // 確保儲存目錄存在
                ensureImageDirectoryExists();
                
                String fileName;
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");

                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    fileName = handleFormDataUpload(exchange);
                } else {
                    fileName = handleBinaryUpload(exchange);
                }

                statusCode = 200;
                response.addProperty("status", statusCode);
                response.addProperty("message", "商品圖片上傳成功");

                JsonObject fileData = new JsonObject();
                fileData.addProperty("fileName", fileName);
                fileData.addProperty("filePath", "/images/" + fileName);
                fileData.addProperty("fullUrl", "http://localhost:8081/images/" + fileName);
                response.add("data", fileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            response.addProperty("status", statusCode);
            response.addProperty("message", "檔案上傳失敗：" + e.getMessage());
            response.add("data", JsonNull.INSTANCE);
        }

        sendJsonResponse(exchange, statusCode, response);
    }

    /**
     * 處理 binary 格式上傳
     */
    private String handleBinaryUpload(HttpExchange exchange) throws IOException {
        // 檢查內容長度
        String contentLengthStr = exchange.getRequestHeaders().getFirst("Content-Length");
        if (contentLengthStr != null) {
            long contentLength = Long.parseLong(contentLengthStr);
            if (contentLength > MAX_FILE_SIZE) {
                throw new IOException("檔案大小超過限制 (最大 5MB)");
            }
        }

        // 讀取檔案內容
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream inputStream = exchange.getRequestBody()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
                if (totalBytes > MAX_FILE_SIZE) {
                    throw new IOException("檔案大小超過限制 (最大 5MB)");
                }
                baos.write(buffer, 0, bytesRead);
            }
        }

        byte[] fileContent = baos.toByteArray();
        if (fileContent.length == 0) {
            throw new IOException("檔案內容為空");
        }

        return saveImageFile(fileContent);
    }

    /**
     * 處理 form-data 格式上傳
     */
    private String handleFormDataUpload(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        String boundary = extractBoundary(contentType);
        
        if (boundary == null) {
            throw new IOException("無法解析 multipart boundary");
        }

        // 讀取整個請求體
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = exchange.getRequestBody()) {
            byte[] buffer = new byte[8192];
            int length;
            long totalBytes = 0;
            
            while ((length = is.read(buffer)) != -1) {
                totalBytes += length;
                if (totalBytes > MAX_FILE_SIZE) {
                    throw new IOException("檔案大小超過限制 (最大 5MB)");
                }
                baos.write(buffer, 0, length);
            }
        }

        byte[] requestBody = baos.toByteArray();
        return parseMultipartData(requestBody, boundary);
    }

    /**
     * 解析 multipart 數據
     */
    private String parseMultipartData(byte[] requestBody, String boundary) throws IOException {
        String bodyStr = new String(requestBody, "ISO-8859-1");
        String boundaryStr = "--" + boundary;
        String[] parts = bodyStr.split(boundaryStr);

        for (String part : parts) {
            if (part.contains("name=\"file\"") || part.contains("name=\"image\"")) {
                int headerEndIndex = part.indexOf("\r\n\r\n");
                if (headerEndIndex != -1) {
                    int contentStart = headerEndIndex + 4;
                    int contentEnd = part.length();

                    // 移除結尾的換行符
                    if (part.endsWith("\r\n")) {
                        contentEnd = part.length() - 2;
                    }

                    if (contentStart < contentEnd) {
                        String fileContentStr = part.substring(contentStart, contentEnd);
                        byte[] fileContent = fileContentStr.getBytes("ISO-8859-1");
                        return saveImageFile(fileContent);
                    }
                }
            }
        }

        throw new IOException("無法找到檔案內容");
    }

    /**
     * 儲存圖片檔案
     */
    private String saveImageFile(byte[] content) throws IOException {
        // 根據檔案內容判斷格式
        String extension = detectImageFormat(content);
        if (extension == null) {
            throw new IOException("不支援的圖片格式，僅支援 JPG、PNG、GIF");
        }

        // 生成唯一檔名
        String fileName = "product_" + UUID.randomUUID().toString() + extension;
        Path targetPath = Paths.get(IMAGE_STORAGE_PATH, fileName);

        // 儲存檔案
        Files.write(targetPath, content);
        
        System.out.println("✅ 商品圖片上傳成功: " + fileName + " (大小: " + content.length + " bytes)");
        return fileName;
    }

    /**
     * 根據檔案內容檢測圖片格式
     */
    private String detectImageFormat(byte[] content) {
        if (content.length < 4) {
            return null;
        }

        // JPEG 魔術字節: FF D8
        if ((content[0] & 0xFF) == 0xFF && (content[1] & 0xFF) == 0xD8) {
            return ".jpg";
        }
        
        // PNG 魔術字節: 89 50 4E 47
        if (content.length >= 8 && 
            (content[0] & 0xFF) == 0x89 && (content[1] & 0xFF) == 0x50 &&
            (content[2] & 0xFF) == 0x4E && (content[3] & 0xFF) == 0x47) {
            return ".png";
        }
        
        // GIF 魔術字節: 47 49 46
        if ((content[0] & 0xFF) == 0x47 && (content[1] & 0xFF) == 0x49 &&
            (content[2] & 0xFF) == 0x46) {
            return ".gif";
        }

        return null;
    }

    /**
     * 從 Content-Type 中提取 boundary
     */
    private String extractBoundary(String contentType) {
        if (contentType == null || !contentType.contains("boundary=")) {
            return null;
        }
        
        String[] parts = contentType.split("boundary=");
        if (parts.length > 1) {
            return parts[1].trim();
        }
        
        return null;
    }

    /**
     * 確保圖片儲存目錄存在
     */
    private void ensureImageDirectoryExists() throws IOException {
        Path imageDir = Paths.get(IMAGE_STORAGE_PATH);
        System.out.println("🔍 檢查圖片目錄: " + imageDir.toAbsolutePath());
        
        if (!Files.exists(imageDir)) {
            Files.createDirectories(imageDir);
            System.out.println("📁 創建圖片儲存目錄: " + imageDir.toAbsolutePath());
        } else {
            System.out.println("✅ 圖片目錄已存在: " + imageDir.toAbsolutePath());
        }
    }

    /**
     * 發送 JSON 回應
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, JsonObject response) throws IOException {
        String jsonResponse = gson.toJson(response);
        byte[] bytes = jsonResponse.getBytes("UTF-8");

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}