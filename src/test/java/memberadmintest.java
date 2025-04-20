import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Member;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class memberadmintest {
    private static final String BASE_URL = "http://localhost:8080/user/members";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws Exception {
        // 1. 新增會員
        Member m = new Member();
        m.setName("Testpser");
        m.setPassword("pasd123");
        m.setPhone("091887452");
        m.setAddress("Test Adtrress");
        m.setCreate_at(LocalDateTime.now());
        m.setEmail("testuser@ex897ple.com");
        System.out.println("CREATE member: " + sendPost(BASE_URL, gson.toJson(m)));

        // 2. 查詢所有會員
        String listResp = sendGet(BASE_URL);
        System.out.println("LIST members: " + listResp);

        // 3. 更新會員（取第一筆）
        Member[] members = gson.fromJson(listResp, Member[].class);
        if (members.length > 0) {
            Member u = members[0];
            u.setName(u.getName() + "_upd");
            System.out.println("UPDATE member: " + sendPut(BASE_URL, gson.toJson(u)));

            // 4. 刪除會員
            String delJson = "{\"member_id\":" + u.getMember_id() + "}";
            System.out.println("DELETE member: " + sendDelete(BASE_URL, delJson));
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
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) { os.write(json.getBytes()); }
        return readResponse(conn);
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        InputStream stream = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();
        if (stream == null) return "No response. Status=" + conn.getResponseCode();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder sb = new StringBuilder();
            String line;                       // ← 這裡單獨宣告 String line
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();             // ← 最後回傳 sb.toString()
        }
    }
}