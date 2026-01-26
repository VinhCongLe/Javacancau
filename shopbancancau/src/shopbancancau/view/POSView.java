package shopbancancau.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import shopbancancau.model.Product;

public class POSView extends JFrame {

    /* ===== MENU ===== */
    private JMenuItem menuOrderHistory;
    private JMenuItem menuCreateUser;


    /* ===== KHU A: Chọn sản phẩm ===== */
    private JComboBox<Product> cbProduct;
    private JTextField txtQuantity;
    private JButton btnAdd;
    private JButton btnRemove;

    /* ===== KHU B: Hóa đơn ===== */
    private JTable table;
    private DefaultTableModel tableModel;

    /* ===== KHU C: Thanh toán ===== */
    private JTextField txtCustomerName;
    private JTextField txtPhone;
    private JLabel lblTotal;
    private JButton btnPay;

    /* ================= CONSTRUCTOR CÓ ROLE (DÙNG KHI LOGIN) ================= */
    public POSView(String role) {
        initUI();

        // ===== PHÂN QUYỀN =====
        if (!"ADMIN".equalsIgnoreCase(role)) {
            menuOrderHistory.setVisible(false);
        }
    }

    /* ================= CONSTRUCTOR KHÔNG ROLE (DÙNG TEST) ================= */
    public POSView() {
        initUI();
    }

    /* ================= INIT UI ================= */
    private void initUI() {
        setTitle("Bán hàng (POS)");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        /* ===== MENU ===== */
        JMenuBar menuBar = new JMenuBar();
        JMenu menuManage = new JMenu("Quản lý");
        menuOrderHistory = new JMenuItem("Lịch sử hóa đơn");
        menuCreateUser = new JMenuItem("Quản lý tài khoản");
        menuManage.add(menuCreateUser);

        menuManage.add(menuOrderHistory);
        menuBar.add(menuManage);
        setJMenuBar(menuBar);

        /* ================= KHU A ================= */
        JPanel panelLeft = new JPanel(new GridLayout(7, 1, 5, 5));
        panelLeft.setBorder(BorderFactory.createTitledBorder("Chọn sản phẩm"));

        cbProduct = new JComboBox<>();
        txtQuantity = new JTextField();
        btnAdd = new JButton("Thêm vào hóa đơn");
        btnRemove = new JButton("Xóa sản phẩm");

        panelLeft.add(new JLabel("Sản phẩm:"));
        panelLeft.add(cbProduct);
        panelLeft.add(new JLabel("Số lượng:"));
        panelLeft.add(txtQuantity);
        panelLeft.add(new JLabel());
        panelLeft.add(btnAdd);
        panelLeft.add(btnRemove);

        /* ================= KHU B ================= */
        String[] cols = {"Sản phẩm", "Số lượng", "Giá", "Thành tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Hóa đơn"));

        /* ================= KHU C ================= */
        JPanel panelBottom = new JPanel(new GridLayout(2, 4, 5, 5));
        panelBottom.setBorder(BorderFactory.createTitledBorder("Thanh toán"));

        txtCustomerName = new JTextField();
        txtPhone = new JTextField();
        lblTotal = new JLabel("0 VND");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(Color.RED);
        btnPay = new JButton("Thanh toán");

        panelBottom.add(new JLabel("Tên khách:"));
        panelBottom.add(txtCustomerName);
        panelBottom.add(new JLabel("SĐT:"));
        panelBottom.add(txtPhone);
        panelBottom.add(new JLabel("Tổng tiền:"));
        panelBottom.add(lblTotal);
        panelBottom.add(new JLabel());
        panelBottom.add(btnPay);

        /* ================= ADD TO FRAME ================= */
        add(panelLeft, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
    }

    /* ================= GETTER CHO CONTROLLER ================= */
    public JComboBox<Product> getCbProduct() {
        return cbProduct;
    }

    public JTextField getTxtQuantity() {
        return txtQuantity;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return table;
    }

    public JLabel getLblTotal() {
        return lblTotal;
    }

    public String getCustomerName() {
        return txtCustomerName.getText();
    }

    public String getPhone() {
        return txtPhone.getText();
    }

    public JButton getBtnRemove() {
        return btnRemove;
    }

    public JMenuItem getMenuOrderHistory() {
        return menuOrderHistory;
    }

    public void clearCustomerInfo() {
        txtCustomerName.setText("");
        txtPhone.setText("");
    }

    /* ================= GẮN SỰ KIỆN ================= */
    public void addAddListener(ActionListener l) {
        btnAdd.addActionListener(l);
    }

    public void addPayListener(ActionListener l) {
        btnPay.addActionListener(l);
    }
    public JMenuItem getMenuCreateUser() {
        return menuCreateUser;
    }

}
