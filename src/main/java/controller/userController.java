package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.categoryDAO;
import dao.memberDAO;
import dao.orderDAO;
import dao.productDAO;
import model.Category;
import model.Member;
import model.order;
import model.product;
import util.LocalDateTimeAdapter;
import java.sql.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class userController implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final memberDAO mDao = new memberDAO();
    private final orderDAO oDao = new orderDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod();
        try {
            if (path.startsWith("/user/members")) {
                handleMembers(ex, method);
            } else if (path.startsWith("/user/categories")) {
                handleCategories(ex, method);
            } else if (path.startsWith("/user/products")) {
                handleProducts(ex, method);
            } else if (path.startsWith("/user/orders")) {
                handleOrders(ex, method);
            } else {
                ex.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ex.sendResponseHeaders(500, -1);
        }
    }

    private void handleMembers(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                String query = ex.getRequestURI().getQuery();
                String json;
                if (query != null && query.startsWith("member_id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    Member m = mDao.getMemberById(id);
                    json = gson.toJson(m);
                } else {
                    List<Member> list = mDao.getAllMembers();
                    json = gson.toJson(list);
                }
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(200, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            case "POST": {
                InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8);
                Member m = gson.fromJson(reader, Member.class);
                if (m.getCreate_at() == null) m.setCreate_at(LocalDateTime.now());
                boolean ok = mDao.insertMember(m);
                String json = ok ? "{\"status\":\"created\"}" : "{\"error\":\"create failed\"}";
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(ok ? 201 : 400, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            case "PUT": {
                InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8);
                Member m = gson.fromJson(reader, Member.class);
                boolean ok = mDao.updateMember(m);
                String json = ok ? "{\"status\":\"updated\"}" : "{\"error\":\"update failed\"}";
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(ok ? 200 : 400, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            case "DELETE": {
                InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8);
                Map<String, Object> data = gson.fromJson(reader, Map.class);
                Number idNum = (Number) data.get("member_id");
                if (idNum == null) {
                    String err = "{\"error\":\"missing member_id\"}";
                    byte[] resp = err.getBytes(StandardCharsets.UTF_8);
                    ex.sendResponseHeaders(400, resp.length);
                    try (OutputStream os = ex.getResponseBody()) {
                        os.write(resp);
                    }
                    break;
                }
                boolean ok = mDao.deleteMember(idNum.intValue());
                String json = ok ? "{\"status\":\"deleted\"}" : "{\"error\":\"delete failed\"}";
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(ok ? 200 : 400, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            default:
                ex.sendResponseHeaders(405, -1);
        }
    }

    private void handleCategories(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                List<Category> list = categoryDAO.getAllCategories();
                String json = gson.toJson(list);
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(200, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            case "POST": {
                InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8);
                Category c = gson.fromJson(reader, Category.class);
                categoryDAO.insertCategory(c);
                String json = "{\"status\":\"created\"}";
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(201, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            case "PUT": {
                InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8);
                Category c = gson.fromJson(reader, Category.class);
                categoryDAO.updateCategory(c);
                String json = "{\"status\":\"updated\"}";
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(200, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            case "DELETE": {
                InputStreamReader reader = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8);
                Map<String, Object> data = gson.fromJson(reader, Map.class);
                Number idNum = (Number) data.get("category_id");
                if (idNum != null) {
                    categoryDAO.deleteCategory(idNum.intValue());
                    String json = "{\"status\":\"deleted\"}";
                    byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                    ex.sendResponseHeaders(200, resp.length);
                    try (OutputStream os = ex.getResponseBody()) {
                        os.write(resp);
                    }
                } else {
                    ex.sendResponseHeaders(400, -1);
                }
                break;
            }
            default:
                ex.sendResponseHeaders(405, -1);
        }
    }

    private void handleProducts(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                List<product> list = productDAO.getAllProducts();
                String json = gson.toJson(list);
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(200, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
            case "POST": {
                product p = fromJson(ex, product.class);
                try {
                    productDAO.insertProduct(p);
                    sendJson(ex, 201, "{\"status\":\"created\"}");
                } catch (SQLIntegrityConstraintViolationException fk) {
                    sendJson(ex, 400, "{\"error\":\"invalid category_id\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, "{\"error\":\"create failed\"}");
                }
                break;
            }

            case "PUT": {
                product p = fromJson(ex, product.class);
                try {
                    productDAO.updateProduct(p);
                    sendJson(ex, 200, "{\"status\":\"updated\"}");
                } catch (SQLIntegrityConstraintViolationException fk) {
                    sendJson(ex, 400, "{\"error\":\"invalid data or FK violation\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, "{\"error\":\"update failed\"}");
                }
                break;
            }

            case "DELETE": {
                // 讀進 Map 以抓 product_id
                Map<String, Object> map = fromJson(ex, Map.class);
                Number idNum = (Number) map.get("product_id");
                if (idNum == null) {
                    sendJson(ex, 400, "{\"error\":\"missing product_id\"}");
                    break;
                }
                int id = idNum.intValue();

                try {
                    productDAO.deleteProduct(id);
                    sendJson(ex, 200, "{\"status\":\"deleted\"}");
                } catch (SQLIntegrityConstraintViolationException fk) {
                    sendJson(ex, 400,
                            "{\"error\":\"cannot delete: product is referenced\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, "{\"error\":\"delete failed\"}");
                }
                break;
            }

            default:
                ex.sendResponseHeaders(405, -1);
        }
    }

    // ---------- helper methods below ----------
    private <T> T fromJson(HttpExchange ex, Class<T> clazz) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                ex.getRequestBody(), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(isr, clazz);
        }
    }

    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleOrders(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                String query = ex.getRequestURI().getQuery();
                String json;
                if (query != null && query.startsWith("member_id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    List<order> m = oDao.getOrdersByMemberId(id);
                    json = gson.toJson(m);
                } else {
                    List<order> list = oDao.getAllOrders();
                    json = gson.toJson(list);
                }
                byte[] resp = json.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(200, resp.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(resp);
                }
                break;
            }
        }
    }
}