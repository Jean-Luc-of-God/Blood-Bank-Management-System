package controller;

import dao.UserDAO;
import model.User;
import view.LoginPage;
import view.MainDashboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginController implements ActionListener {

    private final LoginPage view;
    private final UserDAO dao;

    public LoginController(LoginPage view, UserDAO dao) {
        this.view = view;
        this.dao = dao;

        this.view.getActionButton().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = view.getUsername();
        String pass = view.getPassword();
        String role = view.getSelectedRole();

        if (user.isEmpty() || pass.isEmpty()) {
            view.showMessage("Please fill in all fields.");
            return;
        }

        try {
            if (view.isLoginMode()) {
                // --- HANDLE LOGIN ---
                User validUser = dao.login(user, pass, role);
                if (validUser != null) {
                    view.dispose(); // Close Login

                    // Open Dashboard with User Role
                    MainDashboard dashboard = new MainDashboard(validUser);
                    dashboard.setVisible(true);
                } else {
                    view.showMessage("Invalid Credentials or Wrong Role!");
                }
            } else {
                // --- HANDLE REGISTER ---
                User newUser = new User(0, user, pass, role);
                if (dao.registerUser(newUser)) {
                    view.showMessage("Account Created Successfully! Please Login.");
                    view.clearFields();
                    // Toggle back to login automatically would be nice, or user clicks link
                } else {
                    view.showMessage("Registration Failed. Username might exist.");
                }
            }
        } catch (SQLException ex) {
            view.showMessage("Database Error: " + ex.getMessage());
        }
    }
}