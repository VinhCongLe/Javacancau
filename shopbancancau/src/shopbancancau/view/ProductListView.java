package shopbancancau.view;

import shopbancancau.controller.ProductController;
import shopbancancau.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class ProductListView extends JFrame {

    private ProductController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPrice, txtQuantity, txtCategory, txtDescription, txtSupplier, txtImage;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JLabel lblImagePreview;  // ← Preview ảnh

    public ProductListView() {
        controller = new ProductController(this);
        initComponents();
        controller.loadAllProducts();
    }

    private void initComponents() {
        setTitle("Quản lý sản phẩm - Phụ kiện câu cá");
        setSize(900, 580);           // Tăng nhẹ để preview ảnh vừa đẹp
        setMinimumSize(new Dimension(800, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // ------------------- Bảng danh sách -------------------
        String[] columns = {"ID", "Tên sản phẩm", "Giá bán", "Tồn kho", "Loại", "Mô tả", "Nhà cung cấp", "Ảnh"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        add(scrollPane, BorderLayout.CENTER);

        // Sự kiện chọn dòng → điền form + preview ảnh
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                txtName.setText(getSafeValue(row, 1));
                txtPrice.setText(getSafeValue(row, 2));
                txtQuantity.setText(getSafeValue(row, 3));
                txtCategory.setText(getSafeValue(row, 4));
                txtDescription.setText(getSafeValue(row, 5));
                txtSupplier.setText(getSafeValue(row, 6));
                String imagePath = getSafeValue(row, 7);
                txtImage.setText(imagePath);

                // Load preview ảnh từ URL
                if (imagePath != null && !imagePath.trim().isEmpty()) {
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
        });

        // ------------------- Form thông tin + Preview ảnh -------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Tên sản phẩm
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1; txtName = new JTextField(25); formPanel.add(txtName, gbc);

        // Giá bán
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Giá bán (VNĐ):"), gbc);
        gbc.gridx = 1; txtPrice = new JTextField(12); formPanel.add(txtPrice, gbc);

        // Số lượng tồn
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Số lượng tồn:"), gbc);
        gbc.gridx = 1; txtQuantity = new JTextField(8); formPanel.add(txtQuantity, gbc);

        // Loại phụ kiện
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Loại phụ kiện:"), gbc);
        gbc.gridx = 1; txtCategory = new JTextField(18); formPanel.add(txtCategory, gbc);

        // Mô tả
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; txtDescription = new JTextField(25); formPanel.add(txtDescription, gbc);

        // Nhà cung cấp
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 1; txtSupplier = new JTextField(20); formPanel.add(txtSupplier, gbc);

        // Đường dẫn ảnh
        gbc.gridy = row++; gbc.gridx = 0;
        formPanel.add(new JLabel("Đường dẫn ảnh:"), gbc);
        gbc.gridx = 1; txtImage = new JTextField(28); formPanel.add(txtImage, gbc);

        // Preview ảnh bên phải (span toàn bộ chiều cao form)
        lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(220, 220));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImagePreview.setBackground(Color.WHITE);
        lblImagePreview.setOpaque(true);

        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 7; gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(lblImagePreview, gbc);

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnAdd = new JButton("Thêm mới");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());

        // Container tổng cho form + nút
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.add(Box.createVerticalGlue());
        formContainer.add(formPanel);
        formContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        formContainer.add(buttonPanel);
        formContainer.add(Box.createVerticalGlue());

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        southPanel.add(formContainer, BorderLayout.CENTER);

        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String getSafeValue(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    private void addProduct() {
        Product p = getProductFromForm();
        if (p != null) {
            controller.addProduct(p);
            clearForm();
        }
    }

    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Product p = getProductFromForm();
        if (p != null) {
            p.setProductId(id);
            controller.updateProduct(p);
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        controller.deleteProduct(id);
    }

    private Product getProductFromForm() {
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            double price = Double.parseDouble(txtPrice.getText().trim());
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            if (price <= 0 || qty < 0) {
                JOptionPane.showMessageDialog(this, "Giá phải > 0 và số lượng ≥ 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new Product(0, name, price, qty,
                    txtCategory.getText().trim(),
                    txtDescription.getText().trim(),
                    txtSupplier.getText().trim(),
                    txtImage.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá và số lượng phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtCategory.setText("");
        txtDescription.setText("");
        txtSupplier.setText("");
        txtImage.setText("");
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Chưa có ảnh");
        table.clearSelection();
    }

    public void displayProducts(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getProductName(),
                    p.getPrice(),
                    p.getQuantity(),
                    p.getCategory(),
                    p.getDescription(),
                    p.getSupplier(),
                    p.getImagePath()
            });
        }
    }
}