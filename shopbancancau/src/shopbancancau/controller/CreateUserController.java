package shopbancancau.controller;

import javax.swing.JOptionPane;

import shopbancancau.dao.UserDAO;
import shopbancancau.view.CreateUserView;
import shopbancancau.view.UserListView;

public class CreateUserController {

    private CreateUserView view;
    private UserListView parentView;
    private UserDAO userDAO;

    public CreateUserController(CreateUserView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        view.addCreateListener(e -> createUser());
    }

    public CreateUserController(CreateUserView view, UserListView parent) {
        this.view = view;
        this.parentView = parent;
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
        
        // Refresh parent view nếu có
        if (parentView != null) {
            parentView.refreshUsers();
        }
        
        view.dispose();
    }
}
