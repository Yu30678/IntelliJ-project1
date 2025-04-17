package dao;

import model.product;
import util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class productDAO {
    public static List<product> getAllProducts() throws Exception {
        List<product> products = new ArrayList<>();
        //String sql = "SELECT * FROM product as p left join category c on p.category_id = c.category_id";
        // 建立資料庫連線
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery
                     ("SELECT p.product_id, p.name, p.price, p.soh, p.category_id,\n" +
                             "           c.name AS category_name\n" +
                             "    FROM product p\n" +
                             "    JOIN category c ON p.category_id = c.category_id")) {

            // 將查詢結果塞入 List
            while (rs.next()) {
                product p = new product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setSoh(rs.getInt("soh"));
                p.setCategory_id(rs.getInt("category_id"));
                p.setCategory_name(rs.getString("category_name"));
                products.add(p);
            }

            System.out.println("查詢到商品數量: " + products.size()); // debug
        }

        return products;
    }
}
