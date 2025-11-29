package view;

import model.Donor; // Will need this for the table model

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * View class for the Donor Registration Page.
 * This is the "V" in MVC.
 * This class is "dumb" - it only displays components and has "getters"
 * for the Controller to access them. It has NO action listeners.
 */
public class DonorRegistrationPage extends JFrame {

    // --- Components ---
    // We make them 'private' and provide 'getters'
    private JTextField nameField;
    private JTextField contactField;
    private JComboBox<String> bloodTypeComboBox;
    private JButton saveButton;
    private JButton updateButton; // We'll add this
    private JButton deleteButton; // And this
    private JTable donorTable;
    private DefaultTableModel tableModel;

    /**
     * Constructor to build the GUI.
     */
    public DonorRegistrationPage() {
        setTitle("Donor Registration and Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Change this later for multi-page
        setLocationRelativeTo(null); // Center the window

        // --- Layout ---
        setLayout(new BorderLayout(10, 10));

        // --- 1. The Form (Top Panel) ---
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add/Update Donor"));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Contact (Phone/Email):"));
        contactField = new JTextField();
        formPanel.add(contactField);

        formPanel.add(new JLabel("Blood Type:"));
        String[] bloodTypes = {"--Select--", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodTypeComboBox = new JComboBox<>(bloodTypes);
        formPanel.add(bloodTypeComboBox);

        // --- 2. The Buttons (Bottom of Form) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton = new JButton("Save New Donor");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");

        buttonPanel.add(saveButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Add form and buttons to a main "top" panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH); // Add the whole top section to the frame

        // --- 3. The Table (Center Panel) ---
        String[] columnNames = {"Donor ID", "Name", "Contact", "Blood Type", "Date Registered"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make the table non-editable by default
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donorTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Donors"));

        add(scrollPane, BorderLayout.CENTER); // Add table to the frame
    }

    // --- "Getters" for the Controller ---
    // The Controller will use these to add listeners and get data.

    public JTextField getNameField() { return nameField; }
    public String getDonorName() { return nameField.getText(); }

    public JTextField getContactField() { return contactField; }
    public String getDonorContact() { return contactField.getText(); }

    public JComboBox<String> getBloodTypeComboBox() { return bloodTypeComboBox; }
    public String getSelectedBloodType() { return (String) bloodTypeComboBox.getSelectedItem(); }

    public JButton getSaveButton() { return saveButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }

    public JTable getDonorTable() { return donorTable; }

    // --- Methods to update the View (called by Controller) ---

    /**
     * Shows a message dialog. This is our replacement for 'alert()'.
     * This fulfills the JOptionPane requirement.
     * @param message The message to display.
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Clears all input fields in the form.
     * The Controller will call this after a successful save.
     */
    public void clearForm() {
        nameField.setText("");
        contactField.setText("");
        bloodTypeComboBox.setSelectedIndex(0); // Reset to "--Select--"
    }

    /**
     * Reloads the JTable with a fresh list of donors.
     * The Controller will call this after saving, updating, or deleting.
     * @param donors The new list of donors from the DAO.
     */
    public void refreshTable(List<Donor> donors) {
        // Clear the existing table data
        tableModel.setRowCount(0);

        // Add each donor as a new row
        for (Donor donor : donors) {
            tableModel.addRow(new Object[]{
                    donor.getDonorId(),
                    donor.getName(),
                    donor.getContact(),
                    donor.getBloodType(),
                    donor.getDateRegistered()
            });
        }
    }
}