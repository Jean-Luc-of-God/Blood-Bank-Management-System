package dao;

import model.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    /**
     * AUTOMATION LOGIC:
     * Scans the BloodUnits table for expiring/expired units.
     * Inserts new alerts into the Alerts table if they don't exist yet.
     */
    public void checkForNewAlerts() throws SQLException {
        String queryUnits = "SELECT blood_id, expiry_date FROM BloodUnits";
        String insertAlert = "INSERT INTO Alerts (blood_id, alert_type, date_generated, status) VALUES (?, ?, ?, 'Pending')";
        String checkExists = "SELECT count(*) FROM Alerts WHERE blood_id = ? AND alert_type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryUnits)) {

            while (rs.next()) {
                int bloodId = rs.getInt("blood_id");
                LocalDate expiry = rs.getDate("expiry_date").toLocalDate();
                LocalDate today = LocalDate.now();

                String alertType = null;

                // 1. Determine Status
                if (expiry.isBefore(today)) {
                    alertType = "Expired";
                } else if (expiry.isBefore(today.plusDays(8))) { // Within 7 days
                    alertType = "Near Expiry";
                }

                // 2. If it needs an alert, check if one already exists
                if (alertType != null) {
                    if (!alertExists(conn, bloodId, alertType, checkExists)) {
                        // 3. Create the Alert
                        try (PreparedStatement pstmt = conn.prepareStatement(insertAlert)) {
                            pstmt.setInt(1, bloodId);
                            pstmt.setString(2, alertType);
                            pstmt.setDate(3, Date.valueOf(today));
                            pstmt.executeUpdate();
                            System.out.println("Generated Alert: " + alertType + " for Blood ID " + bloodId);
                        }
                    }
                }
            }
        }
    }

    // Helper to prevent duplicate alerts
    private boolean alertExists(Connection conn, int bloodId, String type, String sql) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bloodId);
            pstmt.setString(2, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves all alerts, including extra details like Blood Type (via JOIN).
     * Note: We are sticking to the Alert model, but we might store the
     * blood type in the 'status' field temporarily for display, or just display IDs.
     * For this simple project, we will just fetch the Alert object.
     */
    public List<Alert> getAllAlerts() throws SQLException {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts ORDER BY date_generated DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Alert alert = new Alert(
                        rs.getInt("alert_id"),
                        rs.getInt("blood_id"),
                        rs.getString("alert_type"),
                        rs.getDate("date_generated").toLocalDate(),
                        rs.getString("status")
                );
                alerts.add(alert);
            }
        }
        return alerts;
    }

    // Method to clear handled alerts
    public boolean deleteAlert(int alertId) throws SQLException {
        String sql = "DELETE FROM Alerts WHERE alert_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, alertId);
            return pstmt.executeUpdate() > 0;
        }
    }
}
