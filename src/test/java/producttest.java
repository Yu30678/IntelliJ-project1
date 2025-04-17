import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.product;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class producttest {
    private static final String BASE_URL = "http://localhost:8080/product";
    private static final Gson gson = new GsonBuilder().create();

    public static void main(String[] args) throws IOException {
        // 1. 新增商品
        System.out.println("\n➤ 新增商品");
        product p1 = new product();
        p1.setName("藍牙滑鼠");
        p1.setPrice(new BigDecimal("599.99"));
        p1.setSoh(30);
        p1.setCategory_id(1);
        System.out.println(sendPost(BASE_URL, gson.toJson(p1)));

        // 2. 查詢所有商品
        System.out.println("\n➤ 查詢所有商品");
        System.out.println(sendGet(BASE_URL));

        // 3. 修改商品（假設修改 product_id = 1）
        System.out.println("\n➤ 修改商品");
        product p2 = new product();
        p2.setProduct_id(1);
        p2.setName("進階藍牙滑鼠");
        p2.setPrice(new BigDecimal("799.00"));
        p2.setSoh(20);
        p2.setCategory_id(1);
        System.out.println(sendPut(BASE_URL, gson.toJson(p2)));

        // 4. 刪除商品（假設刪除 product_id = 1）
        System.out.println("\n➤ 刪除商品");
        product p3 = new product();
        p3.setProduct_id(1);
        System.out.println(sendDelete(BASE_URL, gson.toJson(p3)));
    }

    private static String sendPost(String urlStr, String json) throws IOException {
        return sendRequest("POST", urlStr, json);
    }

    private static String sendPut(String urlStr, String json) throws IOException {
        return sendRequest("PUT", urlStr, json);
    }

    private static String sendDelete(String urlStr, String json) throws IOException {
        return sendRequest("DELETE", urlStr, json);
    }

    private static String sendGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    private static String sendRequest(String method, String urlStr, String json) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream stream = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (stream == null) return "⚠️ No response stream. HTTP Status: " + conn.getResponseCode();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
            return result.toString();
        }
    }
}
