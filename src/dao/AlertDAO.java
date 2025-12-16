package dao;

import model.Alert;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    public void checkForNewAlerts() throws SQLException {
        String queryUnits = "SELECT blood_id, expiry_date FROM BloodUnits";
        String insertAlert = "INSERT INTO Alerts (blood_id, alert_type, date_generated, status) VALUES (?, ?, ?, 'Pending')";
        String checkExists = "SELECT count(*) FROM Alerts WHERE blood_id = ? AND alert_type = ?";
        LocalDate today = LocalDate.now();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryUnits)) {

            while (rs.next()) {
                int bid = rs.getInt("blood_id");
                LocalDate exp = rs.getDate("expiry_date").toLocalDate();
                String type = null;

                if (exp.isBefore(today)) type = "Expired";
                else if (exp.isBefore(today.plusDays(8))) type = "Near Expiry";

                if (type != null && !alertExists(conn, bid, type, checkExists)) {
                    try (PreparedStatement ps = conn.prepareStatement(insertAlert)) {
                        ps.setInt(1, bid); ps.setString(2, type); ps.setDate(3, Date.valueOf(today));
                        ps.executeUpdate();
                    }
                }
            }
        }
    }

    private boolean alertExists(Connection conn, int bid, String type, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bid); ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }

    // UPDATED: Now fetches Blood Type using JOIN
    public List<Alert> getAllAlerts() throws SQLException {
        List<Alert> list = new ArrayList<>();

        // SQL: Join Alerts with BloodUnits to get the blood_type string
        String sql = "SELECT a.alert_id, a.blood_id, b.blood_type, a.alert_type, a.date_generated, a.status " +
                "FROM Alerts a " +
                "JOIN BloodUnits b ON a.blood_id = b.blood_id " +
                "ORDER BY a.date_generated DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Alert(
                        rs.getInt("alert_id"),
                        rs.getInt("blood_id"),
                        rs.getString("blood_type"), // Pass the fetched blood type to Model
                        rs.getString("alert_type"),
                        rs.getDate("date_generated").toLocalDate(),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    public boolean deleteAlert(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Alerts WHERE alert_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}