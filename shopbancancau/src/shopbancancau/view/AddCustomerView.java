package shopbancancau.view;

import shopbancancau.dao.CustomerDAO;
import shopbancancau.dao.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class AddCustomerView extends JFrame {

    private CustomerDAO customerDAO;
    private JTextField txtName;
    private JTextField txtPhone;
    private JButton btnAdd;
    private JButton btnCancel;

    public AddCustomerView() {
        this.customerDAO = new CustomerDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Thêm khách hàng mới");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

      
        JLabel lblTitle = new JLabel("Thêm khách hàng mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

      
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Thông tin khách hàng"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

       
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblName = new JLabel("Tên khách hàng:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblName, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtName = new JTextField(20);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setPreferredSize(new Dimension(0, 32));
        formPanel.add(txtName, gbc);

       
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblPhone, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPhone = new JTextField(20);
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPhone.setPreferredSize(new Dimension(0, 32));
        formPanel.add(txtPhone, gbc);

      
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnAdd = new JButton("Thêm");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setPreferredSize(new Dimension(100, 35));
        btnAdd.setBackground(new Color(0, 120, 215));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.addActionListener(e -> addCustomer());

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnCancel);

       
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addCustomer() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();

       
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập đầy đủ tên và số điện thoại", 
                "Lỗi", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            
            Connection conn = DBConnection.getConnection();
            Integer customerId = customerDAO.getOrCreateCustomer(conn, name, phone);
            
            if (customerId != null) {
                JOptionPane.showMessageDialog(this, 
                    "Thêm khách hàng thành công", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể thêm khách hàng", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi thêm khách hàng: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
