package controller;

import dao.BloodUnitDAO;
import model.BloodUnit;
import view.BloodStockPage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controller for the Blood Stock Page.
 * Handles validation of dates and quantities.
 */
public class BloodUnitController implements ActionListener {

    private final BloodStockPage view;
    private final BloodUnitDAO dao;

    public BloodUnitController(BloodStockPage view, BloodUnitDAO dao) {
        this.view = view;
        this.dao = dao;

        // Listen to buttons
        this.view.getSaveButton().addActionListener(this);
        this.view.getUpdateButton().addActionListener(this);
        this.view.getDeleteButton().addActionListener(this);

        // Load initial data
        loadStockIntoTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getSaveButton()) {
            addBloodUnit();
        } else if (e.getSource() == view.getUpdateButton()) {
            view.showMessage("Update feature coming soon!");
        } else if (e.getSource() == view.getDeleteButton()) {
            view.showMessage("Delete feature coming soon!");
        }
    }

    private void loadStockIntoTable() {
        try {
            List<BloodUnit> units = dao.getAllBloodUnits();
            view.refreshTable(units);
        } catch (SQLException e) {
            view.showMessage("Error loading stock: " + e.getMessage());
        }
    }

    private void addBloodUnit() {
        // 1. Get Data
        String type = view.getSelectedBloodType();
        String qtyStr = view.getQuantity();
        String donDateStr = view.getDonationDate();
        String expDateStr = view.getExpiryDate();
        String donorIdStr = view.getDonorId();

        // 2. Validate
        if (type.equals("--Select--")) {
            view.showMessage("Please select a blood type.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                view.showMessage("Quantity must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showMessage("Invalid Quantity. Please enter a number.");
            return;
        }

        LocalDate donDate, expDate;
        try {
            donDate = LocalDate.parse(donDateStr); // Expects YYYY-MM-DD
            expDate = LocalDate.parse(expDateStr);

            if (expDate.isBefore(donDate)) {
                view.showMessage("Error: Expiry date cannot be before donation date.");
                return;
            }
        } catch (DateTimeParseException e) {
            view.showMessage("Invalid Date Format. Please use YYYY-MM-DD.");
            return;
        }

        int donorId = 0;
        if (!donorIdStr.isEmpty()) {
            try {
                donorId = Integer.parseInt(donorIdStr);
            } catch (NumberFormatException e) {
                view.showMessage("Donor ID must be a number.");
                return;
            }
        }

        // 3. Create Model
        BloodUnit unit = new BloodUnit(type, quantity, donDate, expDate, donorId);

        // 4. Save via DAO
        try {
            boolean success = dao.saveBloodUnit(unit);
            if (success) {
                view.showMessage("Blood Unit Added Successfully!");
                view.clearForm();
                loadStockIntoTable();
            } else {
                view.showMessage("Failed to add blood unit.");
            }
        } catch (SQLException e) {
            view.showMessage("Database Error: " + e.getMessage());
        }
    }
}