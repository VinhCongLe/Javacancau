package shopbancancau.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OrderDetailView extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public OrderDetailView(int orderId) {
        setTitle("Chi tiết hóa đơn #" + orderId);
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        String[] cols = {"Sản phẩm", "Số lượng", "Giá", "Thành tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        add(scroll, BorderLayout.CENTER);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
