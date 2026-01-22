package lk.sliit.vehiclerental.vehiclerental.model;
import java.time.LocalDate;

public class Rental {
    private String rentalId;
    private Vehicle vehicle; // Changed from String vehicleId to Vehicle object
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;
    private boolean isActive;

    // Constructor, getters, and setters
    public Rental(String rentalId, Vehicle vehicle, String userId, LocalDate startDate,
                  LocalDate endDate, double totalCost) {
        this.rentalId = rentalId;
        this.vehicle = vehicle; // Now takes a Vehicle object
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }
}