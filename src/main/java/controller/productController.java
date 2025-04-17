package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.productDAO;
import model.product;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class productController implements HttpHandler {
    private final Gson gson = new Gson();

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

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            List<product> products = productDAO.getAllProducts();
            String response = gson.toJson(products);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, gson.toJson("Error loading products"));
        }
    }

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

    private void handleDelete(HttpExchange exchange) throws IOException {
        product productToDelete = parseRequestBody(exchange, product.class);
        try {
            productDAO.deleteProduct(productToDelete.getProduct_id());
            sendResponse(exchange, 200, gson.toJson("Product deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, gson.toJson("Error deleting product"));
        }
    }

    private <T> T parseRequestBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        return gson.fromJson(isr, clazz);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}