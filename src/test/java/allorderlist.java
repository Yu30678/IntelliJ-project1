import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.order;
import model.order_detail;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
public class allorderlist {
    private static final String BASE_URL = "http://localhost:8080/order";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public static void main(String[] args) throws IOException, InterruptedException {
        //4.查詢所有訂單
        System.out.println("\n>測試查詢所有訂單");
        String allorders = sendGet("http://localhost:8080/order");
        System.out.println("Raw JSON: " + allorders);
        order[] allorder = gson.fromJson(allorders, order[].class);
        for (order p : allorder) {
            System.out.printf("Order ID: %d, Member ID: %d, Created: %s%n",
                    p.getOrder_id(), p.getMember_id(), p.getCreate_at());
        }
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
