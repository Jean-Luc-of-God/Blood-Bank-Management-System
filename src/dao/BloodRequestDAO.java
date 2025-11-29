package dao;

import model.BloodRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the BloodRequest model.
 * Handles database operations for the 'BloodRequests' table.
 */
public class BloodRequestDAO {

    /**
     * Saves a new blood request to the database.
     * @param request The request object.
     * @return true if saved successfully.
     */
    public boolean saveRequest(BloodRequest request) throws SQLException {
        String sql = "INSERT INTO BloodRequests (blood_type, quantity, request_date, fulfilled) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, request.getBloodType());
            pstmt.setInt(2, request.getQuantity());
            pstmt.setDate(3, Date.valueOf(request.getRequestDate()));
            pstmt.setBoolean(4, request.isFulfilled());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves all requests.
     */
    public List<BloodRequest> getAllRequests() throws SQLException {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM BloodRequests ORDER BY request_date DESC"; // Newest first

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BloodRequest req = new BloodRequest(
                        rs.getInt("request_id"),
                        rs.getString("blood_type"),
                        rs.getInt("quantity"),
                        rs.getDate("request_date").toLocalDate(),
                        rs.getBoolean("fulfilled")
                );
                requests.add(req);
            }
        }
        return requests;
    }

    /**
     * SPECIAL METHOD: Calculates total available stock for a specific blood type.
     * We need this to validate if a request can be fulfilled.
     * * @param bloodType The blood type to check (e.g., "A+").
     * @return The total quantity available in the BloodUnits table.
     */
    public int getTotalStockForType(String bloodType) throws SQLException {
        String sql = "SELECT SUM(quantity) AS total FROM BloodUnits WHERE blood_type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bloodType);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total"); // Returns 0 if null
                }
            }
        }
        return 0;
    }
}