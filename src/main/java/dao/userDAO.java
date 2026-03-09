package dao;
import model.Member;
import model.user;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class userDAO {
    //瀏覽管理員列表(管理員)
    public List<user> getUsers()throws Exception {
        List<user> list = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        System.out.println("✅ 資料庫連線成功？ conn = " + conn);
        String sql = "SELECT * FROM user";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            user u = new user();
            u.setUserId(rs.getInt("user_id"));
            u.setName(rs.getString("name"));
            u.setPassword(rs.getString("password"));
            u.setAccount(rs.getString("account"));
            u.setLevel(Integer.parseInt(rs.getString("level")));
            list.add(u);
        }
        rs.close();
        ps.close();
        conn.close();

        return list;
    }
    //管理員註冊(管理員)
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


    //管理員登入（依 account/password 搜尋）
    public static Optional<user> findByAccountAndPassword(String account, String password) throws Exception {
        String sql = "SELECT user_id, name, account, level FROM user WHERE account = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

    //修改管理員資訊
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

    //刪除管理員
    public static boolean deleteUser(int id) throws Exception {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
