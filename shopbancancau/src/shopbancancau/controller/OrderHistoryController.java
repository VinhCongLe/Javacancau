package shopbancancau.controller;

import shopbancancau.dao.OrderDAO;
import shopbancancau.view.OrderHistoryView;
import shopbancancau.view.OrderDetailView;
import shopbancancau.util.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;

public class OrderHistoryController {

    private OrderHistoryView view;
    private OrderDAO orderDAO;
    private NumberFormat moneyFormat;

    public OrderHistoryController(OrderHistoryView view) {
        this.view = view;
        this.orderDAO = new OrderDAO();
        this.moneyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        loadOrders();

        view.addDetailListener(e -> showDetail());
        view.addFilterListener(e -> filterByDate());
    }

    // ===== LOAD ĐƠN HÀNG (PHÂN QUYỀN) =====
    private void loadOrders() {
        try {
            ResultSet rs;
            
            // Kiểm tra role: ADMIN xem tất cả, USER chỉ xem đơn của mình
            if (Session.currentUser != null && "ADMIN".equalsIgnoreCase(Session.currentUser.getRole())) {
                // ADMIN: hiển thị tất cả đơn hàng
                rs = orderDAO.getAllOrders();
            } else {
                // USER: chỉ hiển thị đơn hàng của mình
                int userId = Session.currentUser != null ? Session.currentUser.getUserId() : 0;
                rs = orderDAO.getOrdersByUserId(userId);
            }
            
            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            double totalSum = 0;

            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                totalSum += total;

                // Lấy tên và SĐT khách hàng (có thể null nếu LEFT JOIN không match)
                String customerName = rs.getString("customer_name");
                String customerPhone = rs.getString("customer_phone");
                if (customerName == null) customerName = "";
                if (customerPhone == null) customerPhone = "";

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        customerName,
                        customerPhone,
                        moneyFormat.format(total) + " đ"
                });
            }

            view.setTotalText("Tổng doanh thu: " + moneyFormat.format(totalSum) + " đ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== LỌC THEO DATE PICKER =====
    private void filterByDate() {
        try {
            java.util.Date fromUtil = view.getFromDate();
            java.util.Date toUtil = view.getToDate();

            // Kiểm tra đã chọn đầy đủ ngày chưa
            if (fromUtil == null || toUtil == null) {
                int choice = JOptionPane.showConfirmDialog(view,
                        "Bạn chưa chọn đầy đủ ngày. Hiển thị tất cả đơn hàng?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    loadOrders(); // Hiển thị tất cả nếu đồng ý
                }
                return;
            }

            // Kiểm tra từ ngày <= đến ngày
            if (fromUtil.after(toUtil)) {
                JOptionPane.showMessageDialog(view,
                        "Từ ngày phải nhỏ hơn hoặc bằng đến ngày",
                        "Lỗi",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Chuyển đổi java.util.Date sang java.sql.Date
            Date fromDate = new Date(fromUtil.getTime());
            
            // Đặt toDate đến cuối ngày (23:59:59.999)
            Calendar cal = Calendar.getInstance();
            cal.setTime(toUtil);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date toDate = new Date(cal.getTimeInMillis());

            ResultSet rs;
            
            // Kiểm tra role: ADMIN xem tất cả, USER chỉ xem đơn của mình
            if (Session.currentUser != null && "ADMIN".equalsIgnoreCase(Session.currentUser.getRole())) {
                // ADMIN: lọc tất cả đơn hàng theo ngày
                rs = orderDAO.getOrdersByDate(fromDate, toDate);
            } else {
                // USER: chỉ lọc đơn hàng của mình theo ngày
                int userId = Session.currentUser != null ? Session.currentUser.getUserId() : 0;
                rs = orderDAO.getOrdersByUserIdAndDate(userId, fromDate, toDate);
            }

            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            double totalSum = 0;

            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                totalSum += total;

                // Lấy tên và SĐT khách hàng (có thể null nếu LEFT JOIN không match)
                String customerName = rs.getString("customer_name");
                String customerPhone = rs.getString("customer_phone");
                if (customerName == null) customerName = "";
                if (customerPhone == null) customerPhone = "";

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        customerName,
                        customerPhone,
                        moneyFormat.format(total) + " đ"
                });
            }

            view.setTotalText("Tổng doanh thu: " + moneyFormat.format(totalSum) + " đ");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi lọc đơn hàng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== XEM CHI TIẾT =====
    private void showDetail() {
        int row = view.getTable().getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn một hóa đơn");
            return;
        }

        int orderId = (int) view.getTableModel().getValueAt(row, 0);

        OrderDetailView detailView = new OrderDetailView(orderId);
        new OrderDetailController(detailView, orderId);
        detailView.setVisible(true);
    }
}
