package shopbancancau.controller;

import javax.swing.JOptionPane;

import shopbancancau.dao.UserDAO;
import shopbancancau.view.CreateUserView;

public class CreateUserController {

    private CreateUserView view;
    private UserDAO userDAO;

    public CreateUserController(CreateUserView view) {
        this.view = view;
        this.userDAO = new UserDAO();

        view.addCreateListener(e -> createUser());
    }

    private void createUser() {
        String username = view.getUsername();
        String password = view.getPassword();
        String role = view.getRole();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Không được để trống");
            return;
        }

        userDAO.createUser(username, password, role);
        JOptionPane.showMessageDialog(view, "Tạo tài khoản thành công");
        view.dispose();
    }
}
