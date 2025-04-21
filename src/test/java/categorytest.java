import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Category;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
public class categorytest {
    private static final String BASE_URL = "http://localhost:8080/user/categories";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws Exception {
        // 1. 新增類別
        Category c = new Category();
        c.setName("TestCaaategory");
        System.out.println("CREATE category: " + sendPost(BASE_URL, gson.toJson(c)));

        // 2. 查詢所有類別
        String listResp = sendGet(BASE_URL);
        System.out.println("LIST categories: " + listResp);

        // 3. 更新類別（取第一筆）
        Category[] cats = gson.fromJson(listResp, Category[].class);
        if (cats.length > 0) {
            Category uc = cats[0];
            uc.setName(uc.getName() + "_upd");
            System.out.println("UPDATE category: " + sendPut(BASE_URL, gson.toJson(uc)));

            // 4. 刪除類別
            String delJson = "{\"category_id\":" + uc.getCategory_id() + "}";
            System.out.println("DELETE category: " + sendDelete(BASE_URL, delJson));
        }
    }

    private static String sendGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
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

    private static String sendRequest(String method, String urlStr, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Content-Length", String.valueOf(body.length));
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body);
        }
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream stream = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (stream == null) return "No response. Status=" + conn.getResponseCode();
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
