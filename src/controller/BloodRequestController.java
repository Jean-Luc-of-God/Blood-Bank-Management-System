package controller;

import dao.BloodRequestDAO;
import dao.BloodUnitDAO;
import model.BloodRequest;
import view.BloodRequestPage;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;

public class BloodRequestController implements ActionListener {
    private final BloodRequestPage view;
    private final BloodRequestDAO reqDAO;
    private final BloodUnitDAO unitDAO;

    public BloodRequestController(BloodRequestPage view, BloodRequestDAO reqDAO) {
        this.view = view;
        this.reqDAO = reqDAO;
        this.unitDAO = new BloodUnitDAO(); // Needed for fulfillment logic

        view.getSubmitButton().addActionListener(this);
        view.getFulfillButton().addActionListener(this);
        view.getDeleteButton().addActionListener(this);

        loadData();
    }

    private void loadData() {
        try { view.refreshTable(reqDAO.getAllRequests()); }
        catch (SQLException e) { view.showMessage("Error: " + e.getMessage()); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getSubmitButton()) submit();
        else if (e.getSource() == view.getFulfillButton()) fulfill();
        else if (e.getSource() == view.getDeleteButton()) delete();
    }

    private void submit() {
        try {
            String type = view.getSelectedBloodType();
            int qty = Integer.parseInt(view.getQuantity());
            if(type.equals("--Select--")) { view.showMessage("Select blood type."); return; }

            // Check stock BEFORE submitting
            if(reqDAO.getTotalStockForType(type) < qty) {
                view.showMessage("STOCK ERROR: Only " + reqDAO.getTotalStockForType(type) + " units available.");
                return;
            }

            reqDAO.saveRequest(new BloodRequest(type, qty, LocalDate.parse(view.getRequestDate()), false));
            view.showMessage("Saved!"); view.clearForm(); loadData();
        } catch(Exception ex) { view.showMessage("Error: Check inputs."); }
    }

    private void fulfill() {
        int r = view.getRequestTable().getSelectedRow();
        if(r == -1) { view.showMessage("Select a request."); return; }

        int id = (int) view.getRequestTable().getValueAt(r, 0);
        String type = (String) view.getRequestTable().getValueAt(r, 1);
        int qty = (int) view.getRequestTable().getValueAt(r, 2);
        String status = (String) view.getRequestTable().getValueAt(r, 4);

        if("Fulfilled".equals(status)) { view.showMessage("Already fulfilled."); return; }

        int confirm = JOptionPane.showConfirmDialog(view, "Fulfill and remove " + qty + " units from stock?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                // Double check stock
                if(reqDAO.getTotalStockForType(type) < qty) { view.showMessage("Not enough stock remaining."); return; }

                // 1. Remove from stock
                unitDAO.deductStock(type, qty);
                // 2. Mark request as done
                reqDAO.markAsFulfilled(id);

                view.showMessage("Success! Stock Updated."); loadData();
            } catch(SQLException ex) { view.showMessage(ex.getMessage()); }
        }
    }

    private void delete() {
        int r = view.getRequestTable().getSelectedRow();
        if(r == -1) { view.showMessage("Select a request."); return; }

        int id = (int) view.getRequestTable().getValueAt(r, 0);
        try {
            if(reqDAO.deleteRequest(id)) { view.showMessage("Deleted."); loadData(); }
        } catch(SQLException ex) { view.showMessage(ex.getMessage()); }
    }
}