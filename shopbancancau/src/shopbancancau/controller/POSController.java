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
        view.getMenuCreateUser().addActionListener(e -> openCreateUser());

        view.getMenuLogout().addActionListener(e -> logout());
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
        OrderHistoryView v = new OrderHistoryView();
        new OrderHistoryController(v);
        v.setVisible(true);
    }

    private void openCreateUser() {
        CreateUserView v = new CreateUserView();
        new CreateUserController(v);
        v.setVisible(true);
    }
}