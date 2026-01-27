package shopbancancau.dao;

import shopbancancau.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    /* LẤY TẤT CẢ SẢN PHẨM (dùng cho quản lý sản phẩm) */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY product_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = mapResultSetToProduct(rs);
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* LẤY SẢN PHẨM THEO ID */
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* LẤY SẢN PHẨM THEO TÊN (dùng cho POS/bán hàng) */
    public Product getProductByName(String name) {
        String sql = "SELECT * FROM products WHERE product_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* LẤY ID SẢN PHẨM THEO TÊN (đã sửa để luôn return int) */
    public int getProductIdByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return -1; // Tên không hợp lệ
        }
        
        String sql = "SELECT product_id FROM products WHERE product_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy sản phẩm → trả về -1
    }

    /* THÊM SẢN PHẨM MỚI */
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (product_name, price, quantity, category, description, supplier, image_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setString(4, p.getCategory());
            ps.setString(5, p.getDescription());
            ps.setString(6, p.getSupplier());
            ps.setString(7, p.getImagePath());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* SỬA SẢN PHẨM */
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET product_name=?, price=?, quantity=?, category=?, " +
                     "description=?, supplier=?, image_path=? WHERE product_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setString(4, p.getCategory());
            ps.setString(5, p.getDescription());
            ps.setString(6, p.getSupplier());
            ps.setString(7, p.getImagePath());
            ps.setInt(8, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* XÓA SẢN PHẨM */
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm hỗ trợ map dữ liệu từ ResultSet sang Product
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setPrice(rs.getDouble("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setCategory(rs.getString("category"));
        p.setDescription(rs.getString("description"));
        p.setSupplier(rs.getString("supplier"));
        p.setImagePath(rs.getString("image_path"));
        return p;
    }
}