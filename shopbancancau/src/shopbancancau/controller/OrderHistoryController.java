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
        view.addDeleteListener(e -> deleteOrder());
        view.addFilterListener(e -> filterByDate());
        view.addRefreshListener(e -> loadOrders());
    }

    
    private void loadOrders() {
        try {
            ResultSet rs;
            
           
            if (Session.currentUser != null && "ADMIN".equalsIgnoreCase(Session.currentUser.getRole())) {
                
                rs = orderDAO.getAllOrders();
            } else {
                // user chỉ xem đơn hàng của mình
                int userId = Session.currentUser != null ? Session.currentUser.getUserId() : 0;
                rs = orderDAO.getOrdersByUserId(userId);
            }
            
            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            double totalSum = 0;

            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                totalSum += total;

                
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

    
    private void filterByDate() {
        try {
            java.util.Date fromUtil = view.getFromDate();
            java.util.Date toUtil = view.getToDate();

            
            if (fromUtil == null || toUtil == null) {
                int choice = JOptionPane.showConfirmDialog(view,
                        "Bạn chưa chọn đầy đủ ngày. Hiển thị tất cả đơn hàng?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    loadOrders(); 
                }
                return;
            }

           
            if (fromUtil.after(toUtil)) {
                JOptionPane.showMessageDialog(view,
                        "Từ ngày phải nhỏ hơn hoặc bằng đến ngày",
                        "Lỗi",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

           
            Date fromDate = new Date(fromUtil.getTime());
            
           
            Calendar cal = Calendar.getInstance();
            cal.setTime(toUtil);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date toDate = new Date(cal.getTimeInMillis());

            ResultSet rs;
            
            
            if (Session.currentUser != null && "ADMIN".equalsIgnoreCase(Session.currentUser.getRole())) {
                
                rs = orderDAO.getOrdersByDate(fromDate, toDate);
            } else {
               
                int userId = Session.currentUser != null ? Session.currentUser.getUserId() : 0;
                rs = orderDAO.getOrdersByUserIdAndDate(userId, fromDate, toDate);
            }

            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            double totalSum = 0;

            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                totalSum += total;

               
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

    
    private void deleteOrder() {
        int row = view.getTable().getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn một hóa đơn để xóa!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) view.getTableModel().getValueAt(row, 0);
        String orderDate = view.getTableModel().getValueAt(row, 1).toString();
        String customerName = view.getTableModel().getValueAt(row, 2).toString();
        String totalAmount = view.getTableModel().getValueAt(row, 4).toString();

        // Hiển thị dialog xác nhận
        String message = String.format(
            "Bạn có chắc chắn muốn xóa đơn hàng này?\n\n" +
            "Mã hóa đơn: %d\n" +
            "Ngày: %s\n" +
            "Khách hàng: %s\n" +
            "Tổng tiền: %s",
            orderId, orderDate, customerName, totalAmount
        );

        int confirm = JOptionPane.showConfirmDialog(
            view,
            message,
            "Xác nhận xóa đơn hàng",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = orderDAO.deleteOrder(orderId);
                
                if (success) {
                    JOptionPane.showMessageDialog(
                        view,
                        "Xóa đơn hàng thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    
                    loadOrders();
                } else {
                    JOptionPane.showMessageDialog(
                        view,
                        "Không thể xóa đơn hàng. Đơn hàng có thể không tồn tại.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    view,
                    "Lỗi khi xóa đơn hàng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
