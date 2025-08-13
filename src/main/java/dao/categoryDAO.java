package dao;
import model.Category;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class categoryDAO {
    //瀏覽商品類別列表(管理員)
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
    //新增商品類別(管理員)
    public static boolean insertCategory(Category c) throws Exception {
        String sql = "INSERT INTO category (name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            int affected = ps.executeUpdate();

            // 新增成功才取得產生的 id
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setCategory_id(rs.getInt(1));
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }
    //更新商品類別(管理員)
    public static boolean updateCategory(Category c) throws Exception {
        String sql = "UPDATE category SET name = ? WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setInt(2, c.getCategory_id());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }
    //刪除商品類別(管理員)
    public static boolean deleteCategory(int id) throws Exception {
        String sql = "DELETE FROM category WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }
}

