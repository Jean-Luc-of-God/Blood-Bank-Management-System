package dao;

import model.Donor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // IMPORTANT: Use java.sql.Date for PreparedStatement
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * DAO (Data Access Object) for the Donor model.
 * This class handles all database operations (CRUD) for the 'Donors' table.
 * Its only job is to talk to the database.
 * It uses the 'DatabaseConnection' class to get its connection.
 */
public class DonorDAO {

    /**
     * Saves a new Donor to the 'Donors' table in the database.
     * This is the "Create" in CRUD.
     *
     * @param donor The Donor object (Model) to be saved.
     * @return true if the donor was saved successfully, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    public boolean saveDonor(Donor donor) throws SQLException {
        // This is the SQL query. The '?' are placeholders.
        String sql = "INSERT INTO Donors (name, contact, blood_type, date_registered) VALUES (?, ?, ?, ?)";

        // This is a "try-with-resources" block.
        // It automatically opens and closes our database connection.
        // Notice we are "turning on the tap" by calling our helper class!
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Now, we "unpack" the Donor object (Model) and set the placeholders.
            // This is "parameterized query" and prevents SQL Injection attacks.
            // 1st '?' is the name
            pstmt.setString(1, donor.getName());
            // 2nd '?' is the contact
            pstmt.setString(2, donor.getContact());
            // 3rd '?' is the blood_type
            pstmt.setString(3, donor.getBloodType());

            // 4th '?' is the date.
            // We must convert Java's modern 'LocalDate' to SQL's older 'Date'.
            pstmt.setDate(4, Date.valueOf(donor.getDateRegistered()));

            // Execute the query.
            // 'executeUpdate()' is used for INSERT, UPDATE, or DELETE.
            // It returns the number of rows affected.
            int rowsAffected = pstmt.executeUpdate();

            // If rowsAffected > 0, it means the insert was successful.
            return rowsAffected > 0;
        }
        // The 'try-with-resources' automatically closes 'conn' and 'pstmt',
        // even if an error occurs. This prevents database leaks.
    }

    /**
     * Retrieves all Donors from the 'Donors' table.
     * This is the "Read" in CRUD.
     *
     * @return A List of Donor objects. The list will be empty if no donors are found.
     * @throws SQLException if a database error occurs.
     */
    public List<Donor> getAllDonors() throws SQLException {
        // Create an empty list to hold our results.
        List<Donor> donors = new ArrayList<>();

        // SQL query to select all donors.
        String sql = "SELECT * FROM Donors";

        // We use 'try-with-resources' again for Connection, Statement, and ResultSet.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) { // 'executeQuery()' is used for SELECT.

            // Loop through every row the database returned.
            while (rs.next()) {
                // For each row, create a new Donor object.
                // This is where we use our "full" constructor!
                Donor donor = new Donor(
                        rs.getInt("donor_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("blood_type"),
                        // We must convert SQL's 'Date' back to Java's 'LocalDate'.
                        rs.getDate("date_registered").toLocalDate()
                );

                // Add the newly created object to our list.
                donors.add(donor);
            }
        }
        // 'conn', 'pstmt', and 'rs' are all automatically closed here.

        // Return the list (it might be empty, which is fine).
        return donors;
    }

    // --- We will add these methods later ---

    /**
     * public boolean updateDonor(Donor donor) { ... }
     */

    /**
     * public boolean deleteDonor(int donorId) { ... }
     */

    /**
     * public Donor getDonorById(int donorId) { ... }
     */
}