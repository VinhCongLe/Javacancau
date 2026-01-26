package shopbancancau.controller;

import shopbancancau.dao.OrderDAO;
import shopbancancau.view.OrderHistoryView;
import shopbancancau.view.OrderHistoryView;
import shopbancancau.view.OrderDetailView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;

public class OrderHistoryController {

    private OrderHistoryView view;
    private OrderDAO orderDAO;

    public OrderHistoryController(OrderHistoryView view) {
        this.view = view;
        this.orderDAO = new OrderDAO();

        loadOrders();

        view.addDetailListener(e -> showDetail());
    }

    private void loadOrders() {
        try {
            ResultSet rs = orderDAO.getAllOrders();
            DefaultTableModel model = view.getTableModel();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("total_amount")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDetail() {
        int row = view.getTable().getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một hóa đơn");
            return;
        }

        int orderId = (int) view.getTableModel().getValueAt(row, 0);

        OrderDetailView detailView = new OrderDetailView(orderId);
        new OrderDetailController(detailView, orderId);
        detailView.setVisible(true);
    }

}
