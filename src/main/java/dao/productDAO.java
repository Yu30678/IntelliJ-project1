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
                     "SELECT p.product_id, p.name, p.price, p.soh, p.category_id, c.name AS category_name " +
                             "FROM product p JOIN category c ON p.category_id = c.category_id")) {

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
        }

        return products;
    }

    public static void insertProduct(product p) throws Exception {
        String sql = "INSERT INTO product (name, price, soh, category_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getSoh());
            ps.setInt(4, p.getCategory_id());
            ps.executeUpdate();
        }
    }

    public static void updateProduct(product p) throws Exception {
        String sql = "UPDATE product SET name = ?, price = ?, soh = ?, category_id = ? WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setBigDecimal(2, p.getPrice());
            ps.setInt(3, p.getSoh());
            ps.setInt(4, p.getCategory_id());
            ps.setInt(5, p.getProduct_id());
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
}
