package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.productDAO;
import model.product;
import util.LocalDateTimeAdapter;
import java.time.LocalDateTime;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class productController implements HttpHandler {
    //private final Gson gson = new Gson();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod();
    String path = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("status", 405);
                errorResponse.addProperty("message", "不支援的HTTP方法");
                errorResponse.add("data", JsonNull.INSTANCE);
                sendResponse(exchange, 405, gson.toJson(errorResponse));
        }
    }
    //取得所有商品資訊
    private void handleGet(HttpExchange exchange) throws IOException {
        JsonObject response = new JsonObject();
        int statusCode;
        
        try {
            productDAO.deactivateOutOfStockProducts();
            List<product> products = productDAO.getAllProducts();
            statusCode = 200;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品查詢成功");
            response.add("data", gson.toJsonTree(products));
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 400;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品查詢失敗：" + e.getMessage());
            response.add("data", JsonNull.INSTANCE);
        }
        
        sendResponse(exchange, statusCode, gson.toJson(response));
    }
    //新增商品
    private void handlePost(HttpExchange exchange) throws IOException {
        JsonObject response = new JsonObject();
        int statusCode;
        
        try {
            product newProduct = parseRequestBody(exchange, product.class);
            productDAO.insertProduct(newProduct);
            statusCode = 201;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品新增成功");
            response.add("data", gson.toJsonTree(newProduct));
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品新增失敗：" + e.getMessage());
            response.add("data", JsonNull.INSTANCE);
        }
        
        sendResponse(exchange, statusCode, gson.toJson(response));
    }
    //更新商品資訊
    private void handlePut(HttpExchange exchange) throws IOException {
        JsonObject response = new JsonObject();
        int statusCode;
        
        try {
            product updatedProduct = parseRequestBody(exchange, product.class);
            productDAO.updateProduct(updatedProduct);
            statusCode = 200;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品更新成功");
            response.add("data", gson.toJsonTree(updatedProduct));
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品更新失敗：" + e.getMessage());
            response.add("data", JsonNull.INSTANCE);
        }
        
        sendResponse(exchange, statusCode, gson.toJson(response));
    }
    //刪除商品
    private void handleDelete(HttpExchange exchange) throws IOException {
        JsonObject response = new JsonObject();
        int statusCode;
        
        try {
            product deletProduct = parseRequestBody(exchange, product.class);
            productDAO.deleteProduct(deletProduct.getProduct_id());
            statusCode = 200;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品刪除成功");
            response.add("data", JsonNull.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            response.addProperty("status", statusCode);
            response.addProperty("message", "商品刪除失敗：" + e.getMessage());
            response.add("data", JsonNull.INSTANCE);
        }
        
        sendResponse(exchange, statusCode, gson.toJson(response));
        System.out.println("🗑️ productController.handleDelete invoked");
    }

    private <T> T parseRequestBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        return gson.fromJson(isr, clazz);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        // 只呼叫一次 sendResponseHeaders
        exchange.sendResponseHeaders(statusCode, bytes.length);
        // 只寫一次 body
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}