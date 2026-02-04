package shopbancancau.view;

import shopbancancau.dao.CustomerDAO;
import shopbancancau.dao.CustomerDAO.CustomerInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerListView extends JFrame {

    private CustomerDAO customerDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerListView() {
        this.customerDAO = new CustomerDAO();
        initComponents();
        loadCustomers();
    }

    private void initComponents() {
        setTitle("Danh sách khách hàng");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

       
        JLabel lblTitle = new JLabel("Danh sách khách hàng", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

       
        String[] columns = {"ID", "Tên khách hàng", "Số điện thoại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));
        add(scrollPane, BorderLayout.CENTER);

     
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton btnAddCustomer = new JButton("Thêm khách hàng");
        btnAddCustomer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddCustomer.setPreferredSize(new Dimension(150, 35));
        btnAddCustomer.setBackground(new Color(0, 120, 215));
        btnAddCustomer.setForeground(Color.WHITE);
        btnAddCustomer.setFocusPainted(false);
        btnAddCustomer.setBorderPainted(false);
        btnAddCustomer.addActionListener(e -> {
            AddCustomerView addView = new AddCustomerView();
            addView.setVisible(true);
        });
        buttonPanel.add(btnAddCustomer);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        java.util.List<CustomerInfo> customers = customerDAO.getAllCustomers();
        
        for (CustomerInfo customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName() != null ? customer.getName() : "",
                customer.getPhone() != null ? customer.getPhone() : ""
            });
        }
    }
}
