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
        }catch (Exception e) {
            e.printStackTrace();  // <-- 加這個

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
}
