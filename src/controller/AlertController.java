package controller;

import dao.AlertDAO;
import model.User;
import view.AlertsPage;
import view.MainDashboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class AlertController implements ActionListener {
    private final AlertsPage view;
    private final AlertDAO dao;
    private final User currentUser;

    public AlertController(AlertsPage view, AlertDAO dao, User user) {
        this.view = view;
        this.dao = dao;
        this.currentUser = user;

        view.getRefreshButton().addActionListener(this);
        view.getDeleteButton().addActionListener(this);

        // BACK BUTTON LOGIC
        view.getBackButton().addActionListener(e -> {
            view.dispose();
            new MainDashboard(currentUser).setVisible(true);
        });

        scan();
    }

    @Override public void actionPerformed(ActionEvent e) {
        if(e.getSource() == view.getRefreshButton()) { scan(); view.showMessage("Scanned."); }
        else if(e.getSource() == view.getDeleteButton()) dismiss();
    }

    private void scan() {
        try { dao.checkForNewAlerts(); view.refreshTable(dao.getAllAlerts()); }
        catch (SQLException e) { view.showMessage(e.getMessage()); }
    }

    private void dismiss() {
        int r = view.getAlertsTable().getSelectedRow();
        if(r != -1) {
            try {
                if(dao.deleteAlert((int)view.getAlertsTable().getValueAt(r, 0))) { scan(); view.showMessage("Dismissed."); }
            } catch(SQLException e) { view.showMessage(e.getMessage()); }
        } else view.showMessage("Select an alert.");
    }
}