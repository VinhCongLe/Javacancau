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

public class POSController {

    private POSView view;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    

    public POSController(POSView view) { // contructor
        this.view = view;
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.customerDAO = new CustomerDAO();


        // ❗ CHẮN SESSION
        if (Session.currentUser == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng đăng nhập!");
            view.dispose();
            return;
        }
        
     // PHÂN QUYỀN MENU
        if (!Session.currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            view.getMenuCreateUser().setVisible(false);
        }


        loadProducts();

        // Gắn sự kiện
        view.addAddListener(new AddHandler());
        view.addPayListener(new PayHandler());
        view.getBtnRemove().addActionListener(e -> removeItem());
        view.getMenuOrderHistory().addActionListener(e -> openOrderHistory());
        view.getMenuCreateUser().addActionListener(e -> openCreateUser());
    }

    /* ================== LOAD SẢN PHẨM ================== */
    private void loadProducts() {
        view.getCbProduct().removeAllItems();
        for (Product p : productDAO.getAllProducts()) {
            view.getCbProduct().addItem(p);
        }
    }
    
    

    /* ================== THÊM VÀO HÓA ĐƠN ================== */
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

    /* ================== XÓA DÒNG ================== */
    private void removeItem() {
        int row = view.getTable().getSelectedRow();
        if (row == -1) return;

        view.getTableModel().removeRow(row);
        updateTotal();
    }

    /* ================== THANH TOÁN – TRANSACTION ================== */
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
                // ===== BEGIN =====
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false);

                // Lấy tổng tiền
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



                // 1️⃣ orders
                int orderId = orderDAO.createOrder(conn, userId, customerId, totalAmount);
                if (orderId == -1) {
                    throw new Exception("Không tạo được hóa đơn");
                }

                // 2️⃣ order_details + trừ kho
                for (int i = 0; i < model.getRowCount(); i++) {
                    String productName = (String) model.getValueAt(i, 0);
                    int quantity = (int) model.getValueAt(i, 1);
                    double price = (double) model.getValueAt(i, 2);

                    int productId = productDAO.getProductIdByName(productName);

                    orderDAO.addOrderDetail(conn, orderId, productId, quantity, price);
                    orderDAO.updateQuantity(conn, productId, quantity);
                }

                // ===== COMMIT =====
                conn.commit();

                JOptionPane.showMessageDialog(
                	    view,
                	    "Thanh toán thành công!"
                	);
                // Reset UI
                model.setRowCount(0);
                view.getLblTotal().setText("0 VND");
                view.clearCustomerInfo();
                loadProducts();

            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    if (conn != null) conn.rollback();
                } catch (Exception ignore) {}

                JOptionPane.showMessageDialog(
                    view,
                    "Thanh toán thất bại, dữ liệu đã được hoàn tác!"
                );

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

    /* ================== TỔNG TIỀN ================== */
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

    /* ================== MENU ================== */
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
