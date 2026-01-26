package shopbancancau.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class OrderHistoryView extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnDetail;

    public OrderHistoryView() {
        setTitle("Lịch sử hóa đơn");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        String[] cols = {"Mã hóa đơn", "Ngày", "Tổng tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        btnDetail = new JButton("Xem chi tiết");

        add(scroll, BorderLayout.CENTER);
        add(btnDetail, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return table;
    }

    public void addDetailListener(ActionListener l) {
        btnDetail.addActionListener(l);
    }
}
