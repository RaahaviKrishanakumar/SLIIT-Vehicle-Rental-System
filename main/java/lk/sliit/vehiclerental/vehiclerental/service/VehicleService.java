package lk.sliit.vehiclerental.vehiclerental.service;

import lk.sliit.vehiclerental.vehiclerental.model.Vehicle;
import lk.sliit.vehiclerental.vehiclerental.model.Car;
import lk.sliit.vehiclerental.vehiclerental.model.Bike;
import lk.sliit.vehiclerental.vehiclerental.model.Truck;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VehicleService {
    private static final Logger LOGGER = Logger.getLogger(VehicleService.class.getName());
    private final String dataDirectory;
    private final String vehiclesFilePath;
    private List<Vehicle> vehicles;

    public VehicleService() {
        this.vehicles = new ArrayList<>();
        this.dataDirectory = "C:\\Users\\raaha\\Documents\\GitHub\\VehicleRental\\data";
        this.vehiclesFilePath = "C:\\Users\\raaha\\Documents\\GitHub\\VehicleRental\\data\\vehicles.txt";
        createDataDirectoryIfNotExists();
        loadVehiclesFromFile();
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

    public synchronized void addVehicle(Vehicle vehicle) {
        try {
            vehicles.add(vehicle);
            saveVehiclesToFile();
            LOGGER.info("Added vehicle: " + vehicle.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding vehicle", e);
        }
    }

    public Vehicle getVehicleById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Vehicle> searchVehicles(String type, String make, String model) {
        List<Vehicle> results = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (matchesSearch(vehicle, type, make, model)) {
                results.add(vehicle);
            }
        }
        return results;
    }

    private boolean matchesSearch(Vehicle vehicle, String type, String make, String model) {
        boolean match = true;
        if (type != null && !type.isEmpty()) match &= vehicle.getType().equalsIgnoreCase(type);
        if (make != null && !make.isEmpty()) match &= vehicle.getMake().equalsIgnoreCase(make);
        if (model != null && !model.isEmpty()) match &= vehicle.getModel().equalsIgnoreCase(model);
        return match;
    }

    public void loadVehiclesFromFile() {
        File file = new File(vehiclesFilePath);
        if (!file.exists()) {
            LOGGER.info("No existing vehicle data file found at: " + vehiclesFilePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        Vehicle vehicle = createVehicleFromParts(parts);
                        if (vehicle != null) {
                            vehicles.add(vehicle);
                            LOGGER.info("Loaded vehicle: " + vehicle.getId());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error parsing vehicle line: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading vehicles from file: " + vehiclesFilePath, e);
        }
    }

    private Vehicle createVehicleFromParts(String[] parts) {
        try {
            String id = parts[0];
        String type = parts[1];
        String make = parts[2];
        String model = parts[3];
            double dailyRate = Double.parseDouble(parts[4]);

        return switch (type) {
                case "Car" -> new Car(id, make, model, dailyRate, Integer.parseInt(parts[5]));
                case "Bike" -> new Bike(id, make, model, dailyRate, parts[5]);
                case "Truck" -> new Truck(id, make, model, dailyRate, Double.parseDouble(parts[5]));
                default -> throw new IllegalArgumentException("Unknown vehicle type: " + type);
        };
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error creating vehicle from parts", e);
            return null;
        }
    }

    public void saveVehiclesToFile() {
        try {
            File file = new File(vehiclesFilePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    LOGGER.warning("Failed to create parent directories for: " + vehiclesFilePath);
                    return;
                }
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
                for (Vehicle vehicle : vehicles) {
                    writer.println(vehicle.toFileString());
                }
                LOGGER.info("Saved " + vehicles.size() + " vehicles to: " + vehiclesFilePath);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving vehicles to file: " + vehiclesFilePath, e);
        }
    }

    public void sortVehicles(List<Vehicle> vehicles, String sortBy) {
        vehicles.sort((a, b) -> {
        if ("price".equalsIgnoreCase(sortBy)) {
                return Double.compare(a.getDailyRate(), b.getDailyRate());
        }
        if ("availability".equalsIgnoreCase(sortBy)) {
                return Boolean.compare(b.isAvailable(), a.isAvailable());
        }
            return 0;
        });
    }

    public void updateVehicle(Vehicle updatedVehicle) throws IOException {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(updatedVehicle.getId())) {
                vehicles.set(i, updatedVehicle);
                saveVehiclesToFile();
                LOGGER.info("Updated vehicle: " + updatedVehicle.getId());
                return;
            }
        }
        throw new IllegalArgumentException("Vehicle not found with ID: " + updatedVehicle.getId());
    }

    public void deleteVehicle(String id) throws IOException {
        vehicles.removeIf(v -> v.getId().equals(id));
        saveVehiclesToFile();
    }

    private void saveVehicles(List<Vehicle> vehicles) throws IOException {
        File file = new File(vehiclesFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Vehicle v : vehicles) {
                writer.write(v.toFileString());
                writer.newLine();
            }
        }
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicles.stream()
                .filter(Vehicle::isAvailable)
                .collect(Collectors.toList());
    }
}
