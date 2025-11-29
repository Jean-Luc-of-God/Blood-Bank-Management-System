package controller;

import dao.AlertDAO;
import model.Alert;
import view.AlertsPage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class AlertController implements ActionListener {

    private final AlertsPage view;
    private final AlertDAO dao;

    public AlertController(AlertsPage view, AlertDAO dao) {
        this.view = view;
        this.dao = dao;

        this.view.getRefreshButton().addActionListener(this);
        this.view.getDeleteButton().addActionListener(this);

        // AUTO-RUN SCAN ON STARTUP
        runSystemScan();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getRefreshButton()) {
            runSystemScan();
            view.showMessage("System scan complete. Table updated.");
        } else if (e.getSource() == view.getDeleteButton()) {
            handleDelete();
        }
    }

    private void runSystemScan() {
        try {
            // 1. Tell DAO to scan DB and generate new alerts
            dao.checkForNewAlerts();

            // 2. Fetch all alerts to display
            List<Alert> alerts = dao.getAllAlerts();

            // 3. Update View
            view.refreshTable(alerts);

        } catch (SQLException e) {
            view.showMessage("Error scanning for alerts: " + e.getMessage());
        }
    }

    private void handleDelete() {
        int selectedRow = view.getAlertsTable().getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Please select an alert to dismiss.");
            return;
        }

        int alertId = (int) view.getAlertsTable().getValueAt(selectedRow, 0);

        try {
            if (dao.deleteAlert(alertId)) {
                view.showMessage("Alert dismissed.");
                runSystemScan();
            } else {
                view.showMessage("Failed to dismiss alert.");
            }
        } catch (SQLException e) {
            view.showMessage("Database Error: " + e.getMessage());
        }
    }
}