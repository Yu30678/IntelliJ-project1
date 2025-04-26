import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public class ordertestflow {
    private static final String BASE_URL = "http://localhost:8080";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void main(String[] args) throws Exception {
        System.out.println("=== orderTestFlow 開始 ===");

        // 情境 1：正常加入購物車
        System.out.println("\n--- 情境1：商品加入購物車 (member_id=50690, product_id=13) ---");
        String cart1 = String.format(
                "{\"member_id\":%d,\"product_id\":%d,\"quantity\":%d,\"create_at\":\"%s\"}",
                50690, 13, 1, LocalDateTime.now().format(FMT)
        );
        assertCode(sendPost("/cart", cart1), 200);

        // 情境 2：quantity 大於庫存，不可加入購物車
        System.out.println("\n--- 情境2：quantity > soh 無法加入購物車 (member_id=50699, product_id=3) ---");
        String cart2 = String.format(
                "{\"member_id\":%d,\"product_id\":%d,\"quantity\":%d,\"create_at\":\"%s\"}",
                50690, 3, 1999, LocalDateTime.now().format(FMT)
        );
        assertCode(sendPost("/cart", cart2), 400);

        // 情境 3：購物車內商品已下架，不可下單
        System.out.println("\n--- 情境3：購物車內商品已下架，不可下單 (member_id=30678, product_id=8) ---");
        // 假設 cart 已存在 member=50690, product=8
        String orderJson = "{\"member_id\":50690}";
        assertCode(sendPost("/order", orderJson), 400);

        System.out.println("\n=== orderTestFlow 結束 ===");
    }

    private static String sendPost(String path, String json) throws Exception {
        return sendRequest("POST", path, json);
    }

    private static String sendRequest(String method, String path, String json) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("UTF-8"));
        }
        int code = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return code + " / " + sb.toString();
    }

    private static void assertCode(String resp, int expect) {
        String[] parts = resp.split(" / ", 2);
        int code = Integer.parseInt(parts[0].trim());
        if (code != expect) {
            throw new RuntimeException(
                    String.format("預期 %d，卻是: %s", expect, resp)
            );
        }
        System.out.println("✓ HTTP " + expect + " as expected: " + parts[1]);
    }
}
