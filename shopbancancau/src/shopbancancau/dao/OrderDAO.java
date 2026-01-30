package shopbancancau.dao;

import shopbancancau.dao.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class OrderDAO {

    // 1️⃣ Tạo hóa đơn - LƯU THỜI GIAN ĐÚNG MÚI GIỜ VIỆT NAM
    public int createOrder(Connection conn, int userId, int customerId, double total) throws SQLException {
        String sql = "INSERT INTO orders (user_id, customer_id, order_date, total_amount) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, customerId);

            // Lấy thời gian hiện tại đúng múi giờ Việt Nam (+07:00)
            LocalDateTime nowVN = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            Timestamp timestamp = Timestamp.valueOf(nowVN);

            ps.setTimestamp(3, timestamp);
            ps.setDouble(4, total);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        // Nếu thất bại, trả -1
        return -1;
    }

    // 2️⃣ Thêm chi tiết hóa đơn
    public void addOrderDetail(Connection conn, int orderId, int productId, int quantity, double price) throws SQLException {
        String sql = "INSERT INTO order_details (order_id, product_id, quantity, price) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);
            ps.executeUpdate();
        }
    }

    // 3️⃣ Trừ tồn kho sản phẩm
    public void updateQuantity(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    // 4️⃣ Lấy tất cả hóa đơn (JOIN với customers để lấy tên và SĐT)
    public ResultSet getAllOrders() throws SQLException {
        String sql = "SELECT o.order_id, o.order_date, o.total_amount, " +
                     "c.name AS customer_name, c.phone AS customer_phone " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                     "ORDER BY o.order_id DESC";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.executeQuery();  // Caller phải close ResultSet + Statement + Connection
    }

    // 5️⃣ Lọc hóa đơn theo khoảng ngày (JOIN với customers để lấy tên và SĐT)
    public ResultSet getOrdersByDate(Date fromDate, Date toDate) throws SQLException {
        String sql = "SELECT o.order_id, o.order_date, o.total_amount, " +
                     "c.name AS customer_name, c.phone AS customer_phone " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                     "WHERE o.order_date BETWEEN ? AND ? " +
                     "ORDER BY o.order_id DESC";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        // Chuyển Date → Timestamp, set end-of-day cho toDate nếu cần (tùy logic)
        Timestamp fromTs = new Timestamp(fromDate.getTime());
        Timestamp toTs = new Timestamp(toDate.getTime() + (24 * 60 * 60 * 1000 - 1)); // Đến 23:59:59.999 của toDate

        ps.setTimestamp(1, fromTs);
        ps.setTimestamp(2, toTs);

        return ps.executeQuery();
    }

    // 6️⃣ Lấy tất cả hóa đơn của một user cụ thể (JOIN với customers để lấy tên và SĐT)
    public ResultSet getOrdersByUserId(int userId) throws SQLException {
        String sql = "SELECT o.order_id, o.order_date, o.total_amount, " +
                     "c.name AS customer_name, c.phone AS customer_phone " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                     "WHERE o.user_id = ? " +
                     "ORDER BY o.order_id DESC";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        return ps.executeQuery();
    }

    // 7️⃣ Lọc hóa đơn của một user theo khoảng ngày (JOIN với customers để lấy tên và SĐT)
    public ResultSet getOrdersByUserIdAndDate(int userId, Date fromDate, Date toDate) throws SQLException {
        String sql = "SELECT o.order_id, o.order_date, o.total_amount, " +
                     "c.name AS customer_name, c.phone AS customer_phone " +
                     "FROM orders o " +
                     "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                     "WHERE o.user_id = ? AND o.order_date BETWEEN ? AND ? " +
                     "ORDER BY o.order_id DESC";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, userId);
        Timestamp fromTs = new Timestamp(fromDate.getTime());
        Timestamp toTs = new Timestamp(toDate.getTime() + (24 * 60 * 60 * 1000 - 1));

        ps.setTimestamp(2, fromTs);
        ps.setTimestamp(3, toTs);

        return ps.executeQuery();
    }

    // 8️⃣ Lấy chi tiết hóa đơn theo order_id
    public ResultSet getOrderDetails(int orderId) throws SQLException {
        String sql = "SELECT p.product_name, od.quantity, od.price, " +
                     "(od.quantity * od.price) AS total " +
                     "FROM order_details od " +
                     "JOIN products p ON od.product_id = p.product_id " +
                     "WHERE od.order_id = ?";

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, orderId);
        return ps.executeQuery();
    }

    // 9️⃣ Xóa đơn hàng (xóa order_details trước, sau đó xóa order)
    public boolean deleteOrder(int orderId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Xóa order_details trước (do foreign key constraint)
            String sqlDeleteDetails = "DELETE FROM order_details WHERE order_id = ?";
            try (PreparedStatement psDetails = conn.prepareStatement(sqlDeleteDetails)) {
                psDetails.setInt(1, orderId);
                psDetails.executeUpdate();
            }

            // Xóa order
            String sqlDeleteOrder = "DELETE FROM orders WHERE order_id = ?";
            try (PreparedStatement psOrder = conn.prepareStatement(sqlDeleteOrder)) {
                psOrder.setInt(1, orderId);
                int rowsAffected = psOrder.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction nếu thành công
                    return true;
                } else {
                    conn.rollback(); // Rollback nếu không có dòng nào bị xóa
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback nếu có lỗi
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Khôi phục auto-commit
                conn.close();
            }
        }
    }
}