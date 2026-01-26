package shopbancancau.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CreateUserView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnCreate;

    public CreateUserView() {
        setTitle("Tạo tài khoản nhân viên");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        add(new JLabel("Role:"));
        cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        add(cbRole);

        btnCreate = new JButton("Tạo tài khoản");
        add(new JLabel());
        add(btnCreate);
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public String getRole() {
        return cbRole.getSelectedItem().toString();
    }

    public void addCreateListener(ActionListener l) {
        btnCreate.addActionListener(l);
    }
}
