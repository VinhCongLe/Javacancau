package shopbancancau.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import shopbancancau.model.Product;
import shopbancancau.util.Session;

public class POSView extends JFrame {

    /* ===== MENU ===== */
    private JMenuItem menuOrderHistory;
    private JMenuItem menuCreateUser;
    private JMenuItem menuQuanLySanPham;
    private JMenuItem menuLogout;
    private JMenuItem menuUserName;

    /* ===== KHU A ===== */
    private JComboBox<Product> cbProduct;
    private JTextField txtQuantity;
    private JButton btnAdd;
    private JButton btnRemove;

    /* ===== KHU B ===== */
    private JTable table;
    private DefaultTableModel tableModel;

    /* ===== KHU C ===== */
    private JTextField txtCustomerName;
    private JTextField txtPhone;
    private JLabel lblTotal;
    private JButton btnPay;

    public POSView(String role) {
        initUI(role);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            menuOrderHistory.setVisible(false);
            menuCreateUser.setVisible(false);
            menuQuanLySanPham.setVisible(false);
        }
    }

    public POSView() {
        initUI("");
    }

    private void initUI(String role) {
        setTitle("Bán hàng (POS) - Phụ kiện câu cá");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        /* ===== MENU ===== */
        JMenuBar menuBar = new JMenuBar();
        JMenu menuManage = new JMenu("Quản lý");

        menuQuanLySanPham = new JMenuItem("Quản lý sản phẩm");
        menuQuanLySanPham.addActionListener(e -> new ProductListView().setVisible(true));
        menuManage.add(menuQuanLySanPham);

        menuCreateUser = new JMenuItem("Quản lý tài khoản");
        menuManage.add(menuCreateUser);

        menuOrderHistory = new JMenuItem("Lịch sử hóa đơn");
        menuManage.add(menuOrderHistory);

        // Tên user
        menuUserName = new JMenuItem();
        if (Session.currentUser != null) {
            String displayName = Session.currentUser.getUsername();
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = "User";
            }
            menuUserName.setText(displayName.toUpperCase());
            menuUserName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            menuUserName.setForeground(new Color(0, 120, 215));
            menuUserName.setEnabled(false);
            menuUserName.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        } else {
            menuUserName.setVisible(false);
        }

        menuLogout = new JMenuItem("Đăng xuất");
        menuLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuLogout.setForeground(new Color(200, 35, 51));
        menuLogout.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        menuBar.add(menuManage);
        menuBar.add(menuUserName);
        menuBar.add(menuLogout);
        setJMenuBar(menuBar);

        /* ===== KHU A - Chọn sản phẩm ===== */
        JPanel panelLeft = new JPanel(new BorderLayout(10, 10));
        panelLeft.setBorder(BorderFactory.createTitledBorder("Chọn sản phẩm"));
        panelLeft.setPreferredSize(new Dimension(420, 0));

        // Phần sản phẩm
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.add(new JLabel("Sản phẩm"), BorderLayout.NORTH);
        cbProduct = new JComboBox<>();
        cbProduct.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbProduct.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        topPanel.add(cbProduct, BorderLayout.CENTER);

        // Phần số lượng + nút
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Label Số lượng
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(new JLabel("Số lượng"), gbc);

        // Ô nhập số lượng - căn giữa
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtQuantity = new JTextField();
        txtQuantity.setHorizontalAlignment(JTextField.CENTER);
        txtQuantity.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtQuantity.setPreferredSize(new Dimension(160, 42));
        centerPanel.add(txtQuantity, gbc);

        // Chỉ cho phép nhập số
        txtQuantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        // Nút THÊM - dùng text thay emoji để đảm bảo hiển thị
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnAdd = new JButton("+ THÊM");
        btnAdd.setPreferredSize(new Dimension(180, 48));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // Nếu muốn thử emoji, uncomment dòng dưới (nhưng có thể vẫn không hiển thị trên Windows)
        // btnAdd.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        centerPanel.add(btnAdd, gbc);

        // Nút XÓA - dùng text "XÓA" thay 🗑
        gbc.gridy = 3;
        btnRemove = new JButton("- XÓA");
        btnRemove.setPreferredSize(new Dimension(180, 48));
        btnRemove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // Nếu muốn thử emoji thùng rác: btnRemove.setText("\uD83D\uDDD1 XÓA"); + font Segoe UI Emoji
        centerPanel.add(btnRemove, gbc);

        // Ghép panelLeft
        panelLeft.add(topPanel, BorderLayout.NORTH);
        panelLeft.add(centerPanel, BorderLayout.CENTER);

        /* ===== KHU B - Hóa đơn ===== */
        String[] cols = {"Sản phẩm", "SL", "Giá", "Thành tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(280);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Hóa đơn"));

        /* ===== KHU C - Thông tin khách & Thanh toán ===== */
        JPanel panelBottom = new JPanel(new BorderLayout(10, 10));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thông tin khách
        JPanel panelCustomer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JLabel lblTenKhach = new JLabel("Tên khách:");
        lblTenKhach.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtCustomerName = new JTextField(25);
        txtCustomerName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCustomerName.setPreferredSize(new Dimension(280, 32));

        JLabel lblSDT = new JLabel("SĐT:");
        lblSDT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtPhone = new JTextField(18);
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPhone.setPreferredSize(new Dimension(180, 32));

        panelCustomer.add(lblTenKhach);
        panelCustomer.add(txtCustomerName);
        panelCustomer.add(lblSDT);
        panelCustomer.add(txtPhone);

        // Tổng tiền + Thanh toán
        JPanel panelPay = new JPanel(new BorderLayout(10, 10));
        panelPay.setBorder(BorderFactory.createTitledBorder("Tổng tiền"));
        lblTotal = new JLabel("0 VND", SwingConstants.CENTER);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(new Color(220, 20, 60));

        btnPay = new JButton("THANH TOÁN");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.setBackground(new Color(0, 120, 215));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setPreferredSize(new Dimension(200, 60));

        panelPay.add(lblTotal, BorderLayout.CENTER);
        panelPay.add(btnPay, BorderLayout.EAST);

        panelBottom.add(panelCustomer, BorderLayout.NORTH);
        panelBottom.add(panelPay, BorderLayout.SOUTH);

        // Thêm vào frame
        add(panelLeft, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
    }

    /* ===== GETTER & LISTENER ===== */
    public JComboBox<Product> getCbProduct() { return cbProduct; }
    public JTextField getTxtQuantity() { return txtQuantity; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTable() { return table; }
    public JLabel getLblTotal() { return lblTotal; }
    public String getCustomerName() { return txtCustomerName.getText(); }
    public String getPhone() { return txtPhone.getText(); }
    public JButton getBtnRemove() { return btnRemove; }
    public JMenuItem getMenuOrderHistory() { return menuOrderHistory; }
    public JMenuItem getMenuCreateUser() { return menuCreateUser; }
    public JMenuItem getMenuLogout() { return menuLogout; }
    public JMenuItem getMenuQuanLySanPham() { return menuQuanLySanPham; }

    public void clearCustomerInfo() {
        txtCustomerName.setText("");
        txtPhone.setText("");
    }

    public void addAddListener(ActionListener l) {
        btnAdd.addActionListener(l);
    }

    public void addPayListener(ActionListener l) {
        btnPay.addActionListener(l);
    }

    public void addRemoveListener(ActionListener l) {
        btnRemove.addActionListener(l);
    }
}