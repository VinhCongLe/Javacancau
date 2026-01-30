package shopbancancau.view;

import shopbancancau.controller.ProductController;
import shopbancancau.model.Product;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AddProductView extends JFrame {

    private ProductController controller;
    private ProductListView parentView;
    private JTextField txtName, txtPrice, txtQuantity, txtCategory, txtDescription, txtSupplier, txtImage;
    private JLabel lblImagePreview;

    public AddProductView(ProductListView parent) {
        this.parentView = parent;
        this.controller = new ProductController(parent);
        initComponents();
    }

    private void initComponents() {
        setTitle("Thêm sản phẩm mới");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Title
        JLabel lblTitle = new JLabel("Thêm sản phẩm mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Tên sản phẩm
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtName = new JTextField(25);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtName, gbc);

        // Giá bán
        gbc.gridy = row++; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Giá bán (VNĐ):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPrice = new JTextField(12);
        txtPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtPrice, gbc);

        // Số lượng tồn
        gbc.gridy = row++; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Số lượng tồn:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtQuantity = new JTextField(8);
        txtQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtQuantity, gbc);

        // Loại phụ kiện
        gbc.gridy = row++; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Loại phụ kiện:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCategory = new JTextField(18);
        txtCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtCategory, gbc);

        // Mô tả
        gbc.gridy = row++; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDescription = new JTextField(25);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtDescription, gbc);

        // Nhà cung cấp
        gbc.gridy = row++; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSupplier = new JTextField(20);
        txtSupplier.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtSupplier, gbc);

        // Đường dẫn ảnh
        gbc.gridy = row++; gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Đường dẫn ảnh:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtImage = new JTextField(28);
        txtImage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtImage.addActionListener(e -> loadImagePreview());
        formPanel.add(txtImage, gbc);

        // Preview ảnh bên phải
        lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(220, 220));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImagePreview.setBackground(Color.WHITE);
        lblImagePreview.setOpaque(true);
        lblImagePreview.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 7; gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(lblImagePreview, gbc);

        // Layout: Form panel và image preview
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Nút Thêm và Đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnAdd = new JButton("Thêm");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setPreferredSize(new Dimension(100, 35));
        btnAdd.setBackground(new Color(0, 120, 215));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.addActionListener(e -> addProduct());
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadImagePreview() {
        String imagePath = txtImage.getText().trim();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                URL url = new URL(imagePath);
                ImageIcon icon = new ImageIcon(url);
                Image scaled = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
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
    }

    private void addProduct() {
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double price = Double.parseDouble(txtPrice.getText().trim());
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            
            if (price <= 0 || qty < 0) {
                JOptionPane.showMessageDialog(this, "Giá phải > 0 và số lượng ≥ 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product product = new Product(0, name, price, qty,
                    txtCategory.getText().trim(),
                    txtDescription.getText().trim(),
                    txtSupplier.getText().trim(),
                    txtImage.getText().trim());
            
            controller.addProduct(product);
            dispose(); // Đóng form sau khi thêm thành công
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá và số lượng phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}
