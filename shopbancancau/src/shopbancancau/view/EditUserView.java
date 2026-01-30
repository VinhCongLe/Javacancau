package shopbancancau.view;

import shopbancancau.dao.UserDAO;
import shopbancancau.model.User;

import javax.swing.*;
import java.awt.*;

public class EditUserView extends JFrame {

    private UserDAO userDAO;
    private UserListView parentView;
    private JTextField txtUsername, txtFullname, txtPhone;
    private JComboBox<String> cbRole;
    private JPasswordField txtPassword;
    private int userId;

    public EditUserView(int userId) {
        this.userDAO = new UserDAO();
        this.userId = userId;
        initComponents();
        loadUserData();
    }

    public EditUserView(int userId, UserListView parent) {
        this.userDAO = new UserDAO();
        this.userId = userId;
        this.parentView = parent;
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        setTitle("Sửa người dùng");
        setSize(550, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Title
        JLabel lblTitle = new JLabel("Sửa thông tin người dùng", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Thông tin người dùng"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Username
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblUsername, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblPassword = new JLabel("Mật khẩu mới:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblPassword, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtPassword, gbc);

        // Role
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblRole = new JLabel("Vai trò:");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblRole, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbRole, gbc);

        // Fullname
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblFullname = new JLabel("Họ tên:");
        lblFullname.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblFullname, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        txtFullname = new JTextField(20);
        txtFullname.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtFullname, gbc);

        // Phone
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblPhone, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        txtPhone = new JTextField(20);
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtPhone, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Nút Lưu và Đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnSave = new JButton("Lưu");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setPreferredSize(new Dimension(120, 40));
        btnSave.setBackground(new Color(0, 120, 215));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.addActionListener(e -> saveUser());
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadUserData() {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng với ID: " + userId, 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Điền dữ liệu vào form
        txtUsername.setText(user.getUsername() != null ? user.getUsername() : "");
        txtFullname.setText(user.getFullname() != null ? user.getFullname() : "");
        txtPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        cbRole.setSelectedItem(user.getRole() != null ? user.getRole() : "USER");
        // Không điền password vì đây là mật khẩu mới
    }

    private void saveUser() {
        try {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String role = cbRole.getSelectedItem().toString();
            String fullname = txtFullname.getText().trim();
            String phone = txtPhone.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra username trùng (trừ chính nó)
            User oldUser = userDAO.getUserById(userId);
            if (oldUser == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String oldUsername = oldUser.getUsername();
            if (!username.equals(oldUsername) && userDAO.usernameExistsExcept(username, userId)) {
                JOptionPane.showMessageDialog(this, "Username đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Nếu không nhập password mới, giữ nguyên password cũ
            if (password.isEmpty()) {
                password = oldUser.getPassword();
            }

            userDAO.updateUser(userId, username, password, fullname, phone, role);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh parent view nếu có
            if (parentView != null) {
                parentView.refreshUsers();
            }
            
            dispose(); // Đóng form sau khi lưu thành công
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
