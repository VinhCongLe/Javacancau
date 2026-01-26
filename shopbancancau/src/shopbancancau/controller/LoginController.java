package shopbancancau.controller;

import javax.swing.JOptionPane;

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

        view.addLoginListener(e -> login());
    }

    private void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        User user = userDAO.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(view, "Sai tài khoản hoặc mật khẩu");
            return;
        }

        //  ĐÚNG CHỖ PHÂN QUYỀN
        POSView posView = new POSView(user.getRole());
        new POSController(posView);
        posView.setVisible(true);
        view.dispose();
    }
}
