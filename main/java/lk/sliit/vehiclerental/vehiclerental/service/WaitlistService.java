package lk.sliit.vehiclerental.vehiclerental.service;

import lk.sliit.vehiclerental.vehiclerental.model.WaitlistRequest;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WaitlistService {
    private static final Logger LOGGER = Logger.getLogger(WaitlistService.class.getName());
    private final String waitlistFilePath;
    private List<WaitlistRequest> waitlistRequests;

    public WaitlistService() {
        this.waitlistRequests = new ArrayList<>();
        this.waitlistFilePath = "C:\\Users\\raaha\\Documents\\GitHub\\VehicleRental\\data\\waitlist.txt";
        loadWaitlistFromFile();
    }

    public String addToWaitlist(String vehicleId, String guestId, LocalDate startDate, LocalDate endDate) {
        String requestId = "W" + UUID.randomUUID().toString().substring(0, 8);
        WaitlistRequest request = new WaitlistRequest(requestId, vehicleId, guestId, startDate, endDate);
        waitlistRequests.add(request);
        saveWaitlistToFile();
        return requestId;
    }

    public List<WaitlistRequest> getWaitlistForVehicle(String vehicleId) {
        return waitlistRequests.stream()
                .filter(request -> request.getVehicleId().equals(vehicleId) && 
                                 request.getStatus().equals("PENDING"))
                .collect(Collectors.toList());
    }

    public List<WaitlistRequest> getWaitlistByGuestId(String guestId) {
        return waitlistRequests.stream()
                .filter(request -> request.getGuestId().equals(guestId))
                .collect(Collectors.toList());
    }

    public WaitlistRequest getWaitlistRequest(String requestId) {
        return waitlistRequests.stream()
                .filter(request -> request.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);
    }

    public void updateRequestStatus(String requestId, String status) {
        WaitlistRequest request = getWaitlistRequest(requestId);
        if (request != null) {
            request.setStatus(status);
            saveWaitlistToFile();
        }
    }

    private void loadWaitlistFromFile() {
        File file = new File(waitlistFilePath);
        if (!file.exists()) {
            LOGGER.info("No existing waitlist file found at: " + waitlistFilePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7) {
                        WaitlistRequest request = new WaitlistRequest(
                            parts[0], // requestId
                            parts[1], // vehicleId
                            parts[2], // guestId
                            LocalDate.parse(parts[4]), // desiredStartDate
                            LocalDate.parse(parts[5])  // desiredEndDate
                        );
                        request.setRequestDate(LocalDate.parse(parts[3]));
                        request.setStatus(parts[6]);
                        waitlistRequests.add(request);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error parsing waitlist line: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading waitlist from file", e);
        }
    }

    private void saveWaitlistToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(waitlistFilePath, false))) {
            for (WaitlistRequest request : waitlistRequests) {
                writer.println(request.toFileString());
            }
            LOGGER.info("Saved " + waitlistRequests.size() + " waitlist requests to file");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving waitlist to file", e);
        }
    }
} 