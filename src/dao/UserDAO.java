package dao;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Checks credentials against the database.
     * DEBUG VERSION: Prints exactly what is being checked.
     */
    public User login(String username, String password, String role) throws SQLException {
        // Query now checks Role as well
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ? AND role = ?";

        System.out.println("--- DEBUG: Attempting Login ---");
        System.out.println("Checking Username: [" + username + "]");
        System.out.println("Checking Password: [" + password + "]");
        System.out.println("Checking Role:     [" + role + "]");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG: SUCCESS! User found in database.");
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                } else {
                    System.out.println("DEBUG: FAILURE. Query returned 0 results.");
                    // Optional: Check if user exists but with wrong role/password for better debugging
                    checkPartialMatch(conn, username);
                }
            }
        }
        return null; // Login failed
    }

    // Helper to find out WHY login failed
    private void checkPartialMatch(Connection conn, String username) {
        try {
            String checkSql = "SELECT * FROM Users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("DEBUG HINT: Username '" + username + "' EXISTS.");
                    System.out.println("   -> Stored Password: " + rs.getString("password"));
                    System.out.println("   -> Stored Role:     " + rs.getString("role"));
                    System.out.println("   -> Mismatch is in Password or Role.");
                } else {
                    System.out.println("DEBUG HINT: Username '" + username + "' does NOT exist.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) throws SQLException {
        if (checkUsernameExists(user.getUsername())) return false;

        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

            return pstmt.executeUpdate() > 0;
        }
    }

    private boolean checkUsernameExists(String username) throws SQLException {
        String sql = "SELECT count(*) FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}