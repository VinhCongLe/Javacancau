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

  
    private static POSView instance;

   
    private JMenuItem menuOrderHistory;
    private JMenuItem menuCreateUser;
    private JMenuItem menuQuanLySanPham;
    private JButton menuLogout; 
    private JLabel menuUserName; 

    
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel posPanel;
    private UserManagementPanel userManagementPanel;
    private String currentCard = "POS"; 

  
    private JComboBox<Product> cbProduct;
    private JTextField txtQuantity;
    private JButton btnAdd;
    private JButton btnRemove;

   
    private JTable table;
    private DefaultTableModel tableModel;

   
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

    
    public static POSView getInstance() {
        return instance;
    }

    private void initUI(String role) {
        setTitle("Bán hàng (POS) - Phụ kiện câu cá");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if ("USER_MANAGEMENT".equals(currentCard)) {
                    
                    showPOSPanel();
                } else {
                    
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
        
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
      
        posPanel = createPOSPanel();
        cardPanel.add(posPanel, "POS");
        
        
        userManagementPanel = new UserManagementPanel();
        cardPanel.add(userManagementPanel, "USER_MANAGEMENT");
        
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);

       
        JMenuBar menuBar = new JMenuBar();
        JMenu menuManage = new JMenu("Quản lý");

        menuQuanLySanPham = new JMenuItem("Quản lý sản phẩm");
        menuQuanLySanPham.addActionListener(e -> {
            ProductListView productView = new ProductListView(POSView.this);
            setVisible(false); 
            productView.setVisible(true);
        });
        menuManage.add(menuQuanLySanPham);

        menuCreateUser = new JMenuItem("Quản lý tài khoản");
        menuManage.add(menuCreateUser);

        menuOrderHistory = new JMenuItem("Thống kê");
        menuManage.add(menuOrderHistory);

        menuBar.add(menuManage);

        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        
       
        menuUserName = new JLabel();
        if (Session.currentUser != null) {
            String displayName = Session.currentUser.getUsername();
            String userRole = Session.currentUser.getRole();
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = "User";
            }
          
            menuUserName.setText("Tài khoản: " + displayName + 
                (userRole != null && !userRole.trim().isEmpty() ? " (" + userRole + ")" : ""));
            menuUserName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            menuUserName.setForeground(new Color(0, 120, 215));
            menuUserName.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        } else {
            menuUserName.setVisible(false);
        }
        userPanel.add(menuUserName);

       
        menuLogout = new JButton("Đăng xuất");
        menuLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        menuLogout.setPreferredSize(new Dimension(100, 30));
        menuLogout.setBackground(new Color(200, 35, 51)); 
        menuLogout.setForeground(Color.WHITE);
        menuLogout.setFocusPainted(false);
        menuLogout.setBorderPainted(false);
        menuLogout.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        userPanel.add(menuLogout);

        menuBar.add(Box.createHorizontalGlue()); 
        menuBar.add(userPanel);
        setJMenuBar(menuBar);
    }

    private JPanel createPOSPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

       
        JPanel panelLeft = new JPanel(new BorderLayout(10, 10));
        panelLeft.setBorder(BorderFactory.createTitledBorder("Chọn sản phẩm"));
        panelLeft.setPreferredSize(new Dimension(420, 0));

      
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.add(new JLabel("Sản phẩm"), BorderLayout.NORTH);
        cbProduct = new JComboBox<>();
        cbProduct.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbProduct.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        topPanel.add(cbProduct, BorderLayout.CENTER);

       
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

      
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(new JLabel("Số lượng"), gbc);

       
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        txtQuantity = new JTextField();
        txtQuantity.setHorizontalAlignment(JTextField.CENTER);
        txtQuantity.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtQuantity.setPreferredSize(new Dimension(160, 42));
        centerPanel.add(txtQuantity, gbc);

      
        txtQuantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnAdd = new JButton("+ THÊM");
        btnAdd.setPreferredSize(new Dimension(180, 48));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        centerPanel.add(btnAdd, gbc);

       
        gbc.gridy = 3;
        btnRemove = new JButton("- XÓA");
        btnRemove.setPreferredSize(new Dimension(180, 48));
        btnRemove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        centerPanel.add(btnRemove, gbc);

       
        panelLeft.add(topPanel, BorderLayout.NORTH);
        panelLeft.add(centerPanel, BorderLayout.CENTER);

        
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

       
        JPanel panelBottom = new JPanel(new BorderLayout(10, 10));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
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

  
    public class UserManagementPanel extends JPanel {
        private JTable userTable;
        private DefaultTableModel userTableModel;
        private JButton btnAdd;
        private JButton btnEdit;
        private JButton btnDelete;
        private JButton btnViewDetail;
        private JButton btnBack;
        private POSView parentView;

        public UserManagementPanel() {
            this.parentView = POSView.getInstance();
            initUserManagementUI();
        }

        private void initUserManagementUI() {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setBackground(new Color(245, 245, 245)); 

           
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            topPanel.setBackground(new Color(245, 245, 245));
            topPanel.setOpaque(false);
            btnBack = new JButton("← Quay lại");
            btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnBack.setPreferredSize(new Dimension(120, 35));
            btnBack.setBackground(new Color(0, 120, 215)); 
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
            
            JScrollPane scrollTable = new JScrollPane(userTable);
            scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách tài khoản"));
            add(scrollTable, BorderLayout.CENTER);

          
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            buttonsPanel.setBackground(new Color(245, 245, 245));
            buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            btnAdd = new JButton("Thêm mới");
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnAdd.setPreferredSize(new Dimension(100, 35));
            btnAdd.setBackground(new Color(0, 120, 215));
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setFocusPainted(false);
            btnAdd.setBorderPainted(false);

            btnEdit = new JButton("Sửa");
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnEdit.setPreferredSize(new Dimension(100, 35));
            btnEdit.setBackground(new Color(0, 120, 215));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);

            btnDelete = new JButton("Xóa");
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnDelete.setPreferredSize(new Dimension(100, 35));
            btnDelete.setBackground(new Color(200, 35, 51));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);

            btnViewDetail = new JButton("Xem chi tiết");
            btnViewDetail.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnViewDetail.setPreferredSize(new Dimension(120, 35));
            btnViewDetail.setBackground(new Color(0, 120, 215));
            btnViewDetail.setForeground(Color.WHITE);
            btnViewDetail.setFocusPainted(false);
            btnViewDetail.setBorderPainted(false);

            buttonsPanel.add(btnAdd);
            buttonsPanel.add(btnEdit);
            buttonsPanel.add(btnDelete);
            buttonsPanel.add(btnViewDetail);

            add(buttonsPanel, BorderLayout.SOUTH);

           
            refreshUserList();
        }

        public void refreshUserList() {
            userTableModel.setRowCount(0);
            shopbancancau.dao.UserDAO userDAO = new shopbancancau.dao.UserDAO();
            java.util.List<shopbancancau.model.User> users = userDAO.getAllUsers();
            
            for (shopbancancau.model.User user : users) {
                userTableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole()
                });
            }
        }

        public JButton getBtnAdd() { return btnAdd; }
        public JButton getBtnEdit() { return btnEdit; }
        public JButton getBtnDelete() { return btnDelete; }
        public JButton getBtnViewDetail() { return btnViewDetail; }
        public JTable getUserTable() { return userTable; }
        public int getSelectedUserId() {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow >= 0) {
                return (Integer) userTableModel.getValueAt(selectedRow, 0);
            }
            return -1;
        }
        public DefaultTableModel getUserTableModel() { return userTableModel; }
        
        
        public class CreateUserPopup extends JDialog {
            private JTextField txtUsername;
            private JPasswordField txtPassword;
            private JComboBox<String> cbRole;
            private JButton btnCreate;
            private JButton btnCancel;
            private UserManagementPanel parentPanel;

            public CreateUserPopup(UserManagementPanel parent) {
                super(POSView.this, "Tạo tài khoản mới", true); 
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

              
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBackground(new Color(245, 245, 245));
                formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;

             
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

           
                btnCreate.addActionListener(e -> handleCreate());
                btnCancel.addActionListener(e -> dispose());
                
             
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