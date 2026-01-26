package shopbancancau.view;

import javax.swing.*;

public class MainMenuView extends JFrame {

    public MainMenuView(String username) {
        setTitle("Menu chính");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lbl = new JLabel("Xin chào " + username, JLabel.CENTER);
        lbl.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        add(lbl);
    }
}
