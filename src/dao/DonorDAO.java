package dao;

import model.Donor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {

    public boolean saveDonor(Donor donor) throws SQLException {
        String sql = "INSERT INTO Donors (name, contact, blood_type, date_registered) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, donor.getName());
            pstmt.setString(2, donor.getContact());
            pstmt.setString(3, donor.getBloodType());
            pstmt.setDate(4, Date.valueOf(donor.getDateRegistered()));
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Donor> getAllDonors() throws SQLException {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM Donors";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                donors.add(new Donor(
                        rs.getInt("donor_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("blood_type"),
                        rs.getDate("date_registered").toLocalDate()
                ));
            }
        }
        return donors;
    }

    /**
     * UPDATED METHOD: Uses a Transaction.
     * When we update a Donor, we MUST also update the blood_type
     * of any stock associated with them (BloodUnits table).
     */
    public boolean updateDonor(Donor donor) throws SQLException {
        String updateDonorSql = "UPDATE Donors SET name=?, contact=?, blood_type=? WHERE donor_id=?";
        String updateStockSql = "UPDATE BloodUnits SET blood_type=? WHERE donor_id=?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // 1. Turn off AutoCommit (Start Transaction)
            conn.setAutoCommit(false);

            // 2. Update the Donor Profile
            try (PreparedStatement ps1 = conn.prepareStatement(updateDonorSql)) {
                ps1.setString(1, donor.getName());
                ps1.setString(2, donor.getContact());
                ps1.setString(3, donor.getBloodType());
                ps1.setInt(4, donor.getDonorId());
                ps1.executeUpdate();
            }

            // 3. Update the Linked Blood Stock (Keep them in sync!)
            try (PreparedStatement ps2 = conn.prepareStatement(updateStockSql)) {
                ps2.setString(1, donor.getBloodType()); // New Blood Type
                ps2.setInt(2, donor.getDonorId());      // Where ID matches
                ps2.executeUpdate();
            }

            // 4. Commit (Save Everything)
            conn.commit();
            return true;

        } catch (SQLException e) {
            // If anything fails, Undo (Rollback)
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public boolean deleteDonor(int id) throws SQLException {
        String sql = "DELETE FROM Donors WHERE donor_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}