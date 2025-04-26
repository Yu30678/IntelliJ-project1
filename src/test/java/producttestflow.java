import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import util.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class producttestflow {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String BASE = "http://localhost:8080";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    public static void main(String[] args) throws Exception {
        System.out.println("=== 測試情境開始 ===");
        int memberId = 30678;

        // 情境一：庫存不足 (product_id=4) → 按照原本流程就行
        System.out.println("\n--- 情境一：庫存不足 (product_id=4) ---");
        assertCode(sendPost("/cart", mkCartJson(memberId, 4, 1)), 400);

        // 情境二：加入購物車後庫存歸零，再下單 (product_id=5)
        System.out.println("\n--- 情境二：庫存歸零後下單 (product_id=5) ---");
        // 1) 加入購物車
        assertCode(sendPost("/cart", mkCartJson(memberId, 5, 1)), 200);
        // 2) 先取得完整 product，再改 soh → 0，整個物件 PUT 回去
        JsonObject p5 = fetchProduct(5);
        p5.addProperty("soh", 0);
        assertCode(sendPut("/product", p5.toString()), 200);
        // 3) 下訂單（應該 400）
        assertCode(sendPost("/order", String.format("{\"member_id\":%d}", memberId)), 400);

        // 情境三：商品下架後下單 (product_id=3)
        System.out.println("\n--- 情境三：商品下架後下單 (product_id=3) ---");
        // 1) 先加入購物車
        assertCode(sendPost("/cart", mkCartJson(memberId, 3, 1)), 200);
        // 2) 取得完整 product，改 is_active → false，再 PUT
        JsonObject p3 = fetchProduct(3);
        p3.addProperty("is_active", false);
        assertCode(sendPut("/product", p3.toString()), 200);
        // 3) 下訂單（應該 400）
        assertCode(sendPost("/order", String.format("{\"member_id\":%d}", memberId)), 400);

        System.out.println("=== 測試結束 ===");
    }

    // 產生 cart JSON
    private static String mkCartJson(int mid, int pid, int qty) {
        return String.format(
                "{\"member_id\":%d,\"product_id\":%d,\"quantity\":%d,\"create_at\":\"%s\"}",
                mid, pid, qty, LocalDateTime.now().format(FMT));
    }

    // 透過 GET /product 拿到完整某筆商品資料
    private static JsonObject fetchProduct(int productId) throws Exception {
        String resp = sendGet("/product");
        String body = resp.split(" / ", 2)[1];
        Type listType = new TypeToken<List<JsonObject>>(){}.getType();
        List<JsonObject> list = gson.fromJson(body, listType);
        return list.stream()
                .filter(o -> o.get("product_id").getAsInt() == productId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("找不到 product_id=" + productId));
    }

    private static String sendGet(String path) throws Exception {
        return sendRequest("GET", path, null);
    }

    private static String sendPost(String path, String json) throws Exception {
        return sendRequest("POST", path, json);
    }

    private static String sendPut(String path, String json) throws Exception {
        return sendRequest("PUT", path, json);
    }

    private static String sendRequest(String method, String path, String json) throws Exception {
        URL url = new URL(BASE + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        if (json != null) {
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("UTF-8"));
            }
        }
        int code = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return code + " / " + sb.toString();
    }

    private static void assertCode(String resp, int expect) {
        int code = Integer.parseInt(resp.split(" / ", 2)[0].trim());
        if (code != expect) {
            throw new RuntimeException(String.format("預期 %d，卻是: %s", expect, resp));
        }
        System.out.println("✓ Code " + expect + " as預期");
    }
}