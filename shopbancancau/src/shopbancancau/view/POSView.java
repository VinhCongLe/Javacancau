package shopbancancau.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import shopbancancau.model.Product;
import shopbancancau.util.Session;

public class POSView extends JFrame {

    /* ===== SINGLETON ===== */
    private static POSView instance;

    /* ===== MENU ===== */
    private JMenuItem menuOrderHistory;
    private JMenuItem menuCreateUser;
    private JMenuItem menuQuanLySanPham;
    private JButton menuLogout; // Changed to JButton to place next to username
    private JLabel menuUserName; // Changed to JLabel to place next to logout button

    /* ===== CARD LAYOUT ===== */
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel posPanel;
    private UserManagementPanel userManagementPanel;
    private String currentCard = "POS"; // Track current panel

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
        instance = this;
        initUI(role);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            menuOrderHistory.setVisible(false);
            menuCreateUser.setVisible(false);
            menuQuanLySanPham.setVisible(false);
        }
    }

    public POSView() {
        instance = this;
        initUI("");
    }

    /* ===== SINGLETON GETTER ===== */
    public static POSView getInstance() {
        return instance;
    }

    private void initUI(String role) {
        setTitle("Bán hàng (POS) - Phụ kiện câu cá");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Window listener: Nếu đang ở UserManagement thì quay về POS, nếu ở POS thì hỏi xác nhận thoát
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if ("USER_MANAGEMENT".equals(currentCard)) {
                    // Nếu đang ở UserManagement, quay về POS
                    showPOSPanel();
                } else {
                    // Nếu đang ở POS, hỏi xác nhận thoát
                    int confirm = JOptionPane.showConfirmDialog(
                        POSView.this,
                        "Bạn có chắc chắn muốn thoát không?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
        });
        
        // CardLayout để chuyển đổi giữa POS và UserManagement
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Tạo POS panel
        posPanel = createPOSPanel();
        cardPanel.add(posPanel, "POS");
        
        // Tạo UserManagement panel
        userManagementPanel = new UserManagementPanel();
        cardPanel.add(userManagementPanel, "USER_MANAGEMENT");
        
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);

        /* ===== MENU ===== */
        JMenuBar menuBar = new JMenuBar();
        JMenu menuManage = new JMenu("Quản lý");

        menuQuanLySanPham = new JMenuItem("Quản lý sản phẩm");
        menuQuanLySanPham.addActionListener(e -> {
            ProductListView productView = new ProductListView(POSView.this);
            setVisible(false); // Hide POSView when opening ProductListView
            productView.setVisible(true);
        });
        menuManage.add(menuQuanLySanPham);

        menuCreateUser = new JMenuItem("Quản lý tài khoản");
        menuManage.add(menuCreateUser);

        menuOrderHistory = new JMenuItem("Thống kê");
        menuManage.add(menuOrderHistory);

        menuBar.add(menuManage);

        // Panel chứa tên user và nút Đăng xuất
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        
        // Tên user label với prefix "Tài khoản: "
        menuUserName = new JLabel();
        if (Session.currentUser != null) {
            String displayName = Session.currentUser.getUsername();
            String userRole = Session.currentUser.getRole();
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = "User";
            }
            // Hiển thị "Tài khoản: [username] ([role])"
            menuUserName.setText("Tài khoản: " + displayName + 
                (userRole != null && !userRole.trim().isEmpty() ? " (" + userRole + ")" : ""));
            menuUserName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            menuUserName.setForeground(new Color(0, 120, 215));
            menuUserName.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        } else {
            menuUserName.setVisible(false);
        }
        userPanel.add(menuUserName);

        // Nút Đăng xuất
        menuLogout = new JButton("Đăng xuất");
        menuLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        menuLogout.setPreferredSize(new Dimension(100, 30));
        menuLogout.setBackground(new Color(200, 35, 51)); // Màu đỏ
        menuLogout.setForeground(Color.WHITE);
        menuLogout.setFocusPainted(false);
        menuLogout.setBorderPainted(false);
        menuLogout.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        userPanel.add(menuLogout);

        menuBar.add(Box.createHorizontalGlue()); // Đẩy userPanel sang bên phải
        menuBar.add(userPanel);
        setJMenuBar(menuBar);
    }

    private JPanel createPOSPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

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

        // Thêm vào panel
        panel.add(panelLeft, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelBottom, BorderLayout.SOUTH);
        
        return panel;
    }

    public void showPOSPanel() {
        currentCard = "POS";
        cardLayout.show(cardPanel, "POS");
        setTitle("Bán hàng (POS) - Phụ kiện câu cá");
        setVisible(true);
        toFront();
        requestFocus();
    }

    public void showUserManagementPanel() {
        currentCard = "USER_MANAGEMENT";
        cardLayout.show(cardPanel, "USER_MANAGEMENT");
        setTitle("Quản lý tài khoản - Phụ kiện câu cá");
        userManagementPanel.refreshUserList();
        setVisible(true);
        toFront();
        requestFocus();
    }

    public UserManagementPanel getUserManagementPanel() {
        return userManagementPanel;
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
    public JButton getMenuLogout() { return menuLogout; }
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

    /* ===== USER MANAGEMENT PANEL ===== */
    public class UserManagementPanel extends JPanel {
        private JTable userTable;
        private DefaultTableModel userTableModel;
        private JTextField txtUsername;
        private JComboBox<String> cbRole;
        private JButton btnAdd;
        private JButton btnSave;
        private JButton btnDelete;
        private JButton btnBack;
        private int selectedUserId = -1;
        private POSView parentView;

        public UserManagementPanel() {
            this.parentView = POSView.getInstance();
            initUserManagementUI();
        }

        private void initUserManagementUI() {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setBackground(new Color(245, 245, 245)); // Màu nền xám nhạt

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
                if (parentView != null) {
                    parentView.showPOSPanel();
                }
            });
            topPanel.add(btnBack);
            add(topPanel, BorderLayout.NORTH);

            // Bên trái: JTable danh sách user (70% chiều rộng)
            String[] cols = {"ID", "Username", "Role"};
            userTableModel = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            userTable = new JTable(userTableModel);
            userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            userTable.getColumnModel().getColumn(1).setPreferredWidth(180);
            userTable.getColumnModel().getColumn(2).setPreferredWidth(80);
            userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            userTable.setRowHeight(24);
            userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            userTable.setBackground(Color.WHITE);
            
            // Listener khi chọn dòng trong table
            userTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int row = userTable.getSelectedRow();
                    if (row >= 0) {
                        loadUserToForm(row);
                    } else {
                        clearForm();
                    }
                }
            });

            JScrollPane scrollTable = new JScrollPane(userTable);
            scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách tài khoản"));
            scrollTable.setPreferredSize(new Dimension(0, 0));

            // Bên phải: Form chi tiết nhỏ gọn (30% chiều rộng)
            JPanel formPanel = new JPanel(new BorderLayout(5, 5));
            formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
            formPanel.setBackground(new Color(245, 245, 245));
            formPanel.setPreferredSize(new Dimension(280, 0));

            // Form fields - chỉ Username và Role
            JPanel fieldsPanel = new JPanel(new GridBagLayout());
            fieldsPanel.setBackground(new Color(245, 245, 245));
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Username
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel lblUsername = new JLabel("Username:");
            lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
            fieldsPanel.add(lblUsername, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            txtUsername = new JTextField();
            txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtUsername.setPreferredSize(new Dimension(0, 32));
            txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            fieldsPanel.add(txtUsername, gbc);

            // Role
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.weightx = 0;
            JLabel lblRole = new JLabel("Role:");
            lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
            fieldsPanel.add(lblRole, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
            cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cbRole.setPreferredSize(new Dimension(0, 32));
            fieldsPanel.add(cbRole, gbc);

            formPanel.add(fieldsPanel, BorderLayout.CENTER);

            // Buttons panel - 3 nút: Thêm mới, Lưu, Xóa (ở dưới cùng)
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
            buttonsPanel.setBackground(new Color(245, 245, 245));
            buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            btnAdd = new JButton("Thêm mới");
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnAdd.setPreferredSize(new Dimension(100, 38));
            btnAdd.setBackground(new Color(0, 120, 215)); // Màu xanh
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setFocusPainted(false);
            btnAdd.setBorderPainted(false);

            btnSave = new JButton("Lưu");
            btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnSave.setPreferredSize(new Dimension(100, 38));
            btnSave.setBackground(new Color(0, 120, 215)); // Màu xanh dương
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.setBorderPainted(false);
            btnSave.setEnabled(false); // Disable mặc định

            btnDelete = new JButton("Xóa");
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnDelete.setPreferredSize(new Dimension(100, 38));
            btnDelete.setBackground(new Color(200, 35, 51)); // Màu đỏ
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setEnabled(false); // Disable mặc định

            buttonsPanel.add(btnAdd);
            buttonsPanel.add(btnSave);
            buttonsPanel.add(btnDelete);

            formPanel.add(buttonsPanel, BorderLayout.SOUTH);

            // Layout chính với tỷ lệ 70% - 30%
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTable, formPanel);
            splitPane.setDividerLocation(0.70); // 70% cho table, 30% cho form
            splitPane.setDividerSize(6);
            splitPane.setResizeWeight(0.70);
            splitPane.setBorder(null);

            add(splitPane, BorderLayout.CENTER);

            // Load dữ liệu ban đầu
            refreshUserList();
        }

        public void refreshUserList() {
            int currentSelectedId = selectedUserId; // Lưu lại ID đang chọn
            
            userTableModel.setRowCount(0);
            shopbancancau.dao.UserDAO userDAO = new shopbancancau.dao.UserDAO();
            java.util.List<shopbancancau.model.User> users = userDAO.getAllUsers();
            int rowToSelect = -1;
            
            for (int i = 0; i < users.size(); i++) {
                shopbancancau.model.User user = users.get(i);
                userTableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole()
                });
                // Tìm lại dòng vừa chọn để select lại
                if (currentSelectedId > 0 && user.getUserId() == currentSelectedId) {
                    rowToSelect = i;
                }
            }
            
            // Nếu có dòng đã chọn trước đó, select lại
            if (rowToSelect >= 0) {
                userTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                loadUserToForm(rowToSelect);
            } else {
                clearForm();
            }
        }

        private void loadUserToForm(int row) {
            if (row < 0 || row >= userTableModel.getRowCount()) return;
            
            selectedUserId = (Integer) userTableModel.getValueAt(row, 0);
            String username = userTableModel.getValueAt(row, 1).toString();
            String role = userTableModel.getValueAt(row, 2).toString();
            
            txtUsername.setText(username);
            cbRole.setSelectedItem(role);
            
            // Username luôn editable
            txtUsername.setEditable(true);
            txtUsername.setBackground(Color.WHITE);
            
            // Role: disable nếu là tài khoản đang đăng nhập
            boolean isCurrentUser = Session.currentUser != null && 
                                    username.equals(Session.currentUser.getUsername());
            cbRole.setEnabled(!isCurrentUser);
            if (isCurrentUser) {
                cbRole.setBackground(new Color(240, 240, 240));
            } else {
                cbRole.setBackground(Color.WHITE);
            }
            
            updateButtonStates();
        }

        private void clearForm() {
            selectedUserId = -1;
            txtUsername.setText("");
            cbRole.setSelectedIndex(0);
            
            // Enable khi thêm mới
            txtUsername.setEditable(true);
            txtUsername.setBackground(Color.WHITE);
            cbRole.setEnabled(true);
            cbRole.setBackground(Color.WHITE);
            
            userTable.clearSelection();
            updateButtonStates();
        }

        private void updateButtonStates() {
            boolean hasSelection = selectedUserId > 0;
            boolean isAdmin = "ADMIN".equalsIgnoreCase(Session.currentUser != null ? Session.currentUser.getRole() : "");
            boolean isCurrentUser = false;
            
            if (hasSelection && Session.currentUser != null) {
                String selectedUsername = txtUsername.getText();
                String currentUsername = Session.currentUser.getUsername();
                isCurrentUser = selectedUsername.equals(currentUsername);
            }
            
            // Nút Thêm mới: luôn enable nếu là ADMIN
            btnAdd.setEnabled(isAdmin);
            
            // Nút Lưu: enable khi có selection và là ADMIN
            btnSave.setEnabled(isAdmin && hasSelection);
            
            // Nút Xóa: chỉ enable khi có selection và không phải current user
            btnDelete.setEnabled(isAdmin && hasSelection && !isCurrentUser);
        }

        public JButton getBtnAdd() { return btnAdd; }
        public JButton getBtnSave() { return btnSave; }
        public JButton getBtnDelete() { return btnDelete; }
        public int getSelectedUserId() { return selectedUserId; }
        public String getUsername() { return txtUsername.getText().trim(); }
        public String getRole() { return cbRole.getSelectedItem().toString(); }
        public DefaultTableModel getUserTableModel() { return userTableModel; }
        
        // Inner class cho popup tạo user mới
        public class CreateUserPopup extends JDialog {
            private JTextField txtUsername;
            private JPasswordField txtPassword;
            private JComboBox<String> cbRole;
            private JButton btnCreate;
            private JButton btnCancel;
            private UserManagementPanel parentPanel;

            public CreateUserPopup(UserManagementPanel parent) {
                super(POSView.this, "Tạo tài khoản mới", true); // Modal dialog - block parent window
                this.parentPanel = parent;
                initPopupUI();
            }

            private void initPopupUI() {
                setSize(380, 280);
                setLocationRelativeTo(parentPanel);
                setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                setResizable(false);
                setLayout(new BorderLayout(8, 8));
                getContentPane().setBackground(new Color(245, 245, 245));

                // Form panel
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBackground(new Color(245, 245, 245));
                formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Username
                gbc.gridx = 0; gbc.gridy = 0;
                JLabel lblUsername = new JLabel("Username:");
                lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
                formPanel.add(lblUsername, gbc);
                gbc.gridx = 1;
                gbc.weightx = 1.0;
                txtUsername = new JTextField();
                txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                txtUsername.setPreferredSize(new Dimension(0, 36));
                txtUsername.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                formPanel.add(txtUsername, gbc);

                // Password
                gbc.gridx = 0; gbc.gridy = 1;
                gbc.weightx = 0;
                JLabel lblPassword = new JLabel("Password:");
                lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
                formPanel.add(lblPassword, gbc);
                gbc.gridx = 1;
                gbc.weightx = 1.0;
                txtPassword = new JPasswordField();
                txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                txtPassword.setPreferredSize(new Dimension(0, 36));
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                formPanel.add(txtPassword, gbc);

                // Role
                gbc.gridx = 0; gbc.gridy = 2;
                gbc.weightx = 0;
                JLabel lblRole = new JLabel("Role:");
                lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
                formPanel.add(lblRole, gbc);
                gbc.gridx = 1;
                gbc.weightx = 1.0;
                cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
                cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cbRole.setPreferredSize(new Dimension(0, 36));
                formPanel.add(cbRole, gbc);

                // Buttons panel
                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
                buttonsPanel.setBackground(new Color(245, 245, 245));
                buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

                btnCreate = new JButton("Tạo");
                btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 14));
                btnCreate.setPreferredSize(new Dimension(100, 40));
                btnCreate.setBackground(new Color(0, 120, 215));
                btnCreate.setForeground(Color.WHITE);
                btnCreate.setFocusPainted(false);
                btnCreate.setBorderPainted(false);

                btnCancel = new JButton("Hủy");
                btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                btnCancel.setPreferredSize(new Dimension(100, 40));
                btnCancel.setBackground(new Color(108, 117, 125));
                btnCancel.setForeground(Color.WHITE);
                btnCancel.setFocusPainted(false);
                btnCancel.setBorderPainted(false);

                buttonsPanel.add(btnCreate);
                buttonsPanel.add(btnCancel);

                add(formPanel, BorderLayout.CENTER);
                add(buttonsPanel, BorderLayout.SOUTH);

                // Event handlers
                btnCreate.addActionListener(e -> handleCreate());
                btnCancel.addActionListener(e -> dispose());
                
                // Enter key để tạo
                txtPassword.addActionListener(e -> handleCreate());
            }

            private void handleCreate() {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword());
                String role = cbRole.getSelectedItem().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username và Password không được để trống", 
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                shopbancancau.dao.UserDAO userDAO = new shopbancancau.dao.UserDAO();
                
                // Kiểm tra username đã tồn tại chưa
                if (userDAO.usernameExists(username)) {
                    JOptionPane.showMessageDialog(this, "Username đã tồn tại! Vui lòng chọn username khác.", 
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    userDAO.insertUser(username, password, role);
                    JOptionPane.showMessageDialog(this, "Thêm thành công", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    parentPanel.refreshUserList();
                    dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi tạo tài khoản: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}