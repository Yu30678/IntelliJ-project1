package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
    //取得所有商品資訊
    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            productDAO.deactivateOutOfStockProducts();
            List<product> products = productDAO.getAllProducts();
            String response = gson.toJson(products);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, gson.toJson("Error loading products"));
        }
    }
    //新增商品
    private void handlePost(HttpExchange exchange) throws IOException {
        product newProduct = parseRequestBody(exchange, product.class);
        try {
            productDAO.insertProduct(newProduct);
            sendResponse(exchange, 201, gson.toJson("Product created successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, gson.toJson("Error creating product"));
        }
    }
    //更新商品資訊
    private void handlePut(HttpExchange exchange) throws IOException {
        product updatedProduct = parseRequestBody(exchange, product.class);
        try {
            productDAO.updateProduct(updatedProduct);
            sendResponse(exchange, 200, gson.toJson("Product updated successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, gson.toJson("Error updating product"));
        }
    }
    //刪除商品
    private void handleDelete(HttpExchange exchange) throws IOException {
        product deletProduct = parseRequestBody(exchange, product.class);
        try {
            productDAO.deleteProduct(deletProduct.getProduct_id());
            sendResponse(exchange, 200, gson.toJson("Product deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, gson.toJson("Error deleting product"));
        }
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