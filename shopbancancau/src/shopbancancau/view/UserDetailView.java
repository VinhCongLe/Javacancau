package shopbancancau.view;

import shopbancancau.dao.UserDAO;
import shopbancancau.model.User;

import javax.swing.*;
import java.awt.*;

public class UserDetailView extends JFrame {

    private UserDAO userDAO;
    private JLabel lblUserId, lblUsername, lblRole, lblFullname, lblPhone;

    public UserDetailView(int userId) {
        this.userDAO = new UserDAO();
        initComponents(userId);
    }

    private void initComponents(int userId) {
        setTitle("Chi tiết người dùng");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

      
        User user = userDAO.getUserById(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng với ID: " + userId, 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

      
        JLabel lblTitle = new JLabel("Chi tiết người dùng", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

     
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin người dùng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

      
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblUserId = new JLabel(String.valueOf(user.getUserId()));
        lblUserId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lblUserId, gbc);

        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        infoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblUsername = new JLabel(user.getUsername() != null ? user.getUsername() : "");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(lblUsername, gbc);

      
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        infoPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblRole = new JLabel(user.getRole() != null ? user.getRole() : "");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRole.setForeground(new Color(220, 20, 60));
        infoPanel.add(lblRole, gbc);

     
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        infoPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblFullname = new JLabel(user.getFullname() != null && !user.getFullname().isEmpty() ? user.getFullname() : "Chưa có");
        lblFullname.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lblFullname, gbc);

     
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        infoPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        lblPhone = new JLabel(user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Chưa có");
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(lblPhone, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }
}
