package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.UUID;
import util.GoogleDriveService;

/**
 * 文件上傳控制器 - 專門用於商品圖片上傳
 * 支援 form-data 和 binary 兩種上傳方式
 * 儲存至：Google Drive
 */
public class FileController {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final GoogleDriveService driveService;
    
    // 支援的圖片格式
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    
    // 最大檔案大小 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    public FileController() {
        try {
            this.driveService = new GoogleDriveService();
            System.out.println("✅ Google Drive 服務初始化成功");
        } catch (Exception e) {
            throw new RuntimeException("Google Drive 服務初始化失敗: " + e.getMessage(), e);
        }
    }

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
                GoogleDriveService.DriveUploadResult uploadResult;
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");

                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    uploadResult = handleFormDataUpload(exchange);
                } else {
                    uploadResult = handleBinaryUpload(exchange);
                }

                statusCode = 200;
                response.addProperty("status", statusCode);
                response.addProperty("message", "商品圖片上傳成功");

                JsonObject fileData = new JsonObject();
                fileData.addProperty("fileName", uploadResult.getFileName());
                fileData.addProperty("fileId", uploadResult.getFileId());
                fileData.addProperty("directUrl", uploadResult.getDirectAccessUrl());
                fileData.addProperty("thumbnailUrl", uploadResult.getThumbnailUrl());
                fileData.addProperty("downloadUrl", uploadResult.getDownloadUrl());
                fileData.addProperty("webViewLink", uploadResult.getWebViewLink());
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
    private GoogleDriveService.DriveUploadResult handleBinaryUpload(HttpExchange exchange) throws IOException {
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

        return uploadImageToDrive(fileContent);
    }

    /**
     * 處理 form-data 格式上傳
     */
    private GoogleDriveService.DriveUploadResult handleFormDataUpload(HttpExchange exchange) throws IOException {
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
    private GoogleDriveService.DriveUploadResult parseMultipartData(byte[] requestBody, String boundary) throws IOException {
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
                        return uploadImageToDrive(fileContent);
                    }
                }
            }
        }

        throw new IOException("無法找到檔案內容");
    }

    /**
     * 上傳圖片到 Google Drive
     */
    private GoogleDriveService.DriveUploadResult uploadImageToDrive(byte[] content) throws IOException {
        // 根據檔案內容判斷格式
        String extension = detectImageFormat(content);
        if (extension == null) {
            throw new IOException("不支援的圖片格式，僅支援 JPG、PNG、GIF");
        }

        // 生成唯一檔名
        String fileName = GoogleDriveService.generateUniqueFileName(extension);
        
        // 決定 MIME 類型
        String mimeType = getMimeTypeFromExtension(extension);

        // 上傳到 Google Drive
        GoogleDriveService.DriveUploadResult result = driveService.uploadImage(content, fileName, mimeType);
        
        System.out.println("✅ 商品圖片上傳到 Google Drive 成功: " + fileName + " (大小: " + content.length + " bytes)");
        System.out.println("📂 Google Drive ID: " + result.getFileId());
        System.out.println("🔗 直接存取 URL: " + result.getDirectAccessUrl());
        
        return result;
    }

    /**
     * 根據副檔名取得 MIME 類型
     */
    private String getMimeTypeFromExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            default -> "application/octet-stream";
        };
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