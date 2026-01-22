package lk.sliit.vehiclerental.vehiclerental.model;

import java.time.LocalDate;

public class WaitlistRequest {
    private String requestId;
    private String vehicleId;
    private String guestId;
    private LocalDate requestDate;
    private LocalDate desiredStartDate;
    private LocalDate desiredEndDate;
    private String status; // PENDING, FULFILLED, CANCELLED

    public WaitlistRequest(String requestId, String vehicleId, String guestId, 
                          LocalDate desiredStartDate, LocalDate desiredEndDate) {
        this.requestId = requestId;
        this.vehicleId = vehicleId;
        this.guestId = guestId;
        this.requestDate = LocalDate.now();
        this.desiredStartDate = desiredStartDate;
        this.desiredEndDate = desiredEndDate;
        this.status = "PENDING";
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }
    
    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    
    public LocalDate getDesiredStartDate() { return desiredStartDate; }
    public void setDesiredStartDate(LocalDate desiredStartDate) { this.desiredStartDate = desiredStartDate; }
    
    public LocalDate getDesiredEndDate() { return desiredEndDate; }
    public void setDesiredEndDate(LocalDate desiredEndDate) { this.desiredEndDate = desiredEndDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toFileString() {
        return String.join("|", 
            requestId,
            vehicleId,
            guestId,
            requestDate.toString(),
            desiredStartDate.toString(),
            desiredEndDate.toString(),
            status
        );
    }
} 