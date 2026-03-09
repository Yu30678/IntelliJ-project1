package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.orderDAO;
import dao.cartDAO;
import dao.productDAO;
import model.order;
import model.cart;
import model.product;
import model.order_detail;

import util.LocalDateTimeAdapter;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class orderController implements HttpHandler {
    private final cartDAO cartDAO = new cartDAO();
    private final productDAO productDAO = new productDAO();
    private final orderDAO orderDAO = new orderDAO();
    public orderController() {
        System.out.println("orderController created and registered");
    }
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("收到請求: " + exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query  = exchange.getRequestURI().getRawQuery();
        if (!"/order".equals(path)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        try {
            switch (method) {
                case "POST"   -> handleCreate(exchange);
                case "GET"    -> handleQuery(exchange);
                case "PUT"    -> handleUpdate(exchange);
                case "DELETE" -> handleDelete(exchange);
                default        -> exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private void handleCreate(HttpExchange ex) throws IOException {
        JsonObject response = new JsonObject();
        int statusCode;
        
        try (InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null || !json.has("member_id") || json.get("member_id").isJsonNull()) {
                statusCode = 400;
                response.addProperty("status", statusCode);
                response.addProperty("message", "member_id 為必要參數");
                response.add("data", null);
            } else {
                int memberId = json.get("member_id").getAsInt();
                // --- 先把這個會員的購物車內容撈出來 ---
                List<cart> carts = cartDAO.getCartByMemberId(memberId);
                if (carts.isEmpty()) {
                    statusCode = 400;
                    response.addProperty("status", statusCode);
                    response.addProperty("message", "購物車為空");
                    response.add("data", null);
                } else {
                    // 檢查購物車商品狀態
                    boolean validationFailed = false;
                    String errorMsg = "";
                    
                    for (cart c : carts) {
                        product p = productDAO.getProductById(c.getProduct_id());
                        if (!p.isIs_active()) {
                            validationFailed = true;
                            errorMsg = String.format("商品 %d 已下架", p.getProduct_id());
                            break;
                        }
                        if (c.getQuantity() > p.getSoh()) {
                            validationFailed = true;
                            errorMsg = String.format("商品 %d 庫存不足", p.getProduct_id());
                            break;
                        }
                    }
                    
                    if (validationFailed) {
                        statusCode = 400;
                        response.addProperty("status", statusCode);
                        response.addProperty("message", errorMsg);
                        response.add("data", null);
                    } else {
                        // 全部檢查通過，建立訂單
                        int newId = orderDAO.placeOrderFromCart(memberId);
                        statusCode = 201;
                        response.addProperty("status", statusCode);
                        response.addProperty("message", "訂單建立成功");
                        JsonObject orderData = new JsonObject();
                        orderData.addProperty("order_id", newId);
                        response.add("data", orderData);
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            statusCode = 400;
            response.addProperty("status", statusCode);
            response.addProperty("message", "JSON 格式錯誤");
            response.add("data", null);
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            response.addProperty("status", statusCode);
            response.addProperty("message", "建立訂單失敗: " + e.getMessage());
            response.add("data", null);
        }
        
        String jsonResponse = gson.toJson(response);
        sendJson(ex, statusCode, jsonResponse);
    }

    private void handleQuery(HttpExchange ex) throws IOException {
        String q = ex.getRequestURI().getQuery();
        try {
            if (q != null && q.startsWith("member_id=")) {
                int memberId = Integer.parseInt(q.split("=")[1]);
                List<order> orderList = orderDAO.getOrdersWithDetailsByMemberId(memberId);

                JsonObject response = new JsonObject();
                response.addProperty("status", 200);
                response.addProperty("message", "查詢成功");
                response.add("data", gson.toJsonTree(orderList));
                
                sendJson(ex, 200, gson.toJson(response));
            } else {
                sendJson(ex, 400, "{\"error\":\"member_id parameter required\"}");
            }
        } catch (NumberFormatException e) {
            sendJson(ex, 400, "{\"error\":\"Invalid member_id format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, "{\"error\":\"load failed\"}");
        }
    }

    private void handleUpdate(HttpExchange ex) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
            order o = gson.fromJson(reader, order.class);
            if (o.getOrder_id() == 0) {
                sendJson(ex, 400, "{\"error\":\"order_id required\"}");
                return;
            }
            orderDAO.updateOrder(o);
            sendJson(ex, 200, "{\"status\":\"updated\"}");
        } catch (JsonSyntaxException e) {
            sendJson(ex, 400, "{\"error\":\"invalid JSON\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, "{\"error\":\"update failed\"}");
        }
    }

    private void handleDelete(HttpExchange ex) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null || !json.has("order_id") || json.get("order_id").isJsonNull()) {
                sendJson(ex, 400, "{\"error\":\"order_id required\"}");
                return;
            }
            int orderId = json.get("order_id").getAsInt();
            orderDAO.deleteOrder(orderId);
            sendJson(ex, 200, "{\"status\":\"deleted\"}");
        } catch (JsonSyntaxException e) {
            sendJson(ex, 400, "{\"error\":\"invalid JSON\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, "{\"error\":\"delete failed\"}");
        }
    }

    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

}
