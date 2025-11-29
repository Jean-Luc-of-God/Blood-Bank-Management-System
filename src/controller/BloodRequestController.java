package controller;

import dao.BloodRequestDAO;
import model.BloodRequest;
import view.BloodRequestPage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controller for handling Blood Requests.
 * ENFORCES BUSINESS RULE: Cannot request more blood than is currently in stock.
 */
public class BloodRequestController implements ActionListener {

    private final BloodRequestPage view;
    private final BloodRequestDAO dao;

    public BloodRequestController(BloodRequestPage view, BloodRequestDAO dao) {
        this.view = view;
        this.dao = dao;

        // Listen to buttons
        this.view.getSubmitButton().addActionListener(this);
        this.view.getFulfillButton().addActionListener(this);
        this.view.getDeleteButton().addActionListener(this);

        // Load data on startup
        loadRequests();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getSubmitButton()) {
            handleSubmit();
        } else if (e.getSource() == view.getFulfillButton()) {
            view.showMessage("Fulfillment logic coming soon!");
        } else if (e.getSource() == view.getDeleteButton()) {
            view.showMessage("Delete logic coming soon!");
        }
    }

    private void loadRequests() {
        try {
            List<BloodRequest> requests = dao.getAllRequests();
            view.refreshTable(requests);
        } catch (SQLException e) {
            view.showMessage("Error loading requests: " + e.getMessage());
        }
    }

    private void handleSubmit() {
        // 1. Get Form Data
        String type = view.getSelectedBloodType();
        String qtyStr = view.getQuantity();
        String dateStr = view.getRequestDate();

        // 2. Basic Validation
        if (type.equals("--Select--")) {
            view.showMessage("Please select a blood type.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                view.showMessage("Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showMessage("Invalid Quantity.");
            return;
        }

        LocalDate reqDate;
        try {
            reqDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            view.showMessage("Invalid Date. Format: YYYY-MM-DD");
            return;
        }

        // 3. --- CRITICAL BUSINESS RULE CHECK ---
        // "Blood requests cannot exceed available stock."
        try {
            int currentStock = dao.getTotalStockForType(type);

            if (quantity > currentStock) {
                // RULE FAILED: Stop the request
                view.showMessage("STOCK ERROR: Insufficient blood available!\n" +
                        "Requested: " + quantity + " units\n" +
                        "Available (" + type + "): " + currentStock + " units");
                return;
            }

            // 4. If Rule Passed, Save the Request
            // New requests default to 'fulfilled = false' (Pending)
            BloodRequest req = new BloodRequest(type, quantity, reqDate, false);

            if (dao.saveRequest(req)) {
                view.showMessage("Request Submitted Successfully!");
                view.clearForm();
                loadRequests();
            } else {
                view.showMessage("Failed to submit request.");
            }

        } catch (SQLException e) {
            view.showMessage("Database Error during stock check: " + e.getMessage());
        }
    }
}