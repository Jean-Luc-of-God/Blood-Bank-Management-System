package controller;

import dao.BloodUnitDAO;
import model.BloodUnit;
import view.BloodStockPage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class BloodUnitController implements ActionListener {

    private final BloodStockPage view;
    private final BloodUnitDAO dao;

    public BloodUnitController(BloodStockPage view, BloodUnitDAO dao) {
        this.view = view;
        this.dao = dao;

        this.view.getSaveButton().addActionListener(this);
        this.view.getUpdateButton().addActionListener(this);
        this.view.getDeleteButton().addActionListener(this);

        this.view.getStockTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        loadStockIntoTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getSaveButton()) saveBloodUnit();
        else if (e.getSource() == view.getUpdateButton()) updateBloodUnit();
        else if (e.getSource() == view.getDeleteButton()) deleteBloodUnit();
    }

    private void loadStockIntoTable() {
        try { view.refreshTable(dao.getAllBloodUnits()); }
        catch (SQLException e) { view.showMessage("Error loading stock: " + e.getMessage()); }
    }

    private void populateFormFromSelection() {
        int row = view.getStockTable().getSelectedRow();
        if (row != -1) {
            view.getBloodTypeComboBox().setSelectedItem(view.getStockTable().getValueAt(row, 1));
            view.getQuantityField().setText(String.valueOf(view.getStockTable().getValueAt(row, 2)));

            // Fix: Parse LocalDate from Table back to Date object for Spinner
            LocalDate donDate = (LocalDate) view.getStockTable().getValueAt(row, 3);
            LocalDate expDate = (LocalDate) view.getStockTable().getValueAt(row, 4);
            view.setDonationDate(java.util.Date.from(donDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            view.setExpiryDate(java.util.Date.from(expDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            Object donorId = view.getStockTable().getValueAt(row, 5);
            view.getDonorIdField().setText((donorId != null && (int)donorId != 0) ? String.valueOf(donorId) : "");
        }
    }

    private void saveBloodUnit() {
        BloodUnit unit = validateAndCreate(0);
        if (unit != null) {
            try {
                if (dao.saveBloodUnit(unit)) {
                    view.showMessage("Blood Unit Added!");
                    view.clearForm();
                    loadStockIntoTable();
                }
            } catch (SQLException e) { view.showMessage("Database Error: " + e.getMessage()); }
        }
    }

    private void updateBloodUnit() {
        int row = view.getStockTable().getSelectedRow();
        if (row == -1) { view.showMessage("Please select a row to update."); return; }
        int id = (int) view.getStockTable().getValueAt(row, 0);

        BloodUnit unit = validateAndCreate(id);
        if (unit != null) {
            try {
                if (dao.updateBloodUnit(unit)) {
                    view.showMessage("Stock Updated Successfully!");
                    view.clearForm();
                    loadStockIntoTable();
                }
            } catch (SQLException e) { view.showMessage("Update Error: " + e.getMessage()); }
        }
    }

    private void deleteBloodUnit() {
        int row = view.getStockTable().getSelectedRow();
        if (row == -1) { view.showMessage("Please select a row to delete."); return; }

        int confirm = JOptionPane.showConfirmDialog(view, "Delete this unit?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) view.getStockTable().getValueAt(row, 0);
            try {
                if (dao.deleteBloodUnit(id)) {
                    view.showMessage("Deleted Successfully.");
                    view.clearForm();
                    loadStockIntoTable();
                }
            } catch (SQLException e) { view.showMessage("Delete Error: " + e.getMessage()); }
        }
    }

    private BloodUnit validateAndCreate(int id) {
        String type = view.getSelectedBloodType();
        String qtyStr = view.getQuantity();
        String donorIdStr = view.getDonorId();

        if (type.equals("--Select--")) { view.showMessage("Select blood type."); return null; }

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) { view.showMessage("Quantity must be positive."); return null; }

            // FIX: Get date directly from spinner, no string parsing needed!
            java.util.Date utilDon = view.getDonationDate();
            java.util.Date utilExp = view.getExpiryDate();

            LocalDate donDate = utilDon.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate expDate = utilExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (expDate.isBefore(donDate)) { view.showMessage("Expiry cannot be before Donation."); return null; }

            int donorId = 0;
            if (!donorIdStr.isEmpty()) donorId = Integer.parseInt(donorIdStr);

            return new BloodUnit(id == 0 ? 0 : id, type, qty, donDate, expDate, donorId);

        } catch (NumberFormatException e) {
            view.showMessage("Invalid Number."); return null;
        }
    }
}