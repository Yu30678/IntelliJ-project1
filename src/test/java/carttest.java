

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.cart;
import util.LocalDateTimeAdapter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;


public class carttest {

    //private static final Gson gson = new Gson();
    private static final String BASE_URL = "http://localhost:8080/cart";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws Exception {
        int member_id = 30678;
        int product_id = 28;
        int invalidProductId = 9999;
        /*
        // 1. 測試加入購物車（正常）
        System.out.println("➤ 測試加入購物車（正常）");
        cart c1 = new cart();
        c1.setMember_id(member_id);
        c1.setProduct_id(product_id);
        c1.setQuantity(2);
        c1.setCreate_at(LocalDateTime.now());
        String response1 = sendPost(BASE_URL, gson.toJson(c1));
        System.out.println(response1);

        String json = gson.toJson(LocalDateTime.now());
        System.out.println("LocalDateTime to JSON: " + json);

        // 2. 測試加入不存在商品
        System.out.println("➤ 測試加入購物車（商品不存在）");
        cart c2 = new cart();
        c2.setMember_id(member_id);
        c2.setProduct_id(invalidProductId);
        c2.setQuantity(1);
        c2.setCreate_at(LocalDateTime.now());
        String response2 = sendPost(BASE_URL, gson.toJson(c2));
        System.out.println(response2);

        // 3. 查詢購物車
        System.out.println("➤ 測試查詢購物車");
        String getResponse = sendGet(BASE_URL + "?member_id=" + member_id);
        cart[] carts = gson.fromJson(getResponse, cart[].class);
        for (cart c : carts) {
            System.out.printf("商品 ID: %d, 數量: %d, 加入時間: %s%n",
                    c.getProduct_id(), c.getQuantity(), c.getCreate_at());
        }

         */

        // 4. 刪除購物車商品
        System.out.println("➤ 測試刪除購物車商品");
        String deleteResponse = sendDelete(BASE_URL + "?member_id=" + member_id + "&product_id=" + product_id);
        System.out.println(deleteResponse);


    }

    // 原生 POST
    private static String sendPost(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        return readResponse(conn);
    }

    // 原生 GET
    private static String sendGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    // 原生 DELETE
    private static String sendDelete(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("DELETE");
        return readResponse(conn);
    }

    // 統一讀 response
    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream stream = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();

        if (stream == null) {
            return "⚠️ No response stream from server. HTTP Status: " + conn.getResponseCode();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            return result.toString();
        }
    }
}
