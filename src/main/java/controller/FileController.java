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

public class FileController {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
                String fileName;
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");

                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                    fileName = handleMultipartUpload(exchange);
                } else {
                    fileName = handleBinaryUpload(exchange);
                }

                statusCode = 200;
                response.addProperty("status", statusCode);
                response.addProperty("message", "檔案上傳成功");

                JsonObject fileData = new JsonObject();
                fileData.addProperty("fileName", fileName);
                fileData.addProperty("filePath", "/images/" + fileName);
                response.add("data", fileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            response.addProperty("status", statusCode);
            response.addProperty("message", "檔案上傳失敗：" + e.getMessage());
            response.add("data", JsonNull.INSTANCE);
        }

        String jsonResponse = gson.toJson(response);
        byte[] bytes = jsonResponse.getBytes("UTF-8");

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String handleBinaryUpload(HttpExchange exchange) throws IOException {
        String fileName = "img_" + UUID.randomUUID().toString() + ".jpg";
        Path targetPath = Paths.get("/app/images/" + fileName);

        Files.createDirectories(targetPath.getParent());

        try (InputStream inputStream = exchange.getRequestBody()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    private String handleMultipartUpload(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        String boundary = contentType.split("boundary=")[1];

        System.out.println("Content-Type: " + contentType);
        System.out.println("Boundary: " + boundary);

        // 讀取所有資料
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = exchange.getRequestBody()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        }

        byte[] requestBody = baos.toByteArray();
        String bodyStr = new String(requestBody, "ISO-8859-1");

        // Debug: 印出前500個字元來看結構
        System.out.println("Request body preview (first 500 chars):");
        System.out.println(bodyStr.substring(0, Math.min(500, bodyStr.length())));
        System.out.println("--- End of preview ---");

        String boundaryStr = "--" + boundary;
        String[] parts = bodyStr.split(boundaryStr);

        System.out.println("Found " + parts.length + " parts");

        for (int i = 0; i < parts.length; i++) {
            System.out.println("Part " + i + " preview:");
            System.out.println(parts[i].substring(0, Math.min(200, parts[i].length())));
            System.out.println("--- End of part " + i + " ---");

            if (parts[i].contains("name=\"file\"")) {
                System.out.println("Found file part at index " + i);

                int headerEndIndex = parts[i].indexOf("\r\n\r\n");
                System.out.println("Header end index: " + headerEndIndex);

                if (headerEndIndex != -1) {
                    int contentStart = headerEndIndex + 4;
                    int contentEnd = parts[i].length();

                    // 移除結尾的 \r\n
                    if (parts[i].endsWith("\r\n")) {
                        contentEnd = parts[i].length() - 2;
                    }

                    System.out.println("Content start: " + contentStart + ", Content end: " + contentEnd);

                    if (contentStart < contentEnd) {
                        String fileContentStr = parts[i].substring(contentStart, contentEnd);
                        byte[] fileContent = fileContentStr.getBytes("ISO-8859-1");
                        System.out.println("File content length: " + fileContent.length);
                        return saveBinaryContent(fileContent);
                    }
                }
            }
        }

        throw new IOException("無法找到檔案內容");
    }

    private String saveBinaryContent(byte[] content) throws IOException {
        // 根據檔案內容判斷副檔名
        String extension = getFileExtension(content);
        String fileName = "img_" + UUID.randomUUID().toString() + extension;
        Path targetPath = Paths.get("/app/images/" + fileName);

        Files.createDirectories(targetPath.getParent());
        Files.write(targetPath, content);

        return fileName;
    }

    private String getFileExtension(byte[] content) {
        if (content.length >= 4) {
            // JPEG
            if ((content[0] & 0xFF) == 0xFF && (content[1] & 0xFF) == 0xD8) {
                return ".jpg";
            }
            // PNG
            if ((content[0] & 0xFF) == 0x89 && (content[1] & 0xFF) == 0x50 &&
                    (content[2] & 0xFF) == 0x4E && (content[3] & 0xFF) == 0x47) {
                return ".png";
            }
            // GIF
            if ((content[0] & 0xFF) == 0x47 && (content[1] & 0xFF) == 0x49 &&
                    (content[2] & 0xFF) == 0x46) {
                return ".gif";
            }
        }
        return ".jpg"; // 預設
    }
}