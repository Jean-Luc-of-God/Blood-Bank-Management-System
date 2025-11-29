package dao;

import model.BloodUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class BloodUnitDAO {

    public boolean saveBloodUnit(BloodUnit unit) throws SQLException {
        String sql = "INSERT INTO BloodUnits (blood_type, quantity, donation_date, expiry_date, donor_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unit.getBloodType());
            pstmt.setInt(2, unit.getQuantity());
            pstmt.setDate(3, Date.valueOf(unit.getDonationDate()));
            pstmt.setDate(4, Date.valueOf(unit.getExpiryDate()));
            if (unit.getDonorId() > 0) pstmt.setInt(5, unit.getDonorId());
            else pstmt.setNull(5, java.sql.Types.INTEGER);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<BloodUnit> getAllBloodUnits() throws SQLException {
        List<BloodUnit> units = new ArrayList<>();
        String sql = "SELECT * FROM BloodUnits ORDER BY expiry_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                units.add(new BloodUnit(
                        rs.getInt("blood_id"),
                        rs.getString("blood_type"),
                        rs.getInt("quantity"),
                        rs.getDate("donation_date").toLocalDate(),
                        rs.getDate("expiry_date").toLocalDate(),
                        rs.getInt("donor_id")
                ));
            }
        }
        return units;
    }

    public boolean updateBloodUnit(BloodUnit unit) throws SQLException {
        String sql = "UPDATE BloodUnits SET blood_type=?, quantity=?, donation_date=?, expiry_date=?, donor_id=? WHERE blood_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unit.getBloodType());
            pstmt.setInt(2, unit.getQuantity());
            pstmt.setDate(3, Date.valueOf(unit.getDonationDate()));
            pstmt.setDate(4, Date.valueOf(unit.getExpiryDate()));
            if (unit.getDonorId() > 0) pstmt.setInt(5, unit.getDonorId());
            else pstmt.setNull(5, java.sql.Types.INTEGER);
            pstmt.setInt(6, unit.getBloodId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteBloodUnit(int bloodId) throws SQLException {
        String sql = "DELETE FROM BloodUnits WHERE blood_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bloodId);
            return pstmt.executeUpdate() > 0;
        }
    }


    public void deductStock(String bloodType, int quantityNeeded) throws SQLException {
        String selectSql = "SELECT blood_id, quantity FROM BloodUnits WHERE blood_type = ? AND quantity > 0 ORDER BY expiry_date ASC";
        String updateSql = "UPDATE BloodUnits SET quantity = ? WHERE blood_id = ?";
        String deleteSql = "DELETE FROM BloodUnits WHERE blood_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement fetchStmt = conn.prepareStatement(selectSql)) {
                fetchStmt.setString(1, bloodType);
                ResultSet rs = fetchStmt.executeQuery();

                while (rs.next() && quantityNeeded > 0) {
                    int id = rs.getInt("blood_id");
                    int currentQty = rs.getInt("quantity");

                    if (currentQty <= quantityNeeded) {
                        try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                            del.setInt(1, id);
                            del.executeUpdate();
                        }
                        quantityNeeded -= currentQty;
                    } else {
                        try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                            upd.setInt(1, currentQty - quantityNeeded);
                            upd.setInt(2, id);
                            upd.executeUpdate();
                        }
                        quantityNeeded = 0;
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}