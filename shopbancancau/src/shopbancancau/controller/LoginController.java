package shopbancancau.controller;

import javax.swing.JOptionPane;
import shopbancancau.util.Session;
import shopbancancau.dao.UserDAO;
import shopbancancau.model.User;
import shopbancancau.view.LoginView;
import shopbancancau.view.POSView;

public class LoginController {
    private LoginView view;
    private UserDAO userDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();

        
        view.addLoginListener(e -> performLogin());

       
        view.getTxtUsername().addActionListener(e -> view.getTxtPassword().requestFocusInWindow());

        
        view.getTxtPassword().addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = view.getUsername().trim();
        String password = view.getPassword();  

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(view, "Sai tài khoản hoặc mật khẩu");
            view.getTxtPassword().setText("");  
            view.getTxtPassword().requestFocusInWindow();  
            return;
        }

        
        Session.currentUser = user;

       
        POSView posView = new POSView(user.getRole());  
        new POSController(posView);
        posView.setVisible(true);

       
        view.dispose();
    }
}