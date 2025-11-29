package controller;

import model.Donor;
import dao.DonorDAO;
import view.DonorRegistrationPage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for the Donor Registration Page.
 * This is the "C" in MVC.
 * It acts as the "brain" that connects the View (DonorRegistrationPage)
 * and the DAO (DonorDAO). It handles all user actions and business logic.
 */
public class DonorController implements ActionListener {

    // --- References to the other layers ---
    private final DonorRegistrationPage view;
    private final DonorDAO dao;

    /**
     * Constructor. This is where we "inject" the dependencies (the view and the dao).
     * This is called "Dependency Injection".
     *
     * @param view The DonorRegistrationPage (V)
     * @param dao  The DonorDAO (DAO)
     */
    public DonorController(DonorRegistrationPage view, DonorDAO dao) {
        this.view = view;
        this.dao = dao;

        // --- Attach Listeners ---
        // Tell the controller to "listen" for clicks on the view's buttons.
        this.view.getSaveButton().addActionListener(this);
        this.view.getUpdateButton().addActionListener(this);
        this.view.getDeleteButton().addActionListener(this);

        // TODO: We also need to listen for when a user clicks on the JTable
        // to select a row for updating or deleting.
        // this.view.getDonorTable().getSelectionModel().addListSelectionListener(e -> populateFormFromTable());

        // --- Load Initial Data ---
        // Load all existing donors into the table when the app starts.
        loadDonorsIntoTable();
    }

    /**
     * This is the main "traffic cop" method.
     * It's called automatically whenever a button we are "listening" to is clicked.
     *
     * @param e The event that occurred (e.g., a button click).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the "source" of the action (which button was clicked?)
        Object source = e.getSource();

        if (source == view.getSaveButton()) {
            // User clicked "Save"
            saveNewDonor();
        } else if (source == view.getUpdateButton()) {
            // User clicked "Update"
            // TODO: Implement updateDonor()
            view.showMessage("Update functionality to be implemented.");
        } else if (source == view.getDeleteButton()) {
            // User clicked "Delete"
            // TODO: Implement deleteDonor()
            view.showMessage("Delete functionality to be implemented.");
        }
    }

    /**
     * Helper method to fetch all donors from the DAO
     * and tell the View to display them in the JTable.
     */
    private void loadDonorsIntoTable() {
        try {
            // 1. Call the DAO (Mechanic)
            List<Donor> donors = dao.getAllDonors();

            // 2. Tell the View (Service Manager) to update
            view.refreshTable(donors);

        } catch (SQLException e) {
            // This is the "Service Manager" catching the "leaking brake line"
            // and showing a polite message to the "Customer" (View).
            view.showMessage("Database Error: Could not load donors. " + e.getMessage());
        }
    }

    /**
     * Helper method to handle the "Save" button click.
     * This contains all our validation logic.
     */
    private void saveNewDonor() {
        // 1. Get Data from the View
        String name = view.getDonorName().trim();
        String contact = view.getDonorContact().trim();
        String bloodType = view.getSelectedBloodType();

        // 2. --- VALIDATION ---
        // This is where we check our business and technical rules.
        if (name.isEmpty()) {
            view.showMessage("Validation Error: Name cannot be empty.");
            return; // Stop the method
        }

        if (contact.isEmpty()) {
            view.showMessage("Validation Error: Contact cannot be empty.");
            return; // Stop the method
        }

        if (bloodType.equals("--Select--")) {
            view.showMessage("Validation Error: Please select a blood type.");
            return; // Stop the method
        }

        // (Add more validation here as needed...)

        // 3. Create the Model
        // All validation passed, so we "pack" a new Donor object.
        // We use the "new donor" constructor (no ID).
        Donor donor = new Donor(name, contact, bloodType, LocalDate.now());

        // 4. Call the DAO (and handle errors)
        try {
            boolean success = dao.saveDonor(donor);

            if (success) {
                // This fulfills the "JOptionPane for success" requirement
                view.showMessage("Donor saved successfully!");
                view.clearForm();       // Clear the form for the next entry
                loadDonorsIntoTable();  // Refresh the table to show the new data
            } else {
                view.showMessage("Error: Donor was not saved. (DAO returned false)");
            }
        } catch (SQLException e) {
            // This fulfills the "JOptionPane for error" requirement
            view.showMessage("Database Error: Could not save donor. " + e.getMessage());
        }
    }
}