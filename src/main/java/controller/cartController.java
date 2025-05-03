package controller;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
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
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

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
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        cart cartItem = gson.fromJson(reader, cart.class);
        if (cartItem.getCreate_at() == null) {
            cartItem.setCreate_at(java.time.LocalDateTime.now());
        }

        String result = dao.addToCart(cartItem);

        byte[] responseBytes = result.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(result.contains("成功") || result.contains("更新") ? 200 : 400, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void handleGet(HttpExchange exchange) throws Exception {
        String query = exchange.getRequestURI().getQuery();
        int memberId = Integer.parseInt(query.split("=")[1]);

        List<cart> carts = dao.getCartByMemberId(memberId);
        String response = gson.toJson(carts);

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleDelete(HttpExchange exchange) throws Exception {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            // 回 400 Bad Request 或其他錯誤訊息
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        String[] params = query.split("&");
        int memberId = Integer.parseInt(params[0].split("=")[1]);
        int productId = Integer.parseInt(params[1].split("=")[1]);

        boolean success = dao.removeFromCart(memberId, productId);
        String response = success ? "移除成功" : "移除失敗";

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(success ? 200 : 400, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleUpdate(HttpExchange exchange) throws Exception {
        // 解析 JSON 請求
        cart req;
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            req = gson.fromJson(isr, cart.class);
        } catch (Exception e) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        // 欄位檢查
        if (req.getMember_id() == 0 || req.getProduct_id() == 0) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        try {
            cartDAO.updateQuantity(req.getMember_id(), req.getProduct_id(), req.getQuantity());
            String resp = gson.toJson("Quantity updated");
            exchange.sendResponseHeaders(200, resp.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp.getBytes());
            }
        } catch (IllegalArgumentException e) {
            String resp = gson.toJson(e.getMessage());
            exchange.sendResponseHeaders(404, resp.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            String resp = gson.toJson("Error updating quantity");
            exchange.sendResponseHeaders(500, resp.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp.getBytes());
            }

        }
    }
}