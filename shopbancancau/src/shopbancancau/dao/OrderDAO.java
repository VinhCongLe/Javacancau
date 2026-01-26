package shopbancancau.dao;
import shopbancancau.util.Session;


import java.sql.*;

public class OrderDAO {
	
    // 1️⃣ Tạo hóa đơn (orders)
	
	
	public int createOrder(Connection conn, int userId, int customerId, double total) throws SQLException {
	    String sql = "INSERT INTO orders(user_id, customer_id, order_date, total_amount) VALUES (?, ?, NOW(), ?)";

	    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setInt(1, userId);
	        ps.setInt(2, customerId);
	        ps.setDouble(3, total);
	        ps.executeUpdate();

	        ResultSet rs = ps.getGeneratedKeys();
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    }
	    return -1;
	}



    // 2️⃣ Thêm chi tiết hóa đơn (order_details)
	public void addOrderDetail(Connection conn, int orderId, int productId, int quantity, double price) throws SQLException {
	    String sql = "INSERT INTO order_details(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

	    PreparedStatement ps = conn.prepareStatement(sql);
	    ps.setInt(1, orderId);
	    ps.setInt(2, productId);
	    ps.setInt(3, quantity);
	    ps.setDouble(4, price);
	    ps.executeUpdate();
	}


    // 3️⃣ Trừ tồn kho (products)
	public void updateQuantity(Connection conn, int productId, int quantity) throws SQLException {
	    String sql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";

	    PreparedStatement ps = conn.prepareStatement(sql);
	    ps.setInt(1, quantity);
	    ps.setInt(2, productId);
	    ps.executeUpdate();
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
