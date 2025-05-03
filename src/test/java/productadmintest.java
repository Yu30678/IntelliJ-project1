import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Category;
import model.product;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class productadmintest {
    private static final String BASE_CAT_URL = "http://localhost:8080/user/categories";
    private static final String BASE_PROD_URL = "http://localhost:8080/user/products";
    private static final Gson gson = new GsonBuilder().create();

    public static void main(String[] args) throws Exception {
        /*
        // 1. 建立一個新的分類，取得 category_id
        // 1. 建立一個唯一分類
        String uniqueCat = "TestCat_" + (System.currentTimeMillis() % 100000);
        String newCatJson = "{\"name\":\"" + uniqueCat + "\"}";
        System.out.println("CREATE category: " + sendPost(BASE_CAT_URL, newCatJson));

        String catsList = sendGet(BASE_CAT_URL);
        System.out.println("LIST categories: " + catsList);
        Category[] cats = gson.fromJson(catsList, Category[].class);
        Category cat = cats[cats.length - 1];
        int catId = cat.getCategory_id();

        // 2. 使用該 category_id 建立商品
        product p = new product();
        p.setName("30678_" + System.currentTimeMillis() % 1000);
        p.setPrice(new BigDecimal("99.99"));
        p.setSoh(100);
        p.setCategory_id(catId);
        p.setIs_active(true);
        System.out.println("CREATE product: " + sendPost(BASE_PROD_URL, gson.toJson(p)));

         */

        // 3. 列出所有商品，並取最後一筆
        String prodsList = sendGet(BASE_PROD_URL);
        System.out.println("LIST products: " + prodsList);
        product[] prods = gson.fromJson(prodsList, product[].class);
        product last = prods[prods.length - 1];
        //int prodId = last.getProduct_id();
        /*
        // 4. 更新該商品
        last.setName(last.getName() + "_upd");
        last.setPrice(new BigDecimal("199.99"));
        System.out.println("UPDATE product: " + sendPut(BASE_PROD_URL, gson.toJson(last)));

        // 5. 刪除該商品
        String delJson = "{\"product_id\":" + prodId + "}";
        System.out.println("DELETE product: " + sendDelete(BASE_PROD_URL, delJson));

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
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}
