package dao;

import model.cart;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class cartDAO {

    // 加入購物車：檢查庫存、是否已有該商品
    public String addToCart(cart cartItem) {
        try (Connection conn = DBUtil.getConnection()) {
            // 1. 先檢查商品是否存在
            String checkProductSql = "SELECT soh FROM product WHERE product_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkProductSql)) {
                checkStmt.setInt(1, cartItem.getProduct_id());
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) return "商品不存在";
                int soh = rs.getInt("soh");

                // 2. 檢查庫存數量
                if (soh == 0) return "商品庫存為 0，無法加入購物車";
                if (cartItem.getQuantity() > soh) return "欲購買數量超過庫存";

                // 3. 檢查購物車中是否已有該商品
                String checkCartSql = "SELECT quantity FROM cart WHERE member_id = ? AND product_id = ?";
                try (PreparedStatement cartStmt = conn.prepareStatement(checkCartSql)) {
                    cartStmt.setInt(1, cartItem.getMember_id());
                    cartStmt.setInt(2, cartItem.getProduct_id());
                    ResultSet cartRs = cartStmt.executeQuery();

                    if (cartRs.next()) {
                        // 若已有商品，則更新數量（覆蓋）
                        String updateSql = "UPDATE cart SET quantity = ?, create_at = ? WHERE member_id = ? AND product_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, cartItem.getQuantity());
                            updateStmt.setTimestamp(2, Timestamp.valueOf(cartItem.getCreate_at()));
                            updateStmt.setInt(3, cartItem.getMember_id());
                            updateStmt.setInt(4, cartItem.getProduct_id());
                            updateStmt.executeUpdate();
                            return "購物車已有此商品，數量已更新";
                        }
                    } else {
                        // 插入新商品
                        String insertSql = "INSERT INTO cart (member_id, product_id, quantity, create_at) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, cartItem.getMember_id());
                            insertStmt.setInt(2, cartItem.getProduct_id());
                            insertStmt.setInt(3, cartItem.getQuantity());
                            //insertStmt.setTimestamp(4, Timestamp.valueOf(cartItem.getCreate_at()));
                            insertStmt.executeUpdate();
                            return "加入購物車成功";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "加入購物車時發生錯誤";
        }
    }

    // 查看購物車
    public static List<cart> getCartByMemberId(int memberId) {
        List<cart> list = new ArrayList<>();
        String sql = "SELECT * FROM cart WHERE member_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cart c = new cart();
                c.setMember_id(rs.getInt("member_id"));
                c.setProduct_id(rs.getInt("product_id"));
                c.setQuantity(rs.getInt("quantity"));
                c.setCreate_at(rs.getTimestamp("create_at").toLocalDateTime());
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 移除購物車商品
    public boolean removeFromCart(int memberId, int productId)  {
        String sql = "DELETE FROM cart WHERE member_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void updateQuantity(int memberId, int productId, int quantity) throws Exception {
        String sql = "UPDATE cart SET quantity = ? WHERE member_id = ? AND product_id = ?";
        try ( Connection conn = DBUtil.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql) ) {
            ps.setInt(1, quantity);
            ps.setInt(2, memberId);
            ps.setInt(3, productId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("購物車中找不到 member_id=" + memberId + ", product_id=" + productId);
            }
        }
    }
}
