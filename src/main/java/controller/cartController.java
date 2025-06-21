package controller;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import util.GsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.cartDAO;
import model.cart;
import java.time.LocalDateTime;
import util.LocalDateTimeAdapter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class cartController implements HttpHandler {

    //private final Gson gson = new Gson();
    private final cartDAO dao = new cartDAO();
    private final Gson gson = GsonUtil.getGson();

    @Override
    public void handle(HttpExchange exchange) {
        try {
            switch (exchange.getRequestMethod()) {
                case "POST" -> handleAdd(exchange);
                case "GET" -> handleGet(exchange);
                case "DELETE" -> handleDelete(exchange);
                case "PUT" -> handleUpdate(exchange);
                default -> exchange.sendResponseHeaders(405, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                exchange.sendResponseHeaders(500, -1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleAdd(HttpExchange exchange) throws Exception {
        JsonObject wrapper = new JsonObject();
        int statusCode;
        
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            cart cartItem = gson.fromJson(reader, cart.class);
            if (cartItem.getCreate_at() == null) {
                cartItem.setCreate_at(java.time.LocalDateTime.now());
            }

            String result = dao.addToCart(cartItem);
            boolean success = result.contains("成功") || result.contains("更新");
            
            statusCode = success ? 200 : 400;
            wrapper.addProperty("status", statusCode);
            wrapper.addProperty("message", result);
            wrapper.add("data", success ? gson.toJsonTree(cartItem) : JsonNull.INSTANCE);
            
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            wrapper.addProperty("status", statusCode);
            wrapper.addProperty("message", "伺服器錯誤：" + e.getMessage());
            wrapper.add("data", JsonNull.INSTANCE);
        }
        
        String response = gson.toJson(wrapper);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleGet(HttpExchange exchange) throws Exception {
        JsonObject wrapper = new JsonObject();
        int statusCode;
        
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject req = gson.fromJson(reader, JsonObject.class);
            
            if (req == null || !req.has("member_id")) {
                statusCode = 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "缺少 member_id 參數");
                wrapper.add("data", JsonNull.INSTANCE);
            } else {
                int memberId = req.get("member_id").getAsInt();
                List<cart> carts = dao.getCartByMemberId(memberId);
                
                statusCode = 200;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "查詢成功");
                wrapper.add("data", gson.toJsonTree(carts));
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            wrapper.addProperty("status", statusCode);
            wrapper.addProperty("message", "伺服器錯誤：" + e.getMessage());
            wrapper.add("data", JsonNull.INSTANCE);
        }
        
        String response = gson.toJson(wrapper);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleDelete(HttpExchange exchange) throws Exception {
        JsonObject wrapper = new JsonObject();
        int statusCode;
        
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject req = gson.fromJson(reader, JsonObject.class);
            
            if (req == null || !req.has("member_id") || !req.has("product_id")) {
                statusCode = 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "缺少 member_id 或 product_id 參數");
                wrapper.add("data", JsonNull.INSTANCE);
            } else {
                int memberId = req.get("member_id").getAsInt();
                int productId = req.get("product_id").getAsInt();

                boolean success = dao.removeFromCart(memberId, productId);
                statusCode = success ? 200 : 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", success ? "移除成功" : "移除失敗");
                wrapper.add("data", JsonNull.INSTANCE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusCode = 500;
            wrapper.addProperty("status", statusCode);
            wrapper.addProperty("message", "伺服器錯誤：" + e.getMessage());
            wrapper.add("data", JsonNull.INSTANCE);
        }
        
        String response = gson.toJson(wrapper);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleUpdate(HttpExchange exchange) throws Exception {
        JsonObject wrapper = new JsonObject();
        int statusCode;
        
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            cart req = gson.fromJson(isr, cart.class);
            
            // 欄位檢查
            if (req.getMember_id() == 0 || req.getProduct_id() == 0) {
                statusCode = 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "member_id 和 product_id 為必要欄位");
                wrapper.add("data", JsonNull.INSTANCE);
            } else {
                try {
                    cartDAO.updateQuantity(req.getMember_id(), req.getProduct_id(), req.getQuantity());
                    statusCode = 200;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "數量更新成功");
                    wrapper.add("data", gson.toJsonTree(req));
                } catch (IllegalArgumentException e) {
                    statusCode = 404;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", e.getMessage());
                    wrapper.add("data", JsonNull.INSTANCE);
                } catch (Exception e) {
                    e.printStackTrace();
                    statusCode = 500;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "更新數量時發生錯誤");
                    wrapper.add("data", JsonNull.INSTANCE);
                }
            }
        } catch (Exception e) {
            statusCode = 400;
            wrapper.addProperty("status", statusCode);
            wrapper.addProperty("message", "請求格式錯誤");
            wrapper.add("data", JsonNull.INSTANCE);
        }
        
        String response = gson.toJson(wrapper);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}