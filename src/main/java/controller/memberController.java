package controller;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.memberDAO;
import model.Member;
import util.GsonUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

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
        } else if ("/member".equals(path) && "GET".equalsIgnoreCase(method)) {
            handleGetById(exchange);
        }else if ("/member".equals(path) && "DELETE".equalsIgnoreCase(method)) {
            handleDelete(exchange);
        }else if ("/member".equals(path) && "PUT".equalsIgnoreCase(method)) {
            handleChange(exchange);
        } else if ("/member/password".equals(path) && "PUT".equalsIgnoreCase(method)) {
            handlePasswordChange(exchange);
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {


        JsonObject wrapper = new JsonObject();
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Member newMember = gson.fromJson(reader, Member.class);

            memberDAO dao = new memberDAO();
            dao.insertMember(newMember);
            // DAO 裡用 getGeneratedKeys 把 member_id 塞回 newMember

            // 清掉不想回傳的欄位
            newMember.setPassword(null);

            // 只組 data 裡面要的欄位
            JsonObject data = new JsonObject();
            data.addProperty("name",      newMember.getName());
            data.addProperty("password",     newMember.getPassword());
            data.addProperty("phone",   newMember.getPhone());
            data.addProperty("address",     newMember.getAddress());
            data.addProperty("email", newMember.getEmail());
            // （如果你有其他欄位也要加在這裡）

            // 組最外層
            wrapper.addProperty("status",  200);
            wrapper.addProperty("message", "註冊成功！");
            wrapper.add("data", data);

            String response = gson.toJson(wrapper);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            wrapper.addProperty("status",  500);
            wrapper.addProperty("message", "註冊失敗：" + e.getMessage());
            wrapper.add("data", JsonNull.INSTANCE);

            String response = gson.toJson(wrapper);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } finally {
            exchange.close();
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {

        int statusCode = 500;
        String responseJson;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Member loginRequest = gson.fromJson(reader, Member.class);

            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            memberDAO dao = new memberDAO();
            Member loginMember = dao.findByEmail(email,password);


            if (loginMember != null && loginMember.getPassword().equals(password)) {
                statusCode = 200;
                // 建議登入成功不回傳密碼
                loginMember.setPassword(null);
                responseJson = String.format(
                        "{\"status\":%d,\"message\":\"登入成功，歡迎 %s\",\"data\":%s}",
                        statusCode, loginMember.getName(), gson.toJson(loginMember)
                );
            } else {
                statusCode = 401;
                responseJson = String.format(
                        "{\"status\":%d,\"message\":\"登入失敗，帳號或密碼錯誤\"}", statusCode
                );
            }
        } catch (Exception e) {
            statusCode = 500;
            responseJson = String.format(
                    "{\"status\":%d,\"message\":\"登入失敗：%s\"}", statusCode, e.getMessage()
            );
            System.out.println("=== 登入時發生錯誤 ===");
            e.printStackTrace();
        }

        exchange.sendResponseHeaders(statusCode, responseJson.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(responseJson.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }
    private void handleGetById(HttpExchange exchange) throws IOException {
        int statusCode;
        JsonObject wrapper = new JsonObject();

        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.contains("member_id=")) {
                statusCode = 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "member_id 參數為必要");
                wrapper.add("data", JsonNull.INSTANCE);
            } else {
                String[] params = query.split("&");
                String memberIdStr = null;
                for (String param : params) {
                    if (param.startsWith("member_id=")) {
                        memberIdStr = param.substring("member_id=".length());
                        break;
                    }
                }

                if (memberIdStr == null) {
                    statusCode = 400;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "member_id 參數為必要");
                    wrapper.add("data", JsonNull.INSTANCE);
                } else {
                    int id = Integer.parseInt(memberIdStr);
                    memberDAO dao = new memberDAO();
                    Member m = dao.findByid(id);

                    if (m == null) {
                        statusCode = 404;
                        wrapper.addProperty("status", statusCode);
                        wrapper.addProperty("message", "找不到 member_id=" + id);
                        wrapper.add("data", JsonNull.INSTANCE);
                    } else {
                        // 不回傳密碼
                        m.setPassword(null);
                        statusCode = 200;
                        wrapper.addProperty("status", statusCode);
                        wrapper.addProperty("message", "查詢成功");
                        wrapper.add("data", gson.toJsonTree(m));
                    }
                }
            }
        } catch (NumberFormatException e) {
            statusCode = 400;
            wrapper.addProperty("status", statusCode);
            wrapper.addProperty("message", "member_id 必須是數字");
            wrapper.add("data", JsonNull.INSTANCE);
        } catch (Exception e) {
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
        exchange.close();
    }
    private void handleDelete(HttpExchange exchange) throws IOException {
        JsonObject wrapper = new JsonObject();
        int statusCode;

        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject req = gson.fromJson(reader, JsonObject.class);
            if (req == null || !req.has("member_id")) {
                statusCode = 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "member_id 欄位為必要");
                wrapper.add("data", JsonNull.INSTANCE);
            } else {
                int id = req.get("member_id").getAsInt();
                memberDAO dao = new memberDAO();
                boolean deleted = dao.deleteMember(id);
                if (deleted) {
                    statusCode = 200;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "刪除成功");
                    wrapper.add("data", JsonNull.INSTANCE);
                } else {
                    statusCode = 404;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "找不到 member_id=" + id);
                    wrapper.add("data", JsonNull.INSTANCE);
                }
            }
        } catch (Exception e) {
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
        exchange.close();
    }
    private void handleChange(HttpExchange exchange) throws IOException {
        JsonObject wrapper = new JsonObject();
        int statusCode;

        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Member memberToUpdate = gson.fromJson(reader, Member.class);
            
            if (memberToUpdate.getMember_id() <= 0) {
                statusCode = 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "member_id 欄位為必要且必須大於0");
                wrapper.add("data", JsonNull.INSTANCE);
            } else {
                memberDAO dao = new memberDAO();
                boolean updated = dao.updateMember(memberToUpdate);
                
                if (updated) {
                    // 不回傳密碼
                    memberToUpdate.setPassword(null);
                    statusCode = 200;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "修改成功");
                    wrapper.add("data", gson.toJsonTree(memberToUpdate));
                } else {
                    statusCode = 404;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "找不到要修改的會員或修改失敗");
                    wrapper.add("data", JsonNull.INSTANCE);
                }
            }
        } catch (Exception e) {
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
        exchange.close();
    }
    private void handlePasswordChange(HttpExchange exchange) throws IOException {
        JsonObject wrapper = new JsonObject();
        int statusCode;

        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonObject req = gson.fromJson(reader, JsonObject.class);
            
            // 檢查必要的欄位
            if (req == null || !req.has("member_id") || !req.has("old_password") || !req.has("new_password")) {
                statusCode = 400;
                wrapper.addProperty("status", statusCode);
                wrapper.addProperty("message", "member_id, old_password, new_password 欄位都是必要的");
                wrapper.add("data", JsonNull.INSTANCE);
            } else {
                int memberId = req.get("member_id").getAsInt();
                String oldPassword = req.get("old_password").getAsString();
                String newPassword = req.get("new_password").getAsString();
                
                // 檢查新密碼不能為空
                if (newPassword == null || newPassword.trim().isEmpty()) {
                    statusCode = 400;
                    wrapper.addProperty("status", statusCode);
                    wrapper.addProperty("message", "新密碼不能為空");
                    wrapper.add("data", JsonNull.INSTANCE);
                } else {
                    memberDAO dao = new memberDAO();
                    
                    // 先驗證舊密碼是否正確
                    boolean isOldPasswordValid = dao.verifyPassword(memberId, oldPassword);
                    
                    if (!isOldPasswordValid) {
                        statusCode = 401;
                        wrapper.addProperty("status", statusCode);
                        wrapper.addProperty("message", "舊密碼錯誤");
                        wrapper.add("data", JsonNull.INSTANCE);
                    } else {
                        // 舊密碼正確，更新新密碼
                        boolean updated = dao.updatePassword(memberId, newPassword);
                        
                        if (updated) {
                            statusCode = 200;
                            wrapper.addProperty("status", statusCode);
                            wrapper.addProperty("message", "密碼修改成功");
                            wrapper.add("data", JsonNull.INSTANCE);
                        } else {
                            statusCode = 404;
                            wrapper.addProperty("status", statusCode);
                            wrapper.addProperty("message", "找不到要修改的會員或修改失敗");
                            wrapper.add("data", JsonNull.INSTANCE);
                        }
                    }
                }
            }
        } catch (Exception e) {
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
        exchange.close();
    }
    }

