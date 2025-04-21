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
                     "SELECT p.product_id, p.name, p.price, p.soh, p.category_id, c.name AS category_name, p.is_active " +
                             "FROM product p JOIN category c ON p.category_id = c.category_id WHERE p.is_active = 1")) {

            while (rs.next()) {
                product p = new product();
                p.setProduct_id(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setSoh(rs.getInt("soh"));
                p.setCategory_id(rs.getInt("category_id"));
                p.setCategory_name(rs.getString("category_name"));
                p.setIs_active(rs.getBoolean("is_active"));
                products.add(p);
            }
        }

        return products;
    }

    public static void insertProduct(product p) throws Exception {
        String sql = "INSERT INTO product (name, price, soh, category_id, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getSoh());
            ps.setInt(4, p.getCategory_id());
            ps.setBoolean(5, true);
            ps.executeUpdate();
        }
    }

    public static void updateProduct(product p) throws Exception {
        String sql = "UPDATE product SET name = ?, price = ?, soh = ?, category_id = ?, is_active = ? WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getSoh());
            ps.setInt(4, p.getCategory_id());
            ps.setBoolean(5, p.isIs_active());
            ps.setInt(6, p.getProduct_id());
            //ps.setInt(7, p.getProduct_id());
            ps.executeUpdate();
        }
    }

    public static void deleteProduct(int product_id) throws Exception {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product_id);
            ps.executeUpdate();
        }
    }
    public static void deactivateOutOfStockProducts() throws Exception {
        String sql = "UPDATE product SET is_active = 0 WHERE soh = 0 AND is_active = 1";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
