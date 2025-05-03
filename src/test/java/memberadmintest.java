import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Member;
import util.LocalDateTimeAdapter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class memberadmintest {
    private static final String BASE_URL = "http://localhost:8080/user/members";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public static void main(String[] args) throws Exception {
        /*
        // 1. 新增會員（帶有獨特名稱以便識別）
        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 100000);
        String baseName = "Test56";
        Member m = new Member();
        m.setName(baseName + uniqueSuffix);
        m.setPassword("pa57");
        m.setPhone("0157" + uniqueSuffix);
        m.setAddress("Test Ad9ess");
        m.setCreate_at(LocalDateTime.now());
        m.setEmail("te66sqr" + uniqueSuffix + "@example.com");
        System.out.println("CREATE member: " + sendPost(BASE_URL, gson.toJson(m)));

         */

        // 2. 查詢所有會員，並找出剛剛新增的那筆
        String listResp = sendGet(BASE_URL);
        System.out.println("LIST members: " + listResp);
        Member[] members = gson.fromJson(listResp, Member[].class);
        /*
        Optional<Member> opt = Arrays.stream(members)
                .filter(u -> (baseName + uniqueSuffix).equals(u.getName()))
                .findFirst();
        if (opt.isEmpty()) {
            System.err.println("❌ 剛剛新增的會員沒找到，無法繼續測試");
            return;
        }
        Member created = opt.get();

         */
        /*
        // 3. 更新該會員
        created.setName(created.getName() + "_upd");
        System.out.println("UPDATE member: " + sendPut(BASE_URL, gson.toJson(created)));

        // 4. 刪除該會員（此會員沒有任何關聯資料）
        String delJson = "{\"member_id\":" + created.getMember_id() + "}";
        System.out.println("DELETE member: " + sendDelete(BASE_URL, delJson));

         */
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
