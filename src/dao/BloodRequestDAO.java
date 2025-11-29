package dao;

import model.BloodRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BloodRequestDAO {

    public boolean saveRequest(BloodRequest request) throws SQLException {
        String sql = "INSERT INTO BloodRequests (blood_type, quantity, request_date, fulfilled) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getBloodType());
            pstmt.setInt(2, request.getQuantity());
            pstmt.setDate(3, Date.valueOf(request.getRequestDate()));
            pstmt.setBoolean(4, request.isFulfilled());
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<BloodRequest> getAllRequests() throws SQLException {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM BloodRequests ORDER BY request_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                requests.add(new BloodRequest(
                        rs.getInt("request_id"),
                        rs.getString("blood_type"),
                        rs.getInt("quantity"),
                        rs.getDate("request_date").toLocalDate(),
                        rs.getBoolean("fulfilled")
                ));
            }
        }
        return requests;
    }

    public int getTotalStockForType(String bloodType) throws SQLException {
        String sql = "SELECT SUM(quantity) AS total FROM BloodUnits WHERE blood_type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bloodType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }

    // --- NEW METHOD: CALCULATE PENDING STOCK ---
    // Counts how much blood is currently requested but not yet fulfilled.
    public int getPendingStockForType(String bloodType) throws SQLException {
        String sql = "SELECT SUM(quantity) AS total FROM BloodRequests WHERE blood_type = ? AND fulfilled = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bloodType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0; // Return 0 if no pending requests found
    }

    public boolean deleteRequest(int id) throws SQLException {
        String sql = "DELETE FROM BloodRequests WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean markAsFulfilled(int id) throws SQLException {
        String sql = "UPDATE BloodRequests SET fulfilled = 1 WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}