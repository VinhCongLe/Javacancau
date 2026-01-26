package shopbancancau.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import shopbancancau.dao.OrderDAO;
import shopbancancau.dao.ProductDAO;
import shopbancancau.model.Product;
import shopbancancau.view.CreateUserView;
import shopbancancau.view.OrderHistoryView;
import shopbancancau.view.POSView;


public class POSController {

    private POSView view;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;

    public POSController(POSView view) {
        this.view = view;
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();

        // Load sản phẩm từ DB vào combobox
        for (Product p : productDAO.getAllProducts()) {
            view.getCbProduct().addItem(p);
        }

        // Gắn sự kiện
        view.addAddListener(new AddHandler());
        view.addPayListener(new PayHandler());
        view.getBtnRemove().addActionListener(e -> removeItem());
        view.getMenuOrderHistory().addActionListener(e -> openOrderHistory());
        view.getMenuCreateUser().addActionListener(e -> openCreateUser());


    }

    /* ================== THÊM VÀO HÓA ĐƠN ================== */
    class AddHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Product p = (Product) view.getCbProduct().getSelectedItem();

                if (p == null) {
                    JOptionPane.showMessageDialog(view, "Chưa chọn sản phẩm");
                    return;
                }

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

                double price = p.getPrice();
                double total = price * quantity;

                DefaultTableModel model = view.getTableModel();
                model.addRow(new Object[]{
                        p.getProductName(),
                        quantity,
                        price,
                        total
                });

                // Cập nhật tổng tiền
                updateTotal();

                view.getTxtQuantity().setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Số lượng không hợp lệ");
            }
        }
    }

    /* ================== XÓA SẢN PHẨM KHỎI HÓA ĐƠN ================== */
    private void removeItem() {
        int row = view.getTable().getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần xóa");
            return;
        }

        DefaultTableModel model = view.getTableModel();
        model.removeRow(row);

        updateTotal();
    }

    /* ================== THANH TOÁN (DEMO) ================== */
    class PayHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTableModel model = view.getTableModel();

            // 1️⃣ Kiểm tra có sản phẩm chưa
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(view, "Chưa có sản phẩm để thanh toán");
                return;
            }

            try {
                // 2️⃣ Lấy tổng tiền
                String totalText = view.getLblTotal().getText()
                        .replace(" VND", "")
                        .replace(".", "")
                        .trim();

                double totalAmount = Double.parseDouble(totalText);

                // 3️⃣ Tạo hóa đơn
                int userId = 1;      // tạm
                int customerId = 1;  // tạm

                int orderId = orderDAO.createOrder(userId, customerId, totalAmount);

                if (orderId == -1) {
                    JOptionPane.showMessageDialog(view, "Tạo hóa đơn thất bại");
                    return;
                }

                // 4️⃣ Ghi chi tiết hóa đơn
                for (int i = 0; i < model.getRowCount(); i++) {
                    String productName = (String) model.getValueAt(i, 0);
                    int quantity = (int) model.getValueAt(i, 1);
                    double price = (double) model.getValueAt(i, 2);

                    int productId = productDAO.getProductIdByName(productName);

                    orderDAO.addOrderDetail(orderId, productId, quantity, price);
                    orderDAO.updateQuantity(productId, quantity);
                }

                // 5️⃣ Thông báo thành công
                JOptionPane.showMessageDialog(view, "Thanh toán thành công!");

                // 6️⃣ Reset UI
                model.setRowCount(0);
                view.getLblTotal().setText("0 VND");
                view.clearCustomerInfo();
                reloadProducts();

                System.out.println("Đã tạo hóa đơn ID = " + orderId);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Có lỗi xảy ra khi thanh toán");
            }
        }
    }



    /* ================== TÍNH TỔNG TIỀN ================== */
    private void updateTotal() {
        DefaultTableModel model = view.getTableModel();
        double sum = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            sum += (double) model.getValueAt(i, 3);
        }

        view.getLblTotal().setText(formatMoney(sum));

    }
    private String formatMoney(double amount) {
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        return vn.format(amount) + " VND";
    }
    
    /* ================== LOAD LẠI SẢN PHẨM SAU THANH TOÁN ================== */
    private void reloadProducts() {
        view.getCbProduct().removeAllItems();
        for (Product p : productDAO.getAllProducts()) {
            view.getCbProduct().addItem(p);
        }
    }
    
    private void openOrderHistory() {
        OrderHistoryView historyView = new OrderHistoryView();
        new OrderHistoryController(historyView);
        historyView.setVisible(true);
    }

    private void openCreateUser() {
        CreateUserView v = new CreateUserView();
        new CreateUserController(v);
        v.setVisible(true);
    }

    int userId = 1;      // admin / nhân viên đang đăng nhập
    int customerId = 1; // tạm
    
}
