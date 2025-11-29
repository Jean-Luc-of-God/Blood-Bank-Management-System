package dao;

import model.BloodUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // For SQL dates
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the BloodUnit model.
 * Handles CRUD operations for the 'BloodUnits' table.
 */
public class BloodUnitDAO {

    /**
     * Saves a new BloodUnit to the database.
     * @param unit The BloodUnit object to save.
     * @return true if successful, false otherwise.
     */
    public boolean saveBloodUnit(BloodUnit unit) throws SQLException {
        // SQL query matches the columns in your BloodUnits table
        String sql = "INSERT INTO BloodUnits (blood_type, quantity, donation_date, expiry_date, donor_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the values from the BloodUnit object
            pstmt.setString(1, unit.getBloodType());
            pstmt.setInt(2, unit.getQuantity());

            // Convert Java LocalDate to SQL Date
            pstmt.setDate(3, Date.valueOf(unit.getDonationDate()));
            pstmt.setDate(4, Date.valueOf(unit.getExpiryDate()));

            // For donor_id (Foreign Key), we assume the UI passed us a valid ID
            if (unit.getDonorId() > 0) {
                pstmt.setInt(5, unit.getDonorId());
            } else {
                // If no donor is linked, we set it to NULL
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves all BloodUnits from the database.
     * @return A List of BloodUnit objects.
     */
    public List<BloodUnit> getAllBloodUnits() throws SQLException {
        List<BloodUnit> units = new ArrayList<>();
        String sql = "SELECT * FROM BloodUnits";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Create a new BloodUnit object from the row data
                BloodUnit unit = new BloodUnit(
                        rs.getInt("blood_id"),
                        rs.getString("blood_type"),
                        rs.getInt("quantity"),
                        rs.getDate("donation_date").toLocalDate(),
                        rs.getDate("expiry_date").toLocalDate(),
                        rs.getInt("donor_id")
                );
                units.add(unit);
            }
        }
        return units;
    }

    // We can add delete/update methods later as needed
}