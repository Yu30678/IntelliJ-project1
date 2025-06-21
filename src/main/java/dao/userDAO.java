package dao;
import model.user;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class userDAO {// 1) 註冊
    public static user insertUser(user u) throws Exception {
        String sql = "INSERT INTO user (name, password, account, level) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getAccount());
            ps.setInt(4, u.getLevel());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setUserId(keys.getInt(1));
                }
            }

        }
        return u;
    }


    // 2) 登入（依 account/password 搜尋）
    public static Optional<user> findByAccountAndPassword(String account, String password) throws Exception {
        String sql = "SELECT user_id, name, account, level FROM user WHERE account = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 一定要有一個 public user() {} 的無參建構子
                    user u = new user();
                    u.setName(rs.getString("name"));
                    u.setAccount(rs.getString("account"));
                    u.setLevel(rs.getInt("level"));
                    // 不回傳密碼，所以不呼叫 u.setPassword(...)
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }

    // 3) 修改
    public static boolean updateUser(user u) throws Exception {
        String sql = "UPDATE user SET name = ?, password = ?, account = ?, level = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getAccount());
            ps.setInt(4, u.getLevel());
            ps.setInt(5, u.getUserId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // 4) 刪除
    public static boolean deleteUser(int id) throws Exception {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
