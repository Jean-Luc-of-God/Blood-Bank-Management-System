package app;

import view.MainDashboard;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Create the Main Dashboard (The Menu with 4 buttons)
                MainDashboard dashboard = new MainDashboard();

                // 2. Center it on screen
                dashboard.setLocationRelativeTo(null);

                // 3. Show it!
                dashboard.setVisible(true);
            }
        });
    }
}