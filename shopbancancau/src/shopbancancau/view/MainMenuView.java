package shopbancancau.view;

import javax.swing.*;
import java.awt.*;

public class MainMenuView extends JFrame {

    public MainMenuView(String username) {
        setTitle("Menu chính");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuManage = new JMenu("Quản lý");

        // Menu "Quản lý khách hàng"
        JMenuItem menuCustomer = new JMenuItem("Quản lý khách hàng");
        menuCustomer.addActionListener(e -> {
            CustomerListView customerView = new CustomerListView();
            customerView.setVisible(true);
        });
        menuManage.add(menuCustomer);

        // Menu "Quản lý nhân viên"
        JMenuItem menuUser = new JMenuItem("Quản lý nhân viên");
        menuUser.addActionListener(e -> {
            UserListView userView = new UserListView();
            userView.setVisible(true);
        });
        menuManage.add(menuUser);

        menuBar.add(menuManage);
        setJMenuBar(menuBar);

        // Label chào mừng (giữ nguyên)
        JLabel lbl = new JLabel("Xin chào " + username, JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        add(lbl);
    }
}
