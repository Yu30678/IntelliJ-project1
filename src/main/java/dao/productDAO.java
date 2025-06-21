package dao;

import model.product;
import util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class productDAO {

    public static List<product> getAllProducts() throws Exception {
        List<product> products = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT p.product_id, p.name, p.price, p.soh, p.category_id, c.name AS category_name, p.image_url, p.is_active " +
                             "FROM product p JOIN category c ON p.category_id = c.category_id WHERE p.is_active = 1")) {

            while (rs.next()) {
                product p = new product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setSoh(rs.getInt("soh"));
                p.setCategory_id(rs.getInt("category_id"));
                p.setCategory_name(rs.getString("category_name"));
                p.setImage_url(rs.getString("image_url"));
                p.setIs_active(rs.getBoolean("is_active"));
                products.add(p);
            }
        }

        return products;
    }

    public static boolean insertProduct(product p) throws Exception {
        String sql = "INSERT INTO product (name, price, soh, category_id, is_active, image_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getSoh());
            ps.setInt(4, p.getCategory_id());
            ps.setBoolean(5, true);
            ps.setString(6, p.getImage_url());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setProduct_id(rs.getInt(1));
                    }
                }
                p.setIs_active(true);
                return true;
            }
        }
        return false;
    }

    public static boolean updateProduct(product p) throws Exception {
        String sql = "UPDATE product SET name = ?, price = ?, soh = ?, category_id = ?, is_active = ? , image_url = ? WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getSoh());
            ps.setInt(4, p.getCategory_id());
            ps.setBoolean(5, p.isIs_active());
            ps.setString(6, p.getImage_url());
            ps.setInt(7, p.getProduct_id());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public static boolean deleteProduct(int product_id) throws Exception {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product_id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }
    public static void deactivateOutOfStockProducts() throws Exception {
        String sql = "UPDATE product SET is_active = 0 WHERE soh = 0 AND is_active = 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
    public static product getProductById(int product_id) throws Exception {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, product_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("找不到商品");
                product p = new product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setSoh(rs.getInt("soh"));
                p.setCategory_id(rs.getInt("category_id"));
                p.setIs_active(rs.getBoolean("is_active"));
                return p;
            }
        }
    }

    public static void updateSoh(int product_id, int newSoh) throws Exception {
        String sql = "UPDATE product SET soh = ? WHERE product_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, newSoh);
            ps.setInt(2, product_id);
            ps.executeUpdate();
        }
    }
}
