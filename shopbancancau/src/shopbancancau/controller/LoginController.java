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

        // Gắn listener cho nút Đăng nhập (click chuột)
        view.addLoginListener(e -> performLogin());

        // Gắn Enter cho ô tên đăng nhập → focus sang mật khẩu
        view.getTxtUsername().addActionListener(e -> view.getTxtPassword().requestFocusInWindow());

        // Gắn Enter cho ô mật khẩu → thực hiện đăng nhập
        view.getTxtPassword().addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = view.getUsername().trim();
        String password = view.getPassword();  // getPassword() trả về char[], nhưng bạn đang dùng getPassword() kiểu String

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(view, "Sai tài khoản hoặc mật khẩu");
            view.getTxtPassword().setText("");  // Xóa mật khẩu sai cho an toàn
            view.getTxtPassword().requestFocusInWindow();  // Focus lại ô pass
            return;
        }

        // ✅ GÁN SESSION
        Session.currentUser = user;

        // Mở POSView
        POSView posView = new POSView(user.getRole());  // Truyền role để ẩn/hiện menu
        new POSController(posView);
        posView.setVisible(true);

        // Đóng LoginView
        view.dispose();
    }
}