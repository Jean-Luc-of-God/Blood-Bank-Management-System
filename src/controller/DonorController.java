package controller;

import dao.BloodUnitDAO;
import dao.DonorDAO;
import model.BloodUnit;
import model.Donor;
import model.User;
import view.DonorRegistrationPage;
import view.MainDashboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

public class DonorController implements ActionListener {
    private final DonorRegistrationPage view;
    private final DonorDAO dao;
    private final BloodUnitDAO bloodDAO;
    private final User currentUser; // Needed for Dashboard return

    public DonorController(DonorRegistrationPage view, DonorDAO dao, User user) {
        this.view = view;
        this.dao = dao;
        this.currentUser = user;
        this.bloodDAO = new BloodUnitDAO();

        view.getSaveButton().addActionListener(this);
        view.getUpdateButton().addActionListener(this);
        view.getDeleteButton().addActionListener(this);
        view.getDonateButton().addActionListener(this);

        // BACK BUTTON LOGIC
        view.getBackButton().addActionListener(e -> {
            view.dispose();
            new MainDashboard(currentUser).setVisible(true);
        });

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

            LocalDate ld = (LocalDate) view.getDonorTable().getValueAt(row, 4);
            java.util.Date date = java.util.Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
            view.setSelectedDate(date);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getSaveButton()) save();
        else if (e.getSource() == view.getUpdateButton()) update();
        else if (e.getSource() == view.getDeleteButton()) delete();
        else if (e.getSource() == view.getDonateButton()) recordDonation();
    }

    private void recordDonation() {
        int row = view.getDonorTable().getSelectedRow();
        if (row == -1) { view.showMessage("Select a donor first."); return; }

        int donorId = (int) view.getDonorTable().getValueAt(row, 0);
        String name = (String) view.getDonorTable().getValueAt(row, 1);
        String bloodType = (String) view.getDonorTable().getValueAt(row, 3);

        if (JOptionPane.showConfirmDialog(view, "Record 1 Unit (" + bloodType + ") from " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (bloodDAO.saveBloodUnit(new BloodUnit(bloodType, 1, LocalDate.now(), LocalDate.now().plusDays(35), donorId))) {
                    view.showMessage("Success! Donation added to Stock.");
                }
            } catch (SQLException ex) { view.showMessage("Database Error: " + ex.getMessage()); }
        }
    }

    private boolean validateInput(String name, String contact, String type) {
        if (name.isEmpty() || contact.isEmpty()) { view.showMessage("Fields cannot be empty."); return false; }
        if (type.equals("--Select--")) { view.showMessage("Select a Blood Type."); return false; }
        if (!contact.matches("\\d{10}")) { view.showMessage("Contact must be 10 digits."); return false; }
        return true;
    }

    private void save() {
        if (!validateInput(view.getDonorName(), view.getDonorContact(), view.getSelectedBloodType())) return;
        java.util.Date utilDate = view.getSelectedDate();
        LocalDate dateReg = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            if(dao.saveDonor(new Donor(view.getDonorName(), view.getDonorContact(), view.getSelectedBloodType(), dateReg))) {
                view.showMessage("Saved!"); view.clearForm(); loadData();
            }
        } catch(SQLException ex) { view.showMessage("Error: " + ex.getMessage()); }
    }

    private void update() {
        int row = view.getDonorTable().getSelectedRow();
        if(row == -1) { view.showMessage("Select a donor."); return; }
        int id = (int) view.getDonorTable().getValueAt(row, 0);
        if (!validateInput(view.getDonorName(), view.getDonorContact(), view.getSelectedBloodType())) return;

        java.util.Date utilDate = view.getSelectedDate();
        LocalDate dateReg = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            if(dao.updateDonor(new Donor(id, view.getDonorName(), view.getDonorContact(), view.getSelectedBloodType(), dateReg))) {
                view.showMessage("Updated!"); view.clearForm(); loadData();
            }
        } catch(SQLException ex) { view.showMessage("Error: " + ex.getMessage()); }
    }

    private void delete() {
        int row = view.getDonorTable().getSelectedRow();
        if(row == -1) { view.showMessage("Select a donor."); return; }
        int id = (int) view.getDonorTable().getValueAt(row, 0);
        if(JOptionPane.showConfirmDialog(view, "Delete this donor?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { if(dao.deleteDonor(id)) { view.showMessage("Deleted!"); view.clearForm(); loadData(); } }
            catch(SQLException ex) { view.showMessage("Error: " + ex.getMessage()); }
        }
    }
}