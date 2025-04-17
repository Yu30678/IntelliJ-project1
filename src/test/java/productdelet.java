import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.product;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class productdelet {

    private static final String BASE_URL = "http://localhost:8080/product";

    // ✅ 支援 LocalDateTime 的 Gson
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws IOException {
        System.out.println("\n➤ 刪除商品");

        // ✅ 準備商品資料（只需指定 product_id）
        product p3 = new product();
        p3.setProduct_id(2);

        // ✅ 印出實際傳送的 JSON（debug 用）
        String json = gson.toJson(p3);
        System.out.println("➡️ 傳送 JSON: " + json);

        // ✅ 執行刪除
        String response = sendDelete(BASE_URL, json);
        System.out.println("✅ 回應: " + response);


    }

    private static String sendDelete(String urlStr, String json) throws IOException {
        System.out.println("👉 DELETE 請求到: " + urlStr);
        return sendRequest("DELETE", urlStr, json);
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
        InputStream stream = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();

        if (stream == null) return "⚠️ No response stream. HTTP Status: " + conn.getResponseCode();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
            return result.toString();
        }
    }
}
