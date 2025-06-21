package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.*;
import model.*;
import util.LocalDateTimeAdapter;
import java.sql.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class userController implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final memberDAO mDao = new memberDAO();
    private final orderDAO oDao = new orderDAO();
    private final productDAO pDao = new productDAO();
    private final userDAO uDao = new userDAO();

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
            }else if (path.startsWith("/user/users")) {
                handleUsers(ex, method);
            }else if (path.startsWith("/user/carts")) {
                handleCarts(ex, method);
            } else {
                sendJson(ex, 404, makeResp(404, "路徑不存在", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(ex, 500, makeResp(500, "伺服器內部錯誤: " + e.getMessage(), null));
        }
    }

    private void handleMembers(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                String query = ex.getRequestURI().getQuery();
                if (query != null && query.startsWith("member_id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    Member m = mDao.getMemberById(id);
                    sendJson(ex, 200, makeResp(200, "查詢成功", m));
                } else {
                    List<Member> list = mDao.getAllMembers();
                    sendJson(ex, 200, makeResp(200, "查詢成功", list));
                }
                break;
            }
            case "POST": {
                Member m = fromJson(ex, Member.class);
                if (m.getCreate_at() == null) m.setCreate_at(LocalDateTime.now());
                boolean ok = mDao.insertMember(m);
                if (ok) {
                    sendJson(ex, 201, makeResp(201, "註冊成功", m));
                } else {
                    sendJson(ex, 400, makeResp(400, "註冊失敗", null));
                }
                break;
            }
            case "PUT": {
                Member m = fromJson(ex, Member.class);
                boolean ok = mDao.updateMember(m);
                if (ok) {
                    sendJson(ex, 200, makeResp(200, "修改成功", m));
                } else {
                    sendJson(ex, 400, makeResp(400, "修改失敗", null));
                }
                break;
            }
            case "DELETE": {
                Map<String, Object> data = fromJson(ex, Map.class);
                Number idNum = (Number) data.get("member_id");
                if (idNum == null) {
                    sendJson(ex, 400, makeResp(400, "缺少 member_id", null));
                    break;
                }
                boolean ok = mDao.deleteMember(idNum.intValue());
                if (ok) {
                    sendJson(ex, 200, makeResp(200, "刪除成功", null));
                } else {
                    sendJson(ex, 400, makeResp(400, "刪除失敗", null));
                }
                break;
            }
            default:
                sendJson(ex, 405, makeResp(405, "方法不支援", null));
        }
    }

    private void handleCategories(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                List<Category> list = categoryDAO.getAllCategories();
                sendJson(ex, 200, makeResp(200, "查詢成功", list));
                break;
            }
            case "POST": {
                Category c = fromJson(ex, Category.class);
                boolean ok = categoryDAO.insertCategory(c);
                sendJson(ex, ok ? 201 : 400, makeResp(ok ? 201 : 400, ok ? "新增成功" : "新增失敗", ok ? c : null));
                break;
            }
            case "PUT": {
                Category c = fromJson(ex, Category.class);
                boolean ok = categoryDAO.updateCategory(c);
                sendJson(ex, ok ? 200 : 400, makeResp(ok ? 200 : 400, ok ? "修改成功" : "修改失敗", ok ? c : null));
                break;
            }
            case "DELETE": {
                Map<String, Object> data = fromJson(ex, Map.class);
                Number idNum = (Number) data.get("category_id");
                if (idNum != null) {
                    boolean ok = categoryDAO.deleteCategory(idNum.intValue());
                    sendJson(ex, ok ? 200 : 400, makeResp(ok ? 200 : 400, ok ? "刪除成功" : "刪除失敗", null));
                } else {
                    sendJson(ex, 400, makeResp(400, "缺少 category_id", null));
                }
                break;
            }
            default:
                sendJson(ex, 405, makeResp(405, "方法不支援", null));
        }
    }

    private void handleProducts(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                List<product> list = productDAO.getAllProducts();
                sendJson(ex, 200, makeResp(200, "查詢成功", list));
                break;
            }
            case "POST": {
                product p = fromJson(ex, product.class);
                try {
                    boolean ok = productDAO.insertProduct(p);
                    sendJson(ex, ok ? 201 : 400, makeResp(ok ? 201 : 400, ok ? "新增成功" : "新增失敗", ok ? p : null));
                } catch (SQLIntegrityConstraintViolationException fk) {
                    fk.printStackTrace();
                    sendJson(ex, 400, makeResp(400, "category_id 錯誤", null));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, makeResp(500, "新增失敗", null));
                }
                break;
            }
            case "PUT": {
                product p = fromJson(ex, product.class);
                try {
                    boolean ok = productDAO.updateProduct(p);
                    sendJson(ex, ok ? 200 : 400, makeResp(ok ? 200 : 400, ok ? "修改成功" : "修改失敗", ok ? p : null));
                } catch (SQLIntegrityConstraintViolationException fk) {
                    fk.printStackTrace();
                    sendJson(ex, 400, makeResp(400, "資料錯誤或 FK 錯誤", null));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, makeResp(500, "修改失敗", null));
                }
                break;
            }
            case "DELETE": {
                Map<String, Object> map = fromJson(ex, Map.class);
                Number idNum = (Number) map.get("product_id");
                if (idNum == null) {
                    sendJson(ex, 400, makeResp(400, "缺少 product_id", null));
                    break;
                }
                int id = idNum.intValue();
                try {
                    boolean ok = productDAO.deleteProduct(id);
                    sendJson(ex, ok ? 200 : 400, makeResp(ok ? 200 : 400, ok ? "刪除成功" : "刪除失敗", null));
                } catch (SQLIntegrityConstraintViolationException fk) {
                    sendJson(ex, 400, makeResp(400, "刪除失敗：產品已被引用", null));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, makeResp(500, "刪除失敗", null));
                }
                break;
            }
            default:
                sendJson(ex, 405, makeResp(405, "方法不支援", null));
        }
    }

    private void handleOrders(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        switch (method) {
            case "GET": {
                String query = ex.getRequestURI().getQuery();
                if (query != null && query.startsWith("member_id=")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    List<order> m = oDao.getOrdersByMemberId(id);
                    sendJson(ex, 200, makeResp(200, "查詢成功", m));
                } else {
                    List<order> list = oDao.getAllOrders();
                    sendJson(ex, 200, makeResp(200, "查詢成功", list));
                }
                break;
            }
            case "PUT": {
                // 更新訂單
                order orderToUpdate = fromJson(ex, order.class);
                if (orderToUpdate.getOrder_id() == 0) {
                    sendJson(ex, 400, makeResp(400, "order_id 為必要欄位", null));
                    break;
                }
                try {
                    oDao.updateOrder(orderToUpdate);
                    sendJson(ex, 200, makeResp(200, "更新成功", orderToUpdate));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, makeResp(500, "更新訂單時發生錯誤: " + e.getMessage(), null));
                }
                break;
            }
            case "DELETE": {
                // 刪除訂單
                Map<String, Object> req = fromJson(ex, Map.class);
                Number orderIdNum = (Number) req.get("order_id");
                if (orderIdNum == null) {
                    sendJson(ex, 400, makeResp(400, "缺少 order_id", null));
                    break;
                }
                try {
                    oDao.deleteOrder(orderIdNum.intValue());
                    sendJson(ex, 200, makeResp(200, "刪除成功", null));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, makeResp(500, "刪除訂單時發生錯誤: " + e.getMessage(), null));
                }
                break;
            }
            default:
                sendJson(ex, 405, makeResp(405, "方法不支援", null));
        }
    }

    private void handleUsers(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        String path = ex.getRequestURI().getPath();
        switch (method) {
            case "POST": {


                if (path.endsWith("/login") ) {
                    // 登入
                    user loginReq = fromJson(ex, user.class); // 只呼叫一次
                    Optional<user> result = userDAO.findByAccountAndPassword(loginReq.getAccount(), loginReq.getPassword());
                    if (result.isPresent()) {
                        sendJson(ex, 200, makeResp(200, "登入成功", result.get()));
                    } else {
                        sendJson(ex, 401, makeResp(401, "帳號或密碼錯誤", null));
                    }
                    return;
                }

                // 註冊
                user u = fromJson(ex, user.class);
                user inserted = userDAO.insertUser(u);
                boolean ok = inserted != null && inserted.getUserId() > 0;
                sendJson(ex, ok ? 201 : 400, makeResp(ok ? 201 : 400, ok ? "註冊成功" : "註冊失敗", ok ? inserted : null));
                break;
            }
            case "PUT": { // 修改
                user u = fromJson(ex, user.class);
                boolean ok = userDAO.updateUser(u);
                sendJson(ex, ok ? 200 : 400, makeResp(ok ? 200 : 400, ok ? "修改成功" : "修改失敗", ok ? u : null));
                break;
            }
            case "DELETE": {
                Map<String, Object> map = fromJson(ex, Map.class);
                Number idNum = (Number) map.get("user_id");
                if (idNum == null) {
                    sendJson(ex, 400, makeResp(400, "缺少 user_id", null));
                    break;
                }
                boolean ok = userDAO.deleteUser(idNum.intValue());
                sendJson(ex, ok ? 200 : 400, makeResp(ok ? 200 : 400, ok ? "刪除成功" : "刪除失敗", null));
                break;
            }
            default:
                sendJson(ex, 405, makeResp(405, "方法不支援", null));
        }
    }
    private void handleCarts(HttpExchange ex, String method) throws Exception {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        cartDAO cDao = new cartDAO();
        
        switch (method) {
            case "GET": {
                // 查詢購物車
                Map<String, Object> req = fromJson(ex, Map.class);
                Number memberIdNum = (Number) req.get("member_id");
                if (memberIdNum == null) {
                    sendJson(ex, 400, makeResp(400, "缺少 member_id", null));
                    break;
                }
                List<cart> carts = cDao.getCartByMemberId(memberIdNum.intValue());
                sendJson(ex, 200, makeResp(200, "查詢成功", carts));
                break;
            }
            case "PUT": {
                // 修改購物車數量
                cart req = fromJson(ex, cart.class);
                if (req.getMember_id() == 0 || req.getProduct_id() == 0) {
                    sendJson(ex, 400, makeResp(400, "member_id 和 product_id 為必要欄位", null));
                    break;
                }
                try {
                    cartDAO.updateQuantity(req.getMember_id(), req.getProduct_id(), req.getQuantity());
                    sendJson(ex, 200, makeResp(200, "數量更新成功", req));
                } catch (IllegalArgumentException e) {
                    sendJson(ex, 404, makeResp(404, e.getMessage(), null));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJson(ex, 500, makeResp(500, "更新數量時發生錯誤", null));
                }
                break;
            }
            case "DELETE": {
                // 刪除購物車項目
                Map<String, Object> req = fromJson(ex, Map.class);
                Number memberIdNum = (Number) req.get("member_id");
                Number productIdNum = (Number) req.get("product_id");
                
                if (memberIdNum == null || productIdNum == null) {
                    sendJson(ex, 400, makeResp(400, "缺少 member_id 或 product_id", null));
                    break;
                }
                
                boolean success = cDao.removeFromCart(memberIdNum.intValue(), productIdNum.intValue());
                sendJson(ex, success ? 200 : 400, makeResp(success ? 200 : 400, success ? "移除成功" : "移除失敗", null));
                break;
            }
            default:
                sendJson(ex, 405, makeResp(405, "方法不支援", null));
        }
    }

    // ===================== Helper =====================

    private <T> T fromJson(HttpExchange ex, Class<T> clazz) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(
                ex.getRequestBody(), StandardCharsets.UTF_8)) {
            return gson.fromJson(isr, clazz);
        }
    }

    private void sendJson(HttpExchange ex, int status, JsonObject obj) throws IOException {
        byte[] bytes = gson.toJson(obj).getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private JsonObject makeResp(int status, String msg, Object data) {
        JsonObject o = new JsonObject();
        o.addProperty("status", status);
        o.addProperty("message", msg);
        o.add("data", data == null ? JsonNull.INSTANCE : gson.toJsonTree(data));
        return o;
    }
}