package shopbancancau.controller;

import shopbancancau.dao.OrderDAO;
import shopbancancau.view.OrderDetailView;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;

public class OrderDetailController {

    private OrderDetailView view;
    private OrderDAO orderDAO;

    public OrderDetailController(OrderDetailView view, int orderId) {
        this.view = view;
        this.orderDAO = new OrderDAO();

        loadOrderDetails(orderId);
    }

    private void loadOrderDetails(int orderId) {
        try {
            ResultSet rs = orderDAO.getOrderDetails(orderId);
            DefaultTableModel model = view.getTableModel();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
