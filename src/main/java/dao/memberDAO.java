package dao;

import model.Member;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class memberDAO {
    public List<Member> getAllMembers() throws Exception {
        List<Member> list = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        System.out.println("✅ 資料庫連線成功？ conn = " + conn);
        String sql = "SELECT * FROM member";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Member m = new Member();
            m.setMember_id(rs.getInt("member_id"));
            m.setName(rs.getString("name"));
            m.setPassword(rs.getString("password"));
            m.setPhone(rs.getString("phone"));
            m.setAddress(rs.getString("address"));
            m.setCreate_at(rs.getTimestamp("create_at").toLocalDateTime());
            m.setEmail(rs.getString("email"));

            list.add(m);
        }

        rs.close();
        ps.close();
        conn.close();

        return list;
    }
    public boolean insertMember(Member m) {
        String sql = "INSERT INTO member (name, password, phone, address, create_at, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getName());
            stmt.setString(2, m.getPassword());
            stmt.setString(3, m.getPhone());
            stmt.setString(4, m.getAddress());
            stmt.setTimestamp(5, Timestamp.valueOf(m.getCreate_at()));
            stmt.setString(6, m.getEmail());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("=== 註冊會員時發生錯誤 ===");
            e.printStackTrace();
            //return false;
            if (e instanceof SQLException) {
                SQLException se = (SQLException) e;
                System.err.println("SQLState: " + se.getSQLState());
                System.err.println("ErrorCode: " + se.getErrorCode());
                System.err.println("Message: " + se.getMessage());
            }

            throw new RuntimeException(e); // 讓 controller 接到錯誤並回傳 500
            //throw new RuntimeException(e);
        }
    }
    public Member findByEmail(String email, String password) {
        String sql = "SELECT * FROM member WHERE email = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Member m = new Member();
                m.setMember_id(rs.getInt("member_id"));
                m.setName(rs.getString("name"));
                m.setPassword(rs.getString("password"));
                m.setPhone(rs.getString("phone"));
                m.setAddress(rs.getString("address"));
                m.setCreate_at(rs.getTimestamp("create_at").toLocalDateTime());
                m.setEmail(rs.getString("email"));
                return m;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Member getMemberById(int id) throws Exception {
        String sql = "SELECT member_id, name, phone, address, create_at, email FROM member WHERE member_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member();
                    m.setMember_id(rs.getInt("member_id"));
                    m.setName(rs.getString("name"));
                    m.setPhone(rs.getString("phone"));
                    m.setAddress(rs.getString("address"));
                    m.setCreate_at(rs.getTimestamp("create_at").toLocalDateTime());
                    m.setEmail(rs.getString("email"));
                    return m;
                }
            }
        }
        return null;
    }
    public boolean updateMember(Member m) throws Exception {
        String sql = "UPDATE member SET name=?, password=?, phone=?, address=?, email=? WHERE member_id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getPassword());
            ps.setString(3, m.getPhone());
            ps.setString(4, m.getAddress());
            ps.setString(5, m.getEmail());
            ps.setInt(6, m.getMember_id());
            return ps.executeUpdate() > 0;
        }
    }
    public boolean deleteMember(int id) throws Exception {
        String sql = "DELETE FROM member WHERE member_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}