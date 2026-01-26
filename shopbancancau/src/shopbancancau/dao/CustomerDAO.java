package shopbancancau.dao;

import java.sql.*;

public class CustomerDAO {

	public Integer getOrCreateCustomer(Connection conn, String name, String phone) throws Exception {

	    String findSql = "SELECT customer_id FROM customers WHERE phone = ?";
	    try (PreparedStatement ps = conn.prepareStatement(findSql)) {
	        ps.setString(1, phone);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("customer_id");
	        }
	    }

	    String insertSql = "INSERT INTO customers(name, phone) VALUES (?, ?)";
	    try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setString(1, name);
	        ps.setString(2, phone);
	        ps.executeUpdate();
	        ResultSet rs = ps.getGeneratedKeys();
	        if (rs.next()) return rs.getInt(1);
	    }

	    throw new Exception("Không tạo được khách hàng");
	}
}