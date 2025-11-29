package model;

import java.time.LocalDate;

/**
 * Model class for an Alert.
 * This class corresponds to the 'Alerts' table in the database.
 */
public class Alert {

    // --- Fields ---
    private int alertId;
    private int bloodId; // Foreign Key
    private String alertType; // "Near Expiry" or "Expired"
    private LocalDate dateGenerated;
    private String status; // "Pending" or "Handled"

    // --- Constructors ---
    public Alert() {
    }

    /**
     * Full constructor for retrieving an Alert from the database.
     */
    public Alert(int alertId, int bloodId, String alertType, LocalDate dateGenerated, String status) {
        this.alertId = alertId;
        this.bloodId = bloodId;
        this.alertType = alertType;
        this.dateGenerated = dateGenerated;
        this.status = status;
    }

    /**
     * Constructor for creating a new Alert.
     */
    public Alert(int bloodId, String alertType, LocalDate dateGenerated, String status) {
        this.bloodId = bloodId;
        this.alertType = alertType;
        this.dateGenerated = dateGenerated;
        this.status = status;
    }

    // --- Getters and Setters ---
    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public int getBloodId() {
        return bloodId;
    }

    public void setBloodId(int bloodId) {
        this.bloodId = bloodId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public LocalDate getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(LocalDate dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- toString() Method ---
    @Override
    public String toString() {
        return "Alert{" +
                "alertId=" + alertId +
                ", bloodId=" + bloodId +
                ", alertType='" + alertType + '\'' +
                ", dateGenerated=" + dateGenerated +
                ", status='" + status + '\'' +
                '}';
    }
}
