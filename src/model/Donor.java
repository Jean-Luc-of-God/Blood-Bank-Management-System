package model;

import java.time.LocalDate;

/**
 * Model class for a Donor.
 * This class represents the 'M' in MVC.
 * It's a simple POJO (Plain Old Java Object) to hold data
 * corresponding to the 'Donors' table in our database.
 */
public class Donor {

    // --- Fields ---
    // These directly match the columns in the 'Donors' table
    private int donorId;
    private String name;
    private String contact;
    private String bloodType;
    private LocalDate dateRegistered;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public Donor() {
    }

    /**
     * Overloaded constructor for creating a new donor (without an ID, as
     * the database will auto-increment it).
     */
    public Donor(String name, String contact, String bloodType, LocalDate dateRegistered) {
        this.name = name;
        this.contact = contact;
        this.bloodType = bloodType;
        this.dateRegistered = dateRegistered;
    }

    /**
     * Full constructor for retrieving a donor from the database (with an ID).
     */
    public Donor(int donorId, String name, String contact, String bloodType, LocalDate dateRegistered) {
        this.donorId = donorId;
        this.name = name;
        this.contact = contact;
        this.bloodType = bloodType;
        this.dateRegistered = dateRegistered;
    }

    // --- Getters and Setters ---
    // These allow the rest of our application (DAO, Controller, View)
    // to access and modify the data in a controlled way.

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public LocalDate getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(LocalDate dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    // --- toString() Method ---
    /**
     * A helpful method for debugging. It provides a string representation
     * of the Donor object.
     */
    @Override
    public String toString() {
        return "Donor{" +
                "donorId=" + donorId +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", dateRegistered=" + dateRegistered +
                '}';
    }
}
