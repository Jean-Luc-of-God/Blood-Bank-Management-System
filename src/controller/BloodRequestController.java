package controller;

import dao.BloodRequestDAO;
import dao.BloodUnitDAO;
import model.BloodRequest;
import model.User;
import view.BloodRequestPage;
import view.MainDashboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

public class BloodRequestController implements ActionListener {
    private final BloodRequestPage view;
    private final BloodRequestDAO reqDAO;
    private final BloodUnitDAO unitDAO;
    private final User currentUser;

    public BloodRequestController(BloodRequestPage view, BloodRequestDAO reqDAO, User user) {
        this.view = view;
        this.reqDAO = reqDAO;
        this.currentUser = user;
        this.unitDAO = new BloodUnitDAO();

        view.getSubmitButton().addActionListener(this);
        view.getFulfillButton().addActionListener(this);
        view.getDeleteButton().addActionListener(this);

        // BACK BUTTON LOGIC
        view.getBackButton().addActionListener(e -> {
            view.dispose();
            new MainDashboard(currentUser).setVisible(true);
        });

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
            java.util.Date utilDate = view.getRequestDate();
            LocalDate date = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if(type.equals("--Select--")) { view.showMessage("Select blood type."); return; }
            if(reqDAO.getTotalStockForType(type) < qty) { view.showMessage("STOCK ERROR: Insufficient stock."); return; }

            reqDAO.saveRequest(new BloodRequest(type, qty, date, false));
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

        if("Fulfilled".equals(status)) { view.showMessage("Already done."); return; }

        if(JOptionPane.showConfirmDialog(view, "Fulfill Request?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if(reqDAO.getTotalStockForType(type) < qty) { view.showMessage("Not enough stock."); return; }
                unitDAO.deductStock(type, qty);
                reqDAO.markAsFulfilled(id);
                view.showMessage("Fulfilled!"); loadData();
            } catch(SQLException ex) { view.showMessage(ex.getMessage()); }
        }
    }

    private void delete() {
        int r = view.getRequestTable().getSelectedRow();
        if(r != -1) {
            try { reqDAO.deleteRequest((int)view.getRequestTable().getValueAt(r, 0)); loadData(); }
            catch(SQLException ex) { view.showMessage(ex.getMessage()); }
        }
    }
}