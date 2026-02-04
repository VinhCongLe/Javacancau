package shopbancancau.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginView() {
        setLookAndFeel();
        initUI();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
    }

    private void initUI() {
        setTitle("Đăng nhập hệ thống");
        setSize(460, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    LoginView.this,
                    "Bạn có chắc chắn muốn thoát không?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0); 
                }
               
            }
        });
        
        setLayout(new GridBagLayout());

        ShadowPanel shadow = new ShadowPanel();
        shadow.setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(22);
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(380, 260));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

       
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(40, 40, 40));
        gbc.gridy = 0;
        gbc.insets = new Insets(18, 12, 16, 12);
        card.add(lblTitle, gbc);

        gbc.insets = new Insets(6, 24, 6, 24);

        
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(new Color(110, 110, 110));
        gbc.gridy = 1;
        card.add(lblUser, gbc);

        txtUsername = createInput();
        gbc.gridy = 2;
        card.add(txtUsername, gbc);

      
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPass.setForeground(new Color(110, 110, 110));
        gbc.gridy = 3;
        gbc.insets = new Insets(14, 24, 6, 24);
        card.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        styleInput(txtPassword);
        gbc.gridy = 4;
        gbc.insets = new Insets(6, 24, 6, 24);
        card.add(txtPassword, gbc);

      
        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 42));
        gbc.gridy = 5;
        gbc.insets = new Insets(18, 24, 18, 24);
        card.add(btnLogin, gbc);

        shadow.add(card);
        add(shadow);
    }

    
    private JTextField createInput() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    
    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
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

   
    static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }

    
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }

    
    static class ShadowPanel extends JPanel {
        public ShadowPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = 12, y = 12;
            int w = getWidth() - 24;
            int h = getHeight() - 24;
            g2.setColor(new Color(0, 0, 0, 22));
            g2.fillRoundRect(x, y, w, h, 26, 26);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}