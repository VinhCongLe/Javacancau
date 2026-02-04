package shopbancancau.view;

import shopbancancau.dao.ProductDAO;
import shopbancancau.model.Product;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailView extends JFrame {

    private ProductDAO productDAO;
    private JLabel lblName, lblPrice, lblQuantity, lblCategory, lblDescription, lblSupplier;
    private JLabel lblImagePreview;

    public ProductDetailView(int productId) {
        this.productDAO = new ProductDAO();
        initComponents(productId);
    }

    private void initComponents(int productId) {
        setTitle("Chi tiết sản phẩm");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

    
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với ID: " + productId, 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

   
        JLabel lblTitle = new JLabel("Chi tiết sản phẩm", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

  
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

 
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

 
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblName = new JLabel(product.getProductName() != null ? product.getProductName() : "");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(lblName, gbc);

     
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        infoPanel.add(new JLabel("Giá bán:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblPrice = new JLabel(vnFormat.format(product.getPrice()) + " VNĐ");
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrice.setForeground(new Color(220, 20, 60));
        infoPanel.add(lblPrice, gbc);

     
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        infoPanel.add(new JLabel("Số lượng tồn:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblQuantity = new JLabel(String.valueOf(product.getQuantity()));
        lblQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lblQuantity, gbc);

      
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        infoPanel.add(new JLabel("Loại phụ kiện:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblCategory = new JLabel(product.getCategory() != null ? product.getCategory() : "");
        lblCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lblCategory, gbc);

      
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        infoPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblDescription = new JLabel(product.getDescription() != null ? product.getDescription() : "");
        lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lblDescription, gbc);

    
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        infoPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblSupplier = new JLabel(product.getSupplier() != null ? product.getSupplier() : "");
        lblSupplier.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lblSupplier, gbc);

     
        lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(250, 250));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        lblImagePreview.setBackground(Color.WHITE);
        lblImagePreview.setOpaque(true);
        lblImagePreview.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        
        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                URL url = new URL(imagePath);
                ImageIcon icon = new ImageIcon(url);
                Image scaled = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(scaled));
                lblImagePreview.setText("");
            } catch (Exception ex) {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("Không tải được ảnh");
            }
        } else {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("Chưa có ảnh");
        }

      
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(lblImagePreview, BorderLayout.EAST);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

       
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }
}
