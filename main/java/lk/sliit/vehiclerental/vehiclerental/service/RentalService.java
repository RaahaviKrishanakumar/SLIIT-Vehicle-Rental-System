package lk.sliit.vehiclerental.vehiclerental.service;

import lk.sliit.vehiclerental.vehiclerental.elements.LinkedList;
import lk.sliit.vehiclerental.vehiclerental.model.Rental;
import lk.sliit.vehiclerental.vehiclerental.model.Vehicle;

import java.io.*;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class RentalService {
    private static final Logger LOGGER = Logger.getLogger(RentalService.class.getName());
    private final String dataDirectory;
    private final String rentalsFilePath;
    private LinkedList<Rental> allRentals = new LinkedList<>();
    private final VehicleService vehicleService;

    public RentalService(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
        this.dataDirectory = "C:\\Users\\raaha\\Documents\\GitHub\\VehicleRental\\data";
        this.rentalsFilePath = "C:\\Users\\raaha\\Documents\\GitHub\\VehicleRental\\data\\rentals.txt";
        createDataDirectoryIfNotExists();
        loadRentalsFromFile();
        LOGGER.info("RentalService initialized with data directory: " + dataDirectory);
    }

    private void createDataDirectoryIfNotExists() {
        File dataDir = new File(dataDirectory);
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                LOGGER.warning("Failed to create data directory at: " + dataDirectory);
            } else {
                LOGGER.info("Created data directory at: " + dataDirectory);
            }
        }
    }

    public synchronized String createRental(String vehicleId, String userId, LocalDate startDate, LocalDate endDate) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.warning("Vehicle with ID " + vehicleId + " not found.");
                return null;
            }

            if (!vehicle.isAvailable()) {
                LOGGER.warning("Vehicle with ID " + vehicleId + " is not available.");
                return null;
            }

            String rentalId = "R" + UUID.randomUUID().toString().substring(0, 8);
            long days = startDate.until(endDate).getDays() + 1;
            double totalCost = days * vehicle.getDailyRate();

            Rental rental = new Rental(rentalId, vehicle, userId, startDate, endDate, totalCost);
            allRentals.add(rental);
            vehicle.setAvailable(false);

            boolean rentalSaved = saveRentalsToFile();
            vehicleService.saveVehiclesToFile();

            if (!rentalSaved) {
                allRentals.remove(rental);
                vehicle.setAvailable(true);
                vehicleService.saveVehiclesToFile();
                LOGGER.severe("Failed to save rental data. Rolled back changes.");
                return null;
            }

            LOGGER.info("Created new rental: " + rentalId + " for vehicle: " + vehicleId);
            return rentalId;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating rental for vehicle: " + vehicleId, e);
            return null;
        }
    }

    public List<Rental> getActiveRentals() {
        List<Rental> activeRentals = new ArrayList<>();
        for (Rental r : allRentals) {
            if (r.isActive()) {
                activeRentals.add(r);
            }
        }
        return activeRentals;
    }

    public List<Rental> getAllRentals() {
        List<Rental> rentalsCopy = new ArrayList<>();
        for (Rental r : allRentals) {
            rentalsCopy.add(r);
        }
        return rentalsCopy;
    }

    public Rental getRentalById(String rentalId) {
        for (Rental r : allRentals) {
            if (r.getRentalId().equals(rentalId)) {
                return r;
            }
        }
        return null;
    }

    public void endRental(String rentalId) throws IOException {
        Rental rental = getRentalById(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException("Rental not found");
        }
        allRentals.remove(rental);
        saveRentalsToFile();
    }

    public boolean editRental(String rentalId, LocalDate newStartDate, LocalDate newEndDate) {
        try {
            Rental rental = getRentalById(rentalId);
            if (rental != null && rental.isActive()) {
                long days = newStartDate.until(newEndDate).getDays() + 1;
                double newTotalCost = days * rental.getVehicle().getDailyRate();

                rental.setStartDate(newStartDate);
                rental.setEndDate(newEndDate);
                rental.setTotalCost(newTotalCost);

                if (!saveRentalsToFile()) {
                    LOGGER.severe("Failed to save updated rental: " + rentalId);
                    return false;
                }

                LOGGER.info("Updated rental: " + rentalId);
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error editing rental: " + rentalId, e);
            return false;
        }
    }

    public void deleteRental(String rentalId) {
        try {
            Rental rental = getRentalById(rentalId);
            if (rental != null) {
                allRentals.remove(rental);
                rental.getVehicle().setAvailable(true);
                vehicleService.saveVehiclesToFile();
                saveRentalsToFile();
                LOGGER.info("Deleted rental: " + rentalId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting rental: " + rentalId, e);
        }
    }

    private void loadRentalsFromFile() {
        File file = new File(rentalsFilePath);
        if (!file.exists()) {
            LOGGER.info("No existing rental data file found at: " + rentalsFilePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        String rentalId = parts[0];
                        String vehicleId = parts[1];
                        String userId = parts[2];
                        LocalDate startDate = LocalDate.parse(parts[3]);
                        LocalDate endDate = LocalDate.parse(parts[4]);
                        double totalCost = Double.parseDouble(parts[5]);
                        boolean isActive = Boolean.parseBoolean(parts[6]);

                        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
                        if (vehicle != null) {
                            Rental rental = new Rental(rentalId, vehicle, userId, startDate, endDate, totalCost);
                            rental.setActive(isActive);
                            allRentals.add(rental);
                            if (isActive) {
                                vehicle.setAvailable(false);
                            }
                            LOGGER.info("Loaded rental: " + rentalId + " (Active: " + isActive + ")");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error parsing rental line: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading rentals from file: " + rentalsFilePath, e);
        }
    }

    private boolean saveRentalsToFile() {
        try {
            File file = new File(rentalsFilePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    LOGGER.warning("Failed to create parent directories for: " + rentalsFilePath);
                    return false;
                }
            }

            if (file.exists() && !file.canWrite()) {
                LOGGER.severe("Cannot write to rentals file: " + rentalsFilePath);
                return false;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Rental rental : allRentals) {
                    writer.println(String.join(",",
                            rental.getRentalId(),
                            rental.getVehicle().getId(),
                            rental.getUserId(),
                            rental.getStartDate().toString(),
                            rental.getEndDate().toString(),
                            String.valueOf(rental.getTotalCost()),
                            String.valueOf(rental.isActive())
                    ));
                }
                LOGGER.info("Saved " + allRentals.size() + " rentals to: " + rentalsFilePath);
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving rentals to file: " + rentalsFilePath, e);
            return false;
        }
    }

    public List<Rental> getRentalsByVehicleId(String vehicleId) {
        List<Rental> results = new ArrayList<>();
        for (Rental r : allRentals) {
            if (r.getVehicle().getId().equals(vehicleId)) {
                results.add(r);
            }
        }
        return results;
    }

    public List<Rental> getRentalsByUserId(String userId) {
        List<Rental> results = new ArrayList<>();
        for (Rental r : allRentals) {
            if (r.getUserId().equals(userId)) {
                results.add(r);
            }
        }
        return results;
    }
}
