package model;

import java.time.LocalDate;

public class Alert {
    private int alertId;
    private int bloodId;
    private String alertType;
    private LocalDate dateGenerated;
    private String status;

    // NEW: Field to hold the blood type name (fetched from DB)
    private String bloodTypeString;

    public Alert() {}

    // Updated Constructor to include bloodTypeString
    public Alert(int alertId, int bloodId, String bloodTypeString, String alertType, LocalDate dateGenerated, String status) {
        this.alertId = alertId;
        this.bloodId = bloodId;
        this.bloodTypeString = bloodTypeString; // Store it here
        this.alertType = alertType;
        this.dateGenerated = dateGenerated;
        this.status = status;
    }

    public int getAlertId() { return alertId; }
    public void setAlertId(int alertId) { this.alertId = alertId; }

    public int getBloodId() { return bloodId; }
    public void setBloodId(int bloodId) { this.bloodId = bloodId; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public LocalDate getDateGenerated() { return dateGenerated; }
    public void setDateGenerated(LocalDate dateGenerated) { this.dateGenerated = dateGenerated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Getter for the new field
    public String getBloodTypeString() { return bloodTypeString; }
    public void setBloodTypeString(String bloodTypeString) { this.bloodTypeString = bloodTypeString; }
}