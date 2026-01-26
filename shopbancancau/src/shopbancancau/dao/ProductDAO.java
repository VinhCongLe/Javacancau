package shopbancancau.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import shopbancancau.model.Product;

public class ProductDAO {

    /* ================= LẤY TẤT CẢ SẢN PHẨM ================= */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setCategory(rs.getString("category"));
                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ================= LẤY SẢN PHẨM THEO TÊN ================= */
    public Product getProductByName(String name) {
        String sql = "SELECT * FROM products WHERE product_name = ?";
        Product p = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setCategory(rs.getString("category"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return p;
    }
    
    
    public int getProductIdByName(String name) {
        String sql = "SELECT product_id FROM products WHERE product_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
