import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.order;
import model.order_detail;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class ordertest {
    private static final String BASE_URL = "http://localhost:8080/order";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

public static void main(String[] args) throws IOException, InterruptedException {
    int memberId = 30679;  // 請替換為測試用會員 ID
    int orderId = 25;
    // 1. 建立訂單
    System.out.println("\n➤ 測試下訂單 (POST /order)");
    String orderResponse = sendPost(BASE_URL , String.valueOf(memberId));
    System.out.println("回應: " + orderResponse);

    // 需稍微等待，以保證資料庫完成寫入
    Thread.sleep(100);

    // 2. 查詢該會員所有訂單
    System.out.println("\n➤ 測試查詢訂單 (GET /order?member_id=)");
    String listResponse = sendGet(BASE_URL + "?member_id=" + memberId);
    System.out.println("Raw JSON: " + listResponse);
    order[] orders = gson.fromJson(listResponse, order[].class);
    for (order o : orders) {
        System.out.printf("Order ID: %d, Member ID: %d, Created: %s%n",
                o.getOrder_id(), o.getMember_id(), o.getCreate_at());
    }



        // 3. 查詢訂單明細
        System.out.println("\n➤ 測試查詢訂單明細 (GET /order_detail?order_id=)");
        String detailResponse = sendGet("http://localhost:8080/order_detail?order_id=" + orderId);
        System.out.println("Raw JSON: " + detailResponse);
        order_detail[] details = gson.fromJson(detailResponse, order_detail[].class);
        for (order_detail od : details) {
            System.out.printf("Product ID: %d, Quantity: %d, Price: %s%n",
                    od.getProduct_id(), od.getQuantity(), od.getPrice());
        }

        //4.查詢所有訂單
        System.out.println("\n>測試查詢所有訂單");
        String allorders = sendGet(BASE_URL + "/orders");
        System.out.println("Raw JSON: " + allorders);
        order[] allorder = gson.fromJson(listResponse, order[].class);
        for (order p : allorder) {
        System.out.printf("Order ID: %d, Member ID: %d, Created: %s%n",
                p.getOrder_id(), p.getMember_id(), p.getCreate_at());
    }
}

private static String sendPost(String urlStr, String body) throws IOException {
    URL url = new URL(urlStr);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
    try (OutputStream os = conn.getOutputStream()) {
        os.write(body.getBytes());
    }
    return readResponse(conn);
}

private static String sendGet(String urlStr) throws IOException {
    URL url = new URL(urlStr);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    return readResponse(conn);
}

private static String readResponse(HttpURLConnection conn) throws IOException {
    InputStream stream = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
    if (stream == null) return "⚠️ No response stream. HTTP Status: " + conn.getResponseCode();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return sb.toString();
    }
}
}
