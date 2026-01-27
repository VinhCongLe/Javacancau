package shopbancancau.controller;

import shopbancancau.dao.OrderDAO;
import shopbancancau.view.OrderHistoryView;
import shopbancancau.view.OrderDetailView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

    // ===== LOAD TẤT CẢ =====
    private void loadOrders() {
        try {
            ResultSet rs = orderDAO.getAllOrders();
            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            double totalSum = 0;

            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                totalSum += total;

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        moneyFormat.format(total) + " đ"
                });
            }

            view.setTotalText("Tổng doanh thu: " + moneyFormat.format(totalSum) + " đ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== LỌC THEO dd/MM/yyyy =====
    private void filterByDate() {
        try {
            String fromStr = view.getFromDate();
            String toStr = view.getToDate();

            if (fromStr.isEmpty() || toStr.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Vui lòng nhập đầy đủ từ ngày và đến ngày");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);

            java.util.Date fromUtil = sdf.parse(fromStr);
            java.util.Date toUtil = sdf.parse(toStr);

            Date fromDate = new Date(fromUtil.getTime());
            Date toDate = new Date(toUtil.getTime() + 24 * 60 * 60 * 1000 - 1);

            ResultSet rs = orderDAO.getOrdersByDate(fromDate, toDate);

            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            double totalSum = 0;

            while (rs.next()) {
                double total = rs.getDouble("total_amount");
                totalSum += total;

                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        moneyFormat.format(total) + " đ"
                });
            }

            view.setTotalText("Tổng doanh thu: " + moneyFormat.format(totalSum) + " đ");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Sai định dạng ngày. Vui lòng nhập theo dd/MM/yyyy");
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
