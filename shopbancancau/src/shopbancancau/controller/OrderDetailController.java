package shopbancancau.controller;

import shopbancancau.dao.OrderDAO;
import shopbancancau.view.OrderDetailView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OrderDetailController {

    private OrderDetailView view;
    private OrderDAO orderDAO;
    private int orderId;
    private NumberFormat moneyFormat;

    public OrderDetailController(OrderDetailView view, int orderId) {
        this.view = view;
        this.orderDAO = new OrderDAO();
        this.orderId = orderId;
        this.moneyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        loadOrderDetails(orderId);

        // nút in hóa đơn
        view.addPrintListener(e -> printInvoice());
    }

    // ===== LOAD CHI TIẾT HÓA ĐƠN =====
    private void loadOrderDetails(int orderId) {
        try {
            ResultSet rs = orderDAO.getOrderDetails(orderId);
            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            while (rs.next()) {
                double price = rs.getDouble("price");
                double total = rs.getDouble("total");

                model.addRow(new Object[]{
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        moneyFormat.format(price) + " đ",
                        moneyFormat.format(total) + " đ"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== IN HÓA ĐƠN (KHÔNG DÙNG SESSION – AN TOÀN) =====
    private void printInvoice() {
        try {
            // ngày giờ hiện tại
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String now = LocalDateTime.now().format(dtf);

            // tên nhân viên (mặc định – đồ án OK)
            String staffName = "Nhân viên bán hàng";

            StringBuilder sb = new StringBuilder();

            sb.append("          HÓA ĐƠN BÁN HÀNG\n");
            sb.append("        SHOP BÁN CẦN CÂU\n");
            sb.append("----------------------------------------\n");
            sb.append("Mã hóa đơn: #").append(orderId).append("\n");
            sb.append("Ngày lập: ").append(now).append("\n");
            sb.append("Nhân viên: ").append(staffName).append("\n");
            sb.append("----------------------------------------\n");
            sb.append(String.format("%-18s %5s %10s\n", "Sản phẩm", "SL", "Thành tiền"));
            sb.append("----------------------------------------\n");

            DefaultTableModel model = view.getTableModel();
            double totalSum = 0;

            for (int i = 0; i < model.getRowCount(); i++) {
                String name = model.getValueAt(i, 0).toString();
                String qty = model.getValueAt(i, 1).toString();
                String totalStr = model.getValueAt(i, 3).toString();

                double total = Double.parseDouble(
                        totalStr.replace(".", "").replace(" đ", "")
                );
                totalSum += total;

                sb.append(String.format(
                        "%-18s %5s %10s\n",
                        name.length() > 18 ? name.substring(0, 18) : name,
                        qty,
                        totalStr
                ));
            }

            sb.append("----------------------------------------\n");
            sb.append("TỔNG CỘNG: ").append(moneyFormat.format(totalSum)).append(" đ\n");
            sb.append("\nCảm ơn quý khách!\n");

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            textArea.setEditable(false);

            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new java.awt.Dimension(420, 420));

            int option = JOptionPane.showConfirmDialog(
                    view,
                    scroll,
                    "In hóa đơn",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                textArea.print();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Không thể in hóa đơn");
        }
    }
}
