package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.orderDAO;
import util.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class orderController implements HttpHandler {
    public orderController() {
        System.out.println("✅ orderController created and registered");
    }
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("📦 收到請求: " + exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try {
            if ("POST".equalsIgnoreCase(method) && "/order".equals(path)) {
                handleCreate(exchange);
            } else if ("GET".equalsIgnoreCase(method) && "/order".equals(path)) {
                handleList(exchange);
            } else if ("GET".equalsIgnoreCase(method) && "/order_detail".equals(path)) {
                handleDetail(exchange);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleCreate(HttpExchange exchange) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        int memberId = Integer.parseInt(reader.readLine());

        if (!orderDAO.validateCartBeforeOrder(memberId)) {
            sendResponse(exchange, 400, gson.toJson("Cart invalid or out of stock"));
            return;
        }

        int orderId = orderDAO.placeOrderFromCart(memberId);
        sendResponse(exchange, 201, gson.toJson("Order created: " + orderId));
        System.out.println("📥 收到訂單建立請求");
    }

    private void handleList(HttpExchange exchange) throws Exception {
        int memberId = Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
        List<?> orders = orderDAO.getOrdersByMemberId(memberId);
        sendResponse(exchange, 200, gson.toJson(orders));
    }

    private void handleDetail(HttpExchange exchange) throws Exception {
        int orderId = Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
        List<?> details = orderDAO.getOrderDetailsByOrderId(orderId);
        sendResponse(exchange, 200, gson.toJson(details));
    }

    private void sendResponse(HttpExchange exchange, int status, String resp) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, resp.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp.getBytes());
        }
    }
}
