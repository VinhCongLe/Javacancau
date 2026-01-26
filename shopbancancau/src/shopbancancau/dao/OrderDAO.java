package shopbancancau.dao;

import java.sql.*;

public class OrderDAO {

    // 1️⃣ Tạo hóa đơn (orders)
    public int createOrder(int userId, int customerId, double total) {
        String sql = "INSERT INTO orders(user_id, customer_id, order_date, total_amount) VALUES (?, ?, NOW(), ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, customerId);
            ps.setDouble(3, total);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // order_id
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 2️⃣ Thêm chi tiết hóa đơn (order_details)
    public void addOrderDetail(int orderId, int productId, int quantity, double price) {
        String sql = "INSERT INTO order_details(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3️⃣ Trừ tồn kho (products)
    public void updateQuantity(int productId, int quantitySold) {
        String sql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantitySold);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ResultSet getAllOrders() {
        String sql = "SELECT order_id, order_date, total_amount FROM orders ORDER BY order_id DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ResultSet getOrderDetails(int orderId) {
        String sql = """
            SELECT p.product_name, od.quantity, od.price,
                   (od.quantity * od.price) AS total
            FROM order_details od
            JOIN products p ON od.product_id = p.product_id
            WHERE od.order_id = ?
        """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
