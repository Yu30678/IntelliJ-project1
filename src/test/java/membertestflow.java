import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Member;
import model.cart;
import model.order;
import model.product;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import java.util.stream.Stream;

/**
 * 使用者端完整流程測試：
 * 1. POST /member          (公開註冊)
 * 2. GET  /product         (瀏覽商品)
 * 3. GET  /user/members    (管理者列出所有會員，取得 member_id)
 * 4. POST /cart            (加入購物車)
 * 5. GET  /cart?member_id= (檢視購物車)
 * 6. DELETE /cart?...      (URL Query 刪除購物車項目)
 * 7. POST /order           (下訂單)
 * 8. GET  /order?member_id= (查該會員訂單)
 * 9. DELETE /order?order_id= (刪訂單)
 */

public class membertestflow {
    private static final String BASE_URL        = "http://localhost:8080";
    // 把 /member 換成 現在伺服器上登記的 admin 建會員 endpoint
    private static final String MEMBER_URL      = BASE_URL + "/user/members";
    private static final String PRODUCT_URL     = BASE_URL + "/product";
    private static final String CART_URL        = BASE_URL + "/cart";
    private static final String ORDER_URL       = BASE_URL + "/order";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws Exception {
        // 1) 在 /user/members 建一個新會員
        String suffix = String.valueOf(System.currentTimeMillis() % 100000);
        Member newM = new Member();
        newM.setName("user" + suffix);
        newM.setPassword("pass" + suffix);
        newM.setPhone("0912" + suffix);
        newM.setAddress("Address" + suffix);
        newM.setEmail("user" + suffix + "@example.com");
        newM.setCreate_at(LocalDateTime.now());

        String regJson = gson.toJson(newM);
        String regResp = sendPost(MEMBER_URL, regJson);
        System.out.println("➤ POST " + MEMBER_URL + " → " + regResp);
        // userController 回傳 {"status":"created"}
        if (!regResp.contains("\"created\"")) {
            throw new RuntimeException("註冊失敗: " + regResp);
        }

        // 2) 重新拉一次 /user/members 列表，找出剛才那筆的 member_id
        String allMems = sendGet(MEMBER_URL);
        System.out.println("➤ GET  " + MEMBER_URL + " → " + allMems);
        Member[] memArr = gson.fromJson(allMems, Member[].class);
        Optional<Member> opt = Arrays.stream(memArr)
                .filter(m -> m.getName().equals(newM.getName())
                        && m.getEmail().equals(newM.getEmail()))
                .findFirst();
        if (opt.isEmpty()) {
            throw new RuntimeException("找不到剛註冊的會員");
        }
        int memberId = opt.get().getMember_id();
        System.out.println("取得 member_id=" + memberId);

        // 3) 瀏覽商品
        String prodList = sendGet(PRODUCT_URL);
        System.out.println("➤ GET  " + PRODUCT_URL + " → " + prodList);
        JsonArray pa = JsonParser.parseString(prodList).getAsJsonArray();
        if (pa.size() == 0) throw new RuntimeException("沒商品");
        product first = gson.fromJson(pa.get(0), product.class);

        // 4) 加入購物車（數量 1）
        cart c = new cart();
        c.setMember_id(memberId);
        c.setProduct_id(first.getProduct_id());
        c.setQuantity(1);
        c.setCreate_at(LocalDateTime.now());
        String addC = sendPost(CART_URL, gson.toJson(c));
        System.out.println("➤ POST " + CART_URL + " → " + addC);

        // 5) 檢視購物車
        String viewC = sendGet(CART_URL + "?member_id=" + memberId);
        System.out.println("➤ GET  " + CART_URL + "?member_id=" + memberId + " → " + viewC);

        // 6) “更新” 這裡先刪除再新增數量 5
        String delC = sendDelete(CART_URL
                + "?member_id=" + memberId
                + "&product_id=" + first.getProduct_id());
        System.out.println("➤ DELETE " + CART_URL + "... → " + delC);

        c.setQuantity(5);
        String updC = sendPost(CART_URL, gson.toJson(c));
        System.out.println("➤ POST " + CART_URL + " (更新) → " + updC);

        // 7) 下訂單
        JsonObject orderReq = new JsonObject();
        orderReq.addProperty("member_id", memberId);
        String ordResp = sendPost(ORDER_URL, orderReq.toString());
        System.out.println("➤ POST " + ORDER_URL + " → " + ordResp);
        int orderId = JsonParser.parseString(ordResp)
                .getAsJsonObject().get("order_id").getAsInt();

        // 8) 下訂單後，再把這筆從購物車清除
        String postDel = sendDelete(CART_URL
                + "?member_id=" + memberId
                + "&product_id=" + first.getProduct_id());
        System.out.println("➤ DELETE " + CART_URL + " (訂單後) → " + postDel);

        System.out.println("✅ 完整客戶流程跑完！");


    }

    private static String sendGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        int code = conn.getResponseCode();
        String body = readResponse(conn);
        System.out.printf("  GET  %s → %d%n", urlStr, code);
        return body;
    }
    private static String sendPost(String urlStr, String json) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        String body = readResponse(conn);
        System.out.printf("  POST %s → %d%n", urlStr, code);
        return body;
    }
    private static String sendDelete(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("DELETE");
        int code = conn.getResponseCode();
        String body = readResponse(conn);
        System.out.printf("  DELETE %s → %d%n", urlStr, code);
        return body;
    }
    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream in = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();
        if (in == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}