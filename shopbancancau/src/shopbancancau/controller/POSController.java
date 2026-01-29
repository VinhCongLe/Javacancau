package shopbancancau.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import shopbancancau.dao.CustomerDAO;
import shopbancancau.dao.DBConnection;
import shopbancancau.dao.OrderDAO;
import shopbancancau.dao.ProductDAO;
import shopbancancau.dao.UserDAO;
import shopbancancau.model.Product;
import shopbancancau.util.Session;
import shopbancancau.view.CreateUserView;
import shopbancancau.view.OrderHistoryView;
import shopbancancau.view.POSView;
import shopbancancau.view.LoginView;

public class POSController {
    private POSView view;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;

    public POSController(POSView view) {
        this.view = view;
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.customerDAO = new CustomerDAO();

        // CHẮN SESSION
        if (Session.currentUser == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng đăng nhập!");
            view.dispose();
            return;
        }

        // PHÂN QUYỀN MENU – CHỈ ẨN "Quản lý tài khoản" nếu KHÔNG PHẢI ADMIN
        if (!"ADMIN".equalsIgnoreCase(Session.currentUser.getRole())) {
            view.getMenuCreateUser().setVisible(false);
            // KHÔNG ẨN menuOrderHistory → user thường VẪN THẤY "Lịch sử hóa đơn"
            view.getMenuOrderHistory().setVisible(true);
        } else {
            // ADMIN thấy cả 2
            view.getMenuCreateUser().setVisible(true);
            view.getMenuOrderHistory().setVisible(true);
        }

        loadProducts();

        // Gắn sự kiện
        view.addAddListener(new AddHandler());
        view.addPayListener(new PayHandler());
        view.getBtnRemove().addActionListener(e -> removeItem());
        view.getMenuOrderHistory().addActionListener(e -> openOrderHistory());
        view.getMenuCreateUser().addActionListener(e -> openUserManagement());

        view.getMenuLogout().addActionListener(e -> logout());
        
        // Gắn sự kiện cho UserManagementPanel
        setupUserManagementHandlers();
    }

