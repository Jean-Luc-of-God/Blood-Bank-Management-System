package model;

import java.time.LocalDate;

/**
 * Model class for a BloodRequest.
 * This class corresponds to the 'BloodRequests' table in the database.
 */
public class BloodRequest {

    // --- Fields ---
    private int requestId;
    private String bloodType;
    private int quantity;
    private LocalDate requestDate;
    private boolean fulfilled; // Java's 'boolean' maps to MySQL's 'BOOLEAN' or 'TINYINT(1)'

    // --- Constructors ---
    public BloodRequest() {
    }

    /**
     * Full constructor for retrieving a BloodRequest from the database.
     */
    public BloodRequest(int requestId, String bloodType, int quantity, LocalDate requestDate, boolean fulfilled) {
        this.requestId = requestId;
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.requestDate = requestDate;
        this.fulfilled = fulfilled;
    }

    /**
     * Constructor for creating a new BloodRequest.
     */
    public BloodRequest(String bloodType, int quantity, LocalDate requestDate, boolean fulfilled) {
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.requestDate = requestDate;
        this.fulfilled = fulfilled;
    }

    // --- Getters and Setters ---
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            this.quantity = 0;
        } else {
            this.quantity = quantity;
        }
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    // For booleans, the getter is often named "is..."
    public boolean isFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

    // --- toString() Method ---
    @Override
    public String toString() {
        return "BloodRequest{" +
                "requestId=" + requestId +
                ", bloodType='" + bloodType + '\'' +
                ", quantity=" + quantity +
                ", requestDate=" + requestDate +
                ", fulfilled=" + fulfilled +
                '}';
    }
}