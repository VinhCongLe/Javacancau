package shopbancancau.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

	// Lấy tất cả khách hàng
	public List<CustomerInfo> getAllCustomers() {
	    List<CustomerInfo> list = new ArrayList<>();
	    String sql = "SELECT customer_id, name, phone FROM customers ORDER BY customer_id ASC";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        
	        while (rs.next()) {
	            CustomerInfo customer = new CustomerInfo();
	            customer.setId(rs.getInt("customer_id"));
	            customer.setName(rs.getString("name"));
	            customer.setPhone(rs.getString("phone"));
	            list.add(customer);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return list;
	}

	// class để lưu thông tin khách hàng
	public static class CustomerInfo {
	    private int id;
	    private String name;
	    private String phone;

	    public int getId() { return id; }
	    public void setId(int id) { this.id = id; }

	    public String getName() { return name; }
	    public void setName(String name) { this.name = name; }

	    public String getPhone() { return phone; }
	    public void setPhone(String phone) { this.phone = phone; }
	}
}