package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.S3Util;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class S3Controller {

    // ===== GET /s3/list =====
    public static HttpHandler list() {
        return exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            try {
                List<String> files = S3Util.listFiles();
                String json = toJsonArray(files);
                sendJsonResponse(exchange, 200, json);
            } catch (Exception e) {
                sendResponse(exchange, 500, e.getMessage());
            }
        };
    }

    // ===== POST /s3/upload =====
    // Header 需帶：X-Object-Key: your/path/filename.txt
    public static HttpHandler upload() {
        return exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            try {
                String rawKey = exchange.getRequestHeaders().getFirst("X-Object-Key");
                if (rawKey == null || rawKey.isBlank()) {
                    sendResponse(exchange, 400, "Missing header: X-Object-Key");
                    return;
                }
                String objectKey = URLDecoder.decode(rawKey, StandardCharsets.UTF_8);
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
                if (contentType == null) contentType = "application/octet-stream";

                InputStream body = exchange.getRequestBody();
                byte[] bytes = body.readAllBytes();

                S3Util.uploadStream(
                        objectKey,
                        new ByteArrayInputStream(bytes),
                        bytes.length,
                        contentType
                );

                sendJsonResponse(exchange, 200,
                        "{\"message\":\"上傳成功\",\"objectKey\":\"" + objectKey + "\"}");

            } catch (Exception e) {
                sendResponse(exchange, 500, e.getMessage());
            }
        };
    }

    // ===== GET /s3/download?key=your/path/filename.txt =====
    public static HttpHandler download() {
        return exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            try {
                String objectKey = getQueryParam(exchange, "key");
                if (objectKey == null || objectKey.isBlank()) {
                    sendResponse(exchange, 400, "Missing query param: key");
                    return;
                }

                byte[] fileBytes = S3Util.downloadToBytes(objectKey);
                String fileName = objectKey.substring(objectKey.lastIndexOf("/") + 1);

                exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                exchange.getResponseHeaders().set("Content-Disposition",
                        "attachment; filename=\"" + fileName + "\"");
                exchange.sendResponseHeaders(200, fileBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileBytes);
                }

            } catch (Exception e) {
                sendResponse(exchange, 500, e.getMessage());
            }
        };
    }

    // ===== DELETE /s3/delete?key=your/path/filename.txt =====
    public static HttpHandler delete() {
        return exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            try {
                String objectKey = getQueryParam(exchange, "key");
                if (objectKey == null || objectKey.isBlank()) {
                    sendResponse(exchange, 400, "Missing query param: key");
                    return;
                }

                S3Util.deleteFile(objectKey);
                sendJsonResponse(exchange, 200,
                        "{\"message\":\"刪除成功\",\"objectKey\":\"" + objectKey + "\"}");

            } catch (Exception e) {
                sendResponse(exchange, 500, e.getMessage());
            }
        };
    }

    // ==================== 工具方法 ====================

    private static String getQueryParam(HttpExchange exchange, String paramName) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && kv[0].equals(paramName)) {
                return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private static String toJsonArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void sendJsonResponse(HttpExchange exchange, int code, String json)
            throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendResponse(HttpExchange exchange, int code, String message)
            throws IOException {
        String json = "{\"error\":\"" + message + "\"}";
        sendJsonResponse(exchange, code, json);
    }
}