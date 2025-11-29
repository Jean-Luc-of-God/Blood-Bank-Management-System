package controller;

import dao.BloodUnitDAO; // Needed to add stock!
import dao.DonorDAO;
import model.BloodUnit;
import model.Donor;
import view.DonorRegistrationPage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

public class DonorController implements ActionListener {
    private final DonorRegistrationPage view;
    private final DonorDAO dao;
    private final BloodUnitDAO bloodDAO; // New DAO instance

    public DonorController(DonorRegistrationPage view, DonorDAO dao) {
        this.view = view;
        this.dao = dao;
        this.bloodDAO = new BloodUnitDAO(); // Initialize it

        view.getSaveButton().addActionListener(this);
        view.getUpdateButton().addActionListener(this);
        view.getDeleteButton().addActionListener(this);
        view.getDonateButton().addActionListener(this); // Listen to new button

        view.getDonorTable().getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) populateForm();
        });

        loadData();
    }

    private void loadData() {
        try { view.refreshTable(dao.getAllDonors()); }
        catch (SQLException e) { view.showMessage("Error: " + e.getMessage()); }
    }

    private void populateForm() {
        int row = view.getDonorTable().getSelectedRow();
        if (row != -1) {
            view.getNameField().setText(view.getDonorTable().getValueAt(row, 1).toString());
            view.getContactField().setText(view.getDonorTable().getValueAt(row, 2).toString());
            view.getBloodTypeComboBox().setSelectedItem(view.getDonorTable().getValueAt(row, 3).toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getSaveButton()) save();
        else if (e.getSource() == view.getUpdateButton()) update();
        else if (e.getSource() == view.getDeleteButton()) delete();
        else if (e.getSource() == view.getDonateButton()) recordDonation();
    }

    // --- NEW: RECORD DONATION ---
    private void recordDonation() {
        int row = view.getDonorTable().getSelectedRow();
        if (row == -1) {
            view.showMessage("Please select a donor from the table first.");
            return;
        }

        // Get Donor Details from Table
        int donorId = (int) view.getDonorTable().getValueAt(row, 0);
        String name = (String) view.getDonorTable().getValueAt(row, 1);
        String bloodType = (String) view.getDonorTable().getValueAt(row, 3);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Record a donation of 1 Unit (" + bloodType + ") from " + name + "?",
                "Confirm Donation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Auto-create a Blood Unit
            // Default: 1 unit, Today's date, Expires in 35 days
            BloodUnit unit = new BloodUnit(
                    bloodType,
                    1,
                    LocalDate.now(),
                    LocalDate.now().plusDays(35),
                    donorId
            );

            try {
                if (bloodDAO.saveBloodUnit(unit)) {
                    view.showMessage("Success! Donation added to Blood Stock.");
                } else {
                    view.showMessage("Failed to record donation.");
                }
            } catch (SQLException ex) {
                view.showMessage("Database Error: " + ex.getMessage());
            }
        }
    }

    private boolean validateInput(String name, String contact, String type) {
        if (name == null || name.trim().isEmpty()) { view.showMessage("Name cannot be empty."); return false; }
        if (contact == null || contact.trim().isEmpty()) { view.showMessage("Contact cannot be empty."); return false; }
        if (type.equals("--Select--")) { view.showMessage("Select a Blood Type."); return false; }
        if (!contact.matches("\\d{10}")) { view.showMessage("Contact must be 10 digits."); return false; }
        return true;
    }

    private void save() {
        String name = view.getDonorName();
        String contact = view.getDonorContact();
        String type = view.getSelectedBloodType();
        if (!validateInput(name, contact, type)) return;

        Donor d = new Donor(name, contact, type, LocalDate.now());
        try {
            if(dao.saveDonor(d)) { view.showMessage("Saved!"); view.clearForm(); loadData(); }
        } catch(SQLException ex) { view.showMessage("Error: " + ex.getMessage()); }
    }

    private void update() {
        int row = view.getDonorTable().getSelectedRow();
        if(row == -1) { view.showMessage("Select a donor."); return; }
        int id = (int) view.getDonorTable().getValueAt(row, 0);
        String name = view.getDonorName();
        String contact = view.getDonorContact();
        String type = view.getSelectedBloodType();

        LocalDate regDate = (LocalDate) view.getDonorTable().getValueAt(row, 4); // Keep original date

        if (!validateInput(name, contact, type)) return;

        Donor d = new Donor(id, name, contact, type, regDate);
        try {
            if(dao.updateDonor(d)) { view.showMessage("Updated!"); view.clearForm(); loadData(); }
        } catch(SQLException ex) { view.showMessage("Error: " + ex.getMessage()); }
    }

    private void delete() {
        int row = view.getDonorTable().getSelectedRow();
        if(row == -1) { view.showMessage("Select a donor."); return; }
        int id = (int) view.getDonorTable().getValueAt(row, 0);

        if(JOptionPane.showConfirmDialog(view, "Delete this donor?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if(dao.deleteDonor(id)) { view.showMessage("Deleted!"); view.clearForm(); loadData(); }
            } catch(SQLException ex) { view.showMessage("Error: " + ex.getMessage()); }
        }
    }
}