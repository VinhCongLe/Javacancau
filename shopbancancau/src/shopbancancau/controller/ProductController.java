package shopbancancau.controller;

import shopbancancau.dao.ProductDAO;
import shopbancancau.model.Product;
import shopbancancau.view.ProductListView;

import javax.swing.*;
import java.util.List;

public class ProductController {
    private ProductDAO productDAO;
    private ProductListView view;

    public ProductController(ProductListView view) {
        this.productDAO = new ProductDAO();
        this.view = view;
    }

    public void loadAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        view.displayProducts(products);
    }

    public void addProduct(Product product) {
        if (productDAO.addProduct(product)) {
            JOptionPane.showMessageDialog(view, "Thêm sản phẩm thành công!");
            loadAllProducts(); 
        } else {
            JOptionPane.showMessageDialog(view, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateProduct(Product product) {
        if (productDAO.updateProduct(product)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
            loadAllProducts();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteProduct(int productId) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn xóa sản phẩm này?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(productId)) {
                JOptionPane.showMessageDialog(view, "Xóa thành công!");
                loadAllProducts();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }
}