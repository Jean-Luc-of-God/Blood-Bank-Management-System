package app;

import controller.LoginController;
import dao.UserDAO;
import view.LoginPage;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Create Login MVC
                UserDAO dao = new UserDAO();
                LoginPage view = new LoginPage();
                new LoginController(view, dao);

                // 2. Show Login Screen
                view.setVisible(true);
            }
        });
    }
}