package dao;
import model.Category;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class categoryDAO {
    public static List<Category> getAllCategories() throws Exception {
        String sql = "SELECT category_id, name FROM category";
        List<Category> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setName(rs.getString("name"));
                list.add(c);
            }
        }
        return list;
    }
    public static Category getCategoryById(int id) throws Exception {
        String sql = "SELECT category_id, name FROM category WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = new Category();
                    c.setCategory_id(rs.getInt("category_id"));
                    c.setName(rs.getString("name"));
                    return c;
                }
            }
        }
        return null;
    }
    public static void insertCategory(Category c) throws Exception {
        String sql = "INSERT INTO category (name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.executeUpdate();
        }
    }
    public static void updateCategory(Category c) throws Exception {
        String sql = "UPDATE category SET name = ? WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setInt(2, c.getCategory_id());
            ps.executeUpdate();
        }
    }
    public static void deleteCategory(int id) throws Exception {
        String sql = "DELETE FROM category WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

