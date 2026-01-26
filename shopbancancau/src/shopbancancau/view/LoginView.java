package shopbancancau.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginView() {
        setTitle("Đăng nhập hệ thống");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        btnLogin = new JButton("Đăng nhập");
        add(new JLabel());
        add(btnLogin);
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void addLoginListener(ActionListener l) {
        btnLogin.addActionListener(l);
    }
}
