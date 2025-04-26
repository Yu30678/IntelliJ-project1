import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import model.order;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
public class orderadmintest {
    private static final String BASE_URL = "http://localhost:8080/order";
    private static final String Dt = "http://localhost:8080/order_detail";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws Exception {
        int memberId = 30678;
        //int orderId = 1;
        // 1. 新增訂單
        System.out.println("➤ CREATE order");
        String createJson = "{\"member_id\":" + memberId + "}";
        String createResp = sendPost(BASE_URL, createJson);
        System.out.println("Response: " + createResp);
        JsonObject obj = gson.fromJson(createResp, JsonObject.class);
        int orderId = obj.get("order_id").getAsInt();

        // 2. 依會員查詢
        System.out.println("➤ QUERY orders by member_id");
        String listResp = sendGet(BASE_URL + "?member_id=" + memberId);
        System.out.println("Response: " + listResp);
        order[] orders = gson.fromJson(listResp, order[].class);

        // 3. 查詢所有訂單
        System.out.println("➤ QUERY all orders");
        String allResp = sendGet(BASE_URL);
        System.out.println("Response: " + allResp);
        order[] allOrders = gson.fromJson(allResp, order[].class);
        /*
        //allorderdetail
        System.out.println("➤ QUERY all orderdetail");
        String alldetailResp = sendGet(Dt + "orderId=" + orderId);
        System.out.println("Response: " + alldetailResp);
        order[] allOrderdetail = gson.fromJson(alldetailResp, order[].class);


         */
        // 4. 更新訂單（更新 create_at）
        System.out.println("➤ UPDATE order");
        order toUpdate = null;
        for (order o : orders) {
            if (o.getOrder_id() == orderId) {
                toUpdate = o;
                break;
            }
        }
        if (toUpdate != null) {
            toUpdate.setCreate_at(LocalDateTime.now());
            String updJson = gson.toJson(toUpdate);
            String updResp = sendPut(BASE_URL, updJson);
            System.out.println("Response: " + updResp);
        } else {
            System.out.println("Created order not found in member query");
        }

        // 5. 刪除訂單
        System.out.println("➤ DELETE order");
        String delJson = "{\"order_id\":" + orderId + "}";
        String delResp = sendDelete(BASE_URL, delJson);
        System.out.println("Response: " + delResp);


    }

    // ========== HTTP helpers ==========

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
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream stream = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();
        if (stream == null) {
            return "⚠️ No response stream. HTTP Status: " + conn.getResponseCode();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
