package dao;

import model.cart;
import model.order_detail;
import model.order;
import util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class orderDAO {

    /**
     * 將購物車所有商品一次性下訂單並處理庫存與購物車清除，整個流程在單一交易中完成
     */
    public static int placeOrderFromCart(int memberId) throws Exception {
        String sqlInsertOrder = "INSERT INTO `order` (member_id, create_at) VALUES (?, ?)";
        String sqlSelectCart  = "SELECT c.product_id, c.quantity, p.price, p.soh, p.is_active FROM cart c JOIN product p ON c.product_id = p.product_id WHERE c.member_id = ?";
        String sqlInsertDetail= "INSERT INTO order_detail (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE product SET soh = soh - ? WHERE product_id = ?";
        String sqlDeleteCart  = "DELETE FROM cart WHERE member_id = ? AND product_id = ?";

        Connection conn = DBUtil.getConnection();
        try {
            conn.setAutoCommit(false);
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, memberId);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) orderId = rs.getInt(1);
                    else throw new SQLException("Failed to retrieve order_id");
                }
            }

            try (
                    PreparedStatement psSelect = conn.prepareStatement(sqlSelectCart);
                    PreparedStatement psDetail = conn.prepareStatement(sqlInsertDetail);
                    PreparedStatement psStock  = conn.prepareStatement(sqlUpdateStock);
                    PreparedStatement psCart   = conn.prepareStatement(sqlDeleteCart)
            ) {
                psSelect.setInt(1, memberId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    while (rs.next()) {
                        int pid = rs.getInt("product_id");
                        int qty = rs.getInt("quantity");
                        BigDecimal price = rs.getBigDecimal("price");
                        int soh = rs.getInt("soh");
                        boolean active = rs.getBoolean("is_active");
                        if (!active || soh == 0 || qty > soh) {
                            throw new SQLException("Cart validation failed for product " + pid);
                        }
                        psDetail.setInt(1, orderId);
                        psDetail.setInt(2, pid);
                        psDetail.setInt(3, qty);
                        psDetail.setBigDecimal(4, price);
                        psDetail.executeUpdate();
                        psStock.setInt(1, qty);
                        psStock.setInt(2, pid);
                        psStock.executeUpdate();
                        psCart.setInt(1, memberId);
                        psCart.setInt(2, pid);
                        psCart.executeUpdate();
                    }
                }
            }
            conn.commit();
            return orderId;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public static List<order> getOrdersByMemberId(int memberId) throws Exception {
        String sql = "SELECT order_id, member_id, create_at FROM `order` WHERE member_id = ? ORDER BY create_at DESC";
        List<order> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    order o = new order();
                    o.setOrder_id(rs.getInt("order_id"));
                    o.setMember_id(rs.getInt("member_id"));
                    o.setCreate_at(rs.getTimestamp("create_at").toLocalDateTime());
                    list.add(o);
                }
            }
        }
        return list;
    }

    public static List<order_detail> getOrderDetailsByOrderId(int orderId) throws Exception {
        String sql = "SELECT product_id, quantity, price FROM order_detail WHERE order_id = ?";
        List<order_detail> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    order_detail od = new order_detail();
                    od.setOrder_id(orderId);
                    od.setProduct_id(rs.getInt("product_id"));
                    od.setQuantity(rs.getInt("quantity"));
                    od.setPrice(rs.getBigDecimal("price"));
                    list.add(od);
                }
            }
        }
        return list;
    }

    public static boolean validateCartBeforeOrder(int memberId) throws Exception {
        String sql = "SELECT c.quantity, p.soh, p.is_active FROM cart c JOIN product p ON c.product_id = p.product_id WHERE c.member_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int qty = rs.getInt("quantity");
                    if (!rs.getBoolean("is_active") || rs.getInt("soh") == 0 || qty > rs.getInt("soh")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}