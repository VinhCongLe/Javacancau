package shopbancancau.view;

import shopbancancau.controller.ProductController;
import shopbancancau.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ProductListView extends JFrame {

    private ProductController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnDelete, btnBack, btnViewDetail, btnEdit;
    private POSView parentView;

    public ProductListView(POSView parent) {
        this.parentView = parent;
        controller = new ProductController(this);
        initComponents();
        controller.loadAllProducts();
    }

    private void initComponents() {
        setTitle("Quản lý sản phẩm - Phụ kiện câu cá");
        setSize(1000, 500);           // Giảm chiều cao vì không còn form thông tin
        setMinimumSize(new Dimension(900, 400));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        
        // Window listener: dispose and show parent POSView
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                if (parentView != null) {
                    parentView.setVisible(true);
                    parentView.toFront();
                    parentView.requestFocus();
                }
            }
        });
        
        // Nút Quay lại ở góc trên trái
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setOpaque(false);
        btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setPreferredSize(new Dimension(120, 35));
        btnBack.setBackground(new Color(0, 120, 215)); // Màu xanh
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.addActionListener(e -> {
            dispose();
            if (parentView != null) {
                parentView.setVisible(true);
                parentView.toFront();
                parentView.requestFocus();
            }
        });
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

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
        table.setRowHeight(30); // Tăng từ 28 lên 30 để dễ đọc hơn

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        add(scrollPane, BorderLayout.CENTER);

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnAdd = new JButton("Thêm mới");
        btnDelete = new JButton("Xóa");
        btnViewDetail = new JButton("Xem chi tiết");
        btnEdit = new JButton("Sửa sản phẩm");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnViewDetail);
        buttonPanel.add(btnEdit);

        btnAdd.addActionListener(e -> addProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnViewDetail.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            ProductDetailView detailView = new ProductDetailView(productId);
            detailView.setVisible(true);
        });
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            EditProductView editView = new EditProductView(productId, this);
            editView.setVisible(true);
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addProduct() {
        AddProductView addView = new AddProductView(this);
        addView.setVisible(true);
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

    public ProductController getController() {
        return controller;
    }
}