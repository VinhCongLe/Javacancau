package shopbancancau.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class OrderHistoryView extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnDetail;
    private JButton btnFilter;

    private JTextField txtFromDate;
    private JTextField txtToDate;

    private JLabel lblTotal; // HIỂN THỊ TỔNG DOANH THU

    public OrderHistoryView() {
        setTitle("Lịch sử hóa đơn");
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== FILTER PANEL =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Từ ngày (dd/MM/yyyy):"));
        txtFromDate = new JTextField(10);
        filterPanel.add(txtFromDate);

        filterPanel.add(new JLabel("Đến ngày (dd/MM/yyyy):"));
        txtToDate = new JTextField(10);
        filterPanel.add(txtToDate);

        btnFilter = new JButton("Lọc");
        filterPanel.add(btnFilter);

        add(filterPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = {"Mã hóa đơn", "Ngày", "Tổng tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ===== BOTTOM PANEL =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        lblTotal = new JLabel("Tổng doanh thu: 0 đ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(lblTotal, BorderLayout.WEST);

        btnDetail = new JButton("Xem chi tiết");
        bottomPanel.add(btnDetail, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ===== GETTERS =====
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return table;
    }

    public void addDetailListener(ActionListener l) {
        btnDetail.addActionListener(l);
    }

    // ===== FILTER SUPPORT =====
    public String getFromDate() {
        return txtFromDate.getText().trim();
    }

    public String getToDate() {
        return txtToDate.getText().trim();
    }

    public void addFilterListener(ActionListener l) {
        btnFilter.addActionListener(l);
    }

    // ===== TOTAL LABEL =====
    public void setTotalText(String text) {
        lblTotal.setText(text);
    }
}
