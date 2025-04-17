package controller;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.memberDAO;
import model.Member;
import util.GsonUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class memberController implements HttpHandler {

    private final Gson gson = GsonUtil.getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("/member".equals(path) && "POST".equalsIgnoreCase(method)) {
            handleRegister(exchange);
        } else if ("/member/login".equals(path) && "POST".equalsIgnoreCase(method)) {
            handleLogin(exchange);
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Member newMember = gson.fromJson(reader, Member.class);

            // 加入註冊時間
            //newMember.setCreate_at(LocalDateTime.now());

            System.out.println("收到註冊資料:");
            System.out.println("  姓名: " + newMember.getName());
            System.out.println("  密碼: " + newMember.getPassword());
            System.out.println("  電話: " + newMember.getPhone());
            System.out.println("  地址: " + newMember.getAddress());
            System.out.println("  信箱: " + newMember.getEmail());
            System.out.println("  建立時間: " + newMember.getCreate_at());

            memberDAO dao = new memberDAO();
            dao.insertMember(newMember);

            String response = "註冊成功！";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } catch (Exception e) {
            System.out.println("=== 註冊會員時發生錯誤 ===");
            e.printStackTrace();
            String response = "註冊失敗：" + e.getMessage();
            exchange.sendResponseHeaders(500, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } finally {
            exchange.close();
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Member loginRequest = gson.fromJson(reader, Member.class);

            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            memberDAO dao = new memberDAO();
            Member loginMember = dao.findByEmail(email,password);

            String response;
            if (loginMember != null && loginMember.getPassword().equals(password)) {
                response = "登入成功，歡迎 " + loginMember.getName();
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
                response = "登入失敗，帳號或密碼錯誤";
                exchange.sendResponseHeaders(401, response.getBytes().length);
            }
            exchange.getResponseBody().write(response.getBytes());
        } catch (Exception e) {
            System.out.println("=== 登入時發生錯誤 ===");
            e.printStackTrace();
            String response = "登入失敗：" + e.getMessage();
            exchange.sendResponseHeaders(500, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } finally {
            exchange.close();
        }
    }
}
