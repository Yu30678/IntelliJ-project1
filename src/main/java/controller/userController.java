package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.*;
import model.*;
import util.LocalDateTimeAdapter;

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
    @Override
    public void handle(HttpExchange ex) {
        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();
            if (path.startsWith("/user/products")) {
                handleProducts(ex, method);
            } else if (path.startsWith("/user/categories")) {
                handleCategories(ex, method);
            } else if (path.startsWith("/user/members")) {
                handleMembers(ex, method);
            } else if (path.startsWith("/user/orders")) {
                handleOrders(ex, method);
            } else {
                ex.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try { ex.sendResponseHeaders(500, -1); } catch (Exception ignored) {}
        }
    }

    // === Products ===
    private void handleProducts(HttpExchange ex, String method) throws Exception {
        productDAO dao = new productDAO();
        switch (method) {
            case "GET": // list
                List<product> ps = productDAO.getAllProducts();
                writeJson(ex, ps);
                break;
            case "POST": // create
                product np = gson.fromJson(new InputStreamReader(ex.getRequestBody()), product.class);
                productDAO.insertProduct(np);
                writeJson(ex, "created");
                break;
            case "PUT": // update
                product up = gson.fromJson(new InputStreamReader(ex.getRequestBody()), product.class);
                productDAO.updateProduct(up);
                writeJson(ex, "updated");
                break;
            case "DELETE": // delete
                Map<String,Integer> delp = gson.fromJson(new InputStreamReader(ex.getRequestBody()), Map.class);
                productDAO.deleteProduct(delp.get("product_id"));
                writeJson(ex, "deleted");
                break;
            default:
                ex.sendResponseHeaders(405, -1);
        }
    }

    // === Categories ===
    private void handleCategories(HttpExchange ex, String method) throws Exception {
        switch (method) {
            case "GET": writeJson(ex, categoryDAO.getAllCategories()); break;
            case "POST": {
                Category c = gson.fromJson(new InputStreamReader(ex.getRequestBody()), Category.class);
                categoryDAO.insertCategory(c);
                writeJson(ex,"created"); break;
            }
            case "PUT": {
                Category c = gson.fromJson(new InputStreamReader(ex.getRequestBody()), Category.class);
                categoryDAO.updateCategory(c);
                writeJson(ex,"updated"); break;
            }
            case "DELETE": {
                Map<String,Integer> d = gson.fromJson(new InputStreamReader(ex.getRequestBody()), Map.class);
                categoryDAO.deleteCategory(d.get("category_id"));
                writeJson(ex,"deleted"); break;
            }
            default: ex.sendResponseHeaders(405,-1);
        }
    }

    // === Members ===
    private void handleMembers(HttpExchange ex, String method) throws Exception {
        memberDAO dao = new memberDAO();
        switch (method) {
            case "GET": writeJson(ex, dao.getAllMembers()); break;
            case "POST": {
                Member m = gson.fromJson(new InputStreamReader(ex.getRequestBody()), Member.class);
                dao.insertMember(m);
                writeJson(ex,"created"); break;
            }
            case "PUT": {
                Member m = gson.fromJson(new InputStreamReader(ex.getRequestBody()), Member.class);
                dao.updateMember(m);
                writeJson(ex,"updated"); break;
            }
            case "DELETE": {
                Map<String,Object> d = gson.fromJson(new InputStreamReader(ex.getRequestBody()), Map.class);
                int memberId = ((Number)d.get("member_id")).intValue();
                dao.deleteMember(memberId);
            }
            default: ex.sendResponseHeaders(405,-1);
        }
    }

    // === Orders ===
    private void handleOrders(HttpExchange ex, String method) throws Exception {
        switch (method) {
            case "GET": // list all
                writeJson(ex, orderDAO.getAllOrders());
                break;
            default:
                ex.sendResponseHeaders(405, -1);
        }
    }

    private void writeJson(HttpExchange ex, Object obj) throws Exception {
        String json = gson.toJson(obj);
        ex.getResponseHeaders().set("Content-Type","application/json");
        ex.sendResponseHeaders(200, json.getBytes().length);
        try (OutputStream os = ex.getResponseBody()) { os.write(json.getBytes()); }
    }
}
