package controller;

import dao.BloodUnitDAO;
import model.BloodUnit;
import model.User;
import view.BloodStockPage;
import view.MainDashboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

public class BloodUnitController implements ActionListener {

    private final BloodStockPage view;
    private final BloodUnitDAO dao;
    private final User currentUser;

    public BloodUnitController(BloodStockPage view, BloodUnitDAO dao, User user) {
        this.view = view;
        this.dao = dao;
        this.currentUser = user;

        this.view.getSaveButton().addActionListener(this);
        this.view.getUpdateButton().addActionListener(this);
        this.view.getDeleteButton().addActionListener(this);

        // BACK BUTTON LOGIC
        this.view.getBackButton().addActionListener(e -> {
            view.dispose();
            new MainDashboard(currentUser).setVisible(true);
        });

        this.view.getStockTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        loadStockIntoTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getSaveButton()) save();
        else if (e.getSource() == view.getUpdateButton()) update();
        else if (e.getSource() == view.getDeleteButton()) delete();
    }

    private void loadStockIntoTable() {
        try { view.refreshTable(dao.getAllBloodUnits()); }
        catch (SQLException e) { view.showMessage("Error loading stock: " + e.getMessage()); }
    }

    private void populateFormFromSelection() {
        int row = view.getStockTable().getSelectedRow();
        if (row != -1) {
            view.getBloodTypeComboBox().setSelectedItem(view.getStockTable().getValueAt(row, 1));
            view.getQuantityField().setText(view.getStockTable().getValueAt(row, 2).toString());

            LocalDate don = (LocalDate) view.getStockTable().getValueAt(row, 3);
            LocalDate exp = (LocalDate) view.getStockTable().getValueAt(row, 4);
            view.setDonationDate(java.util.Date.from(don.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            view.setExpiryDate(java.util.Date.from(exp.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            Object did = view.getStockTable().getValueAt(row, 5);
            view.getDonorIdField().setText((did != null && (int)did != 0) ? did.toString() : "");
        }
    }

    private void save() { process(0); }

    private void update() {
        int r = view.getStockTable().getSelectedRow();
        if (r == -1) { view.showMessage("Select row."); return; }
        process((int)view.getStockTable().getValueAt(r, 0));
    }

    private void delete() {
        int r = view.getStockTable().getSelectedRow();
        if (r == -1) { view.showMessage("Select row."); return; }
        if(JOptionPane.showConfirmDialog(view, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { if(dao.deleteBloodUnit((int)view.getStockTable().getValueAt(r, 0))) { view.showMessage("Deleted"); view.clearForm(); loadStockIntoTable(); } }
            catch(SQLException ex) { view.showMessage(ex.getMessage()); }
        }
    }

    private void process(int id) {
        try {
            String type = view.getSelectedBloodType();
            int qty = Integer.parseInt(view.getQuantity());
            LocalDate don = view.getDonationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate exp = view.getExpiryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (exp.isBefore(don)) { view.showMessage("Expiry cannot be before Donation."); return; }
            int did = view.getDonorId().isEmpty() ? 0 : Integer.parseInt(view.getDonorId());

            BloodUnit u = new BloodUnit(id > 0 ? id : 0, type, qty, don, exp, did);
            boolean ok = (id == 0) ? dao.saveBloodUnit(u) : dao.updateBloodUnit(u);
            if(ok) { view.showMessage("Success!"); view.clearForm(); loadStockIntoTable(); }
        } catch(Exception ex) { view.showMessage("Error: " + ex.getMessage()); }
    }
}