    /* ================== ĐĂNG XUẤT ================== */
    private void logout() {
        int confirm = JOptionPane.showOptionDialog(
            view,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận Đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new Object[]{"Có", "Không"},
            "Có"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Session.currentUser = null;
            view.dispose();

            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);
        }
    }

    // Các phần còn lại giữ nguyên (loadProducts, AddHandler, removeItem, PayHandler, updateTotal, formatMoney, openOrderHistory, openCreateUser)
    private void loadProducts() {
        view.getCbProduct().removeAllItems();
        for (Product p : productDAO.getAllProducts()) {
            view.getCbProduct().addItem(p);
        }
    }

    class AddHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Product p = (Product) view.getCbProduct().getSelectedItem();
                if (p == null) return;
                int quantity = Integer.parseInt(view.getTxtQuantity().getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(view, "Số lượng phải > 0");
                    return;
                }
                if (quantity > p.getQuantity()) {
                    JOptionPane.showMessageDialog(
                        view,
                        "Không đủ hàng trong kho! Tồn kho: " + p.getQuantity()
                    );
                    return;
                }
                double total = p.getPrice() * quantity;
                DefaultTableModel model = view.getTableModel();
                model.addRow(new Object[]{
                    p.getProductName(),
                    quantity,
                    p.getPrice(),
                    total
                });
                updateTotal();
                view.getTxtQuantity().setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Số lượng không hợp lệ");
            }
        }
    }

    private void removeItem() {
        int row = view.getTable().getSelectedRow();
        if (row == -1) return;
        view.getTableModel().removeRow(row);
        updateTotal();
    }

    class PayHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTableModel model = view.getTableModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(view, "Chưa có sản phẩm để thanh toán");
                return;
            }
            Connection conn = null;
            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false);

                String totalText = view.getLblTotal().getText()
                        .replace(" VND", "")
                        .replace(".", "")
                        .trim();
                double totalAmount = Double.parseDouble(totalText);

                int userId = Session.currentUser.getUserId();
                String customerName = view.getCustomerName();
                String phone = view.getPhone();

                if (customerName.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Vui lòng nhập tên và SĐT khách");
                    return;
                }

                int customerId = customerDAO.getOrCreateCustomer(conn, customerName, phone);
                int orderId = orderDAO.createOrder(conn, userId, customerId, totalAmount);
                if (orderId == -1) {
                    throw new Exception("Không tạo được hóa đơn");
                }

                for (int i = 0; i < model.getRowCount(); i++) {
                    String productName = (String) model.getValueAt(i, 0);
                    int quantity = (int) model.getValueAt(i, 1);
                    double price = (double) model.getValueAt(i, 2);
                    int productId = productDAO.getProductIdByName(productName);
                    orderDAO.addOrderDetail(conn, orderId, productId, quantity, price);
                    orderDAO.updateQuantity(conn, productId, quantity);
                }

                conn.commit();
                JOptionPane.showMessageDialog(view, "Thanh toán thành công!");

                model.setRowCount(0);
                view.getLblTotal().setText("0 VND");
                view.clearCustomerInfo();
                loadProducts();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    if (conn != null) conn.rollback();
                } catch (Exception ignore) {}
                JOptionPane.showMessageDialog(view, "Thanh toán thất bại, dữ liệu đã được hoàn tác!");
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (Exception ignore) {}
            }
        }
    }

    private void updateTotal() {
        double sum = 0;
        DefaultTableModel model = view.getTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            sum += (double) model.getValueAt(i, 3);
        }
        view.getLblTotal().setText(formatMoney(sum));
    }

    private String formatMoney(double amount) {
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        return vn.format(amount) + " VND";
    }

    private void openOrderHistory() {
        OrderHistoryView v = new OrderHistoryView(view);
        new OrderHistoryController(v);
        view.setVisible(false); // Hide POSView when opening OrderHistoryView
        v.setVisible(true);
    }

    private void openCreateUser() {
        CreateUserView v = new CreateUserView();
        new CreateUserController(v);
        v.setVisible(true);
    }

    private void openUserManagement() {
        view.showUserManagementPanel();
    }

    private void setupUserManagementHandlers() {
        POSView.UserManagementPanel userPanel = view.getUserManagementPanel();
        
        // Nút Thêm mới - mở popup
        userPanel.getBtnAdd().addActionListener(e -> {
            POSView.UserManagementPanel.CreateUserPopup popup = 
                userPanel.new CreateUserPopup(userPanel);
            popup.setVisible(true);
        });
        
        // Nút Lưu - cập nhật username và role
        userPanel.getBtnSave().addActionListener(e -> handleUpdateUser(userPanel));
        
        // Nút Xóa
        userPanel.getBtnDelete().addActionListener(e -> handleDeleteUser(userPanel));
    }

    private void handleUpdateUser(POSView.UserManagementPanel userPanel) {
        int userId = userPanel.getSelectedUserId();
        if (userId <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn tài khoản cần cập nhật");
            return;
        }

        // Kiểm tra quyền ADMIN
        if (!"ADMIN".equalsIgnoreCase(Session.currentUser.getRole())) {
            JOptionPane.showMessageDialog(view, "Chỉ ADMIN mới có quyền cập nhật tài khoản");
            return;
        }

        String newUsername = userPanel.getUsername().trim();
        String newRole = userPanel.getRole();
        
        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Username không được để trống");
            return;
        }

        // Lấy username cũ từ table để so sánh
        String oldUsername = "";
        DefaultTableModel model = userPanel.getUserTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Integer) model.getValueAt(i, 0) == userId) {
                oldUsername = model.getValueAt(i, 1).toString();
                break;
            }
        }

        UserDAO userDAO = new UserDAO();
        
        // Kiểm tra username trùng (trừ chính nó)
        if (!newUsername.equals(oldUsername) && userDAO.usernameExistsExcept(newUsername, userId)) {
            JOptionPane.showMessageDialog(view, "Username đã tồn tại! Vui lòng chọn username khác.");
            return;
        }

        // Kiểm tra nếu là tài khoản đang đăng nhập thì không cho đổi role
        boolean isCurrentUser = Session.currentUser != null && 
                                oldUsername.equals(Session.currentUser.getUsername());
        if (isCurrentUser) {
            // Nếu là current user, giữ nguyên role cũ
            newRole = Session.currentUser.getRole();
        }

        String confirmMessage = "Bạn có chắc chắn muốn cập nhật tài khoản này?";
        if (!newUsername.equals(oldUsername)) {
            confirmMessage += "\nUsername: \"" + oldUsername + "\" → \"" + newUsername + "\"";
        }
        if (!isCurrentUser && !newRole.equals(getRoleFromTable(model, userId))) {
            confirmMessage += "\nRole: \"" + getRoleFromTable(model, userId) + "\" → \"" + newRole + "\"";
        }

        int confirm = JOptionPane.showOptionDialog(
            view,
            confirmMessage,
            "Xác nhận cập nhật",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new Object[]{"Có", "Không"},
            "Có"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.updateUser(userId, newUsername, newRole);
                
                // Nếu sửa username của tài khoản đang đăng nhập, cập nhật Session
                if (isCurrentUser && !newUsername.equals(oldUsername)) {
                    Session.currentUser.setUsername(newUsername);
                }
                
                JOptionPane.showMessageDialog(view, "Cập nhật tài khoản thành công");
                userPanel.refreshUserList();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật tài khoản: " + e.getMessage());
            }
        }
    }
    
    private String getRoleFromTable(DefaultTableModel model, int userId) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Integer) model.getValueAt(i, 0) == userId) {
                return model.getValueAt(i, 2).toString();
            }
        }
        return "";
    }

    private void handleDeleteUser(POSView.UserManagementPanel userPanel) {
        int userId = userPanel.getSelectedUserId();
        if (userId <= 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn tài khoản cần xóa");
            return;
        }

        String username = userPanel.getUsername();
        
        // Kiểm tra không cho phép xóa chính tài khoản đang đăng nhập
        if (Session.currentUser != null && username.equals(Session.currentUser.getUsername())) {
            JOptionPane.showMessageDialog(view, "Không thể xóa chính tài khoản đang đăng nhập");
            return;
        }

        int confirm = JOptionPane.showOptionDialog(
            view,
            "Bạn có chắc chắn muốn xóa tài khoản \"" + username + "\"?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new Object[]{"Có", "Không"},
            "Không"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO userDAO = new UserDAO();
            try {
                userDAO.deleteUser(userId);
                JOptionPane.showMessageDialog(view, "Xóa tài khoản thành công");
                userPanel.refreshUserList();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa tài khoản: " + e.getMessage());
            }
        }
    }
}