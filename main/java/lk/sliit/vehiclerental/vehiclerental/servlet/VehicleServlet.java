package lk.sliit.vehiclerental.vehiclerental.servlet;

import lk.sliit.vehiclerental.vehiclerental.model.Car;
import lk.sliit.vehiclerental.vehiclerental.model.Vehicle;
import lk.sliit.vehiclerental.vehiclerental.service.VehicleService;
import lk.sliit.vehiclerental.vehiclerental.model.Bike;
import lk.sliit.vehiclerental.vehiclerental.model.Truck;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet(name = "VehicleServlet", urlPatterns = {"/vehicles/*"})
public class VehicleServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(VehicleServlet.class.getName());
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        LOGGER.info("Handling GET request with path: " + pathInfo);

        // Default to list if no path is specified
        if (pathInfo == null || pathInfo.equals("/")) {
            pathInfo = "/list";
        }

        try {
            switch (pathInfo) {
                case "/add":
                    showAddForm(request, response);
                    break;
                case "/update":
                    showUpdateForm(request, response);
                    break;
                case "/list":
                    listVehicles(request, response);
                    break;
                case "/details":
                    showDetails(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred processing your request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        LOGGER.info("Handling POST request with path: " + pathInfo);

        try {
            if ("/add".equals(pathInfo)) {
                addVehicle(request, response);
            } else if ("/update".equals(pathInfo)) {
                updateVehicle(request, response);
            } else if ("/delete".equals(pathInfo)) {
                deleteVehicle(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred processing your request");
        }
    }

    // Show update form with vehicle data
    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vehicle not found");
                return;
            }
            request.setAttribute("vehicle", vehicle);
            request.getRequestDispatcher("/WEB-INF/views/vehicles/update.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing update form", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading vehicle data");
        }
    }

    private void updateVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        try {
            // Get the existing vehicle first to preserve its type
            Vehicle existingVehicle = vehicleService.getVehicleById(id);
            if (existingVehicle == null) {
                throw new IllegalArgumentException("Vehicle not found");
            }

            // Set the ID in the request for createVehicleFromRequest
            request.setAttribute("existingId", id);
            Vehicle updatedVehicle = createVehicleFromRequest(request);
            
            // Update availability
            String available = request.getParameter("available");
            updatedVehicle.setAvailable(available != null);

            vehicleService.updateVehicle(updatedVehicle);
            response.sendRedirect(request.getContextPath() + "/vehicles/details?id=" + id + "&message=Vehicle updated successfully");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error updating vehicle", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("vehicle", vehicleService.getVehicleById(id));
            request.getRequestDispatcher("/WEB-INF/views/vehicles/update.jsp").forward(request, response);
        }
    }

    private void deleteVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Vehicle ID is required");
            }
            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null) {
                throw new IllegalArgumentException("Vehicle not found");
            }
            vehicleService.deleteVehicle(id);
            response.sendRedirect(request.getContextPath() + "/vehicles/list?message=Vehicle deleted successfully");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error deleting vehicle", e);
            response.sendRedirect(request.getContextPath() + "/vehicles/list?error=" + e.getMessage());
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/vehicles/add.jsp").forward(request, response);
    }

    private void listVehicles(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        String make = request.getParameter("make");
        String model = request.getParameter("model");

        List<Vehicle> vehicles = vehicleService.searchVehicles(type, make, model);
        LOGGER.info("Found " + vehicles.size() + " vehicles");

        String sortBy = request.getParameter("sort");
        if (sortBy != null && !sortBy.isEmpty()) {
            vehicleService.sortVehicles(vehicles, sortBy);
        }

        request.setAttribute("vehicles", vehicles);
        request.getRequestDispatcher("/WEB-INF/views/vehicles/list.jsp").forward(request, response);
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null) {
                throw new NoSuchElementException("Vehicle not found");
            }
            request.setAttribute("vehicle", vehicle);
            
            // If the vehicle is not available and the request includes a rent parameter, redirect to rental request
            if (!vehicle.isAvailable() && request.getParameter("rent") != null) {
                response.sendRedirect(request.getContextPath() + "/rentals/request?vehicleId=" + id);
                return;
            }
            
            request.getRequestDispatcher("/WEB-INF/views/vehicles/details.jsp").forward(request, response);
        } catch (NoSuchElementException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vehicle not found");
        }
    }

    private void addVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Vehicle vehicle = createVehicleFromRequest(request);
            vehicleService.addVehicle(vehicle);
            response.sendRedirect(request.getContextPath() + "/vehicles/details?id=" + vehicle.getId());
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid vehicle data", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/vehicles/add.jsp").forward(request, response);
        }
    }

    private Vehicle createVehicleFromRequest(HttpServletRequest request) {
        String type = request.getParameter("type");
        LOGGER.info("Creating vehicle of type: " + type);
        
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        // Use existing ID if provided (for updates), otherwise generate new one
        String id = (String) request.getAttribute("existingId");
        if (id == null) {
            id = "V" + UUID.randomUUID().toString().substring(0, 8);
        }

        String make = request.getParameter("make");
        String model = request.getParameter("model");
        int year;
        try {
            year = Integer.parseInt(request.getParameter("year"));
            if (year < 1900 || year > 2024) {
                throw new IllegalArgumentException("Year must be between 1900 and 2024");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid year format");
        }

        double price;
        try {
            price = Double.parseDouble(request.getParameter("price"));
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format");
        }

        LOGGER.info("Creating vehicle with ID: " + id + ", Make: " + make + ", Model: " + model + ", Year: " + year + ", Price: " + price);

        return switch (type) {
            case "Car" -> {
                try {
                    int numDoors = Integer.parseInt(request.getParameter("numDoors"));
                    if (numDoors < 2 || numDoors > 6) {
                        throw new IllegalArgumentException("Number of doors must be between 2 and 6");
                    }
                    LOGGER.info("Car details - Number of doors: " + numDoors);
                    yield new Car(id, make, model, year, price, numDoors);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number of doors format");
                }
            }
            case "Bike" -> {
                String engineCC = request.getParameter("engineCC");
                if (engineCC == null || !engineCC.matches("[0-9]+")) {
                    throw new IllegalArgumentException("Invalid engine capacity format");
                }
                LOGGER.info("Bike details - Engine capacity: " + engineCC);
                yield new Bike(id, make, model, year, price, engineCC);
            }
            case "Truck" -> {
                try {
                    double cargoCapacity = Double.parseDouble(request.getParameter("cargoCapacity"));
                    if (cargoCapacity <= 0) {
                        throw new IllegalArgumentException("Cargo capacity must be greater than 0");
                    }
                    LOGGER.info("Truck details - Cargo capacity: " + cargoCapacity);
                    yield new Truck(id, make, model, year, price, cargoCapacity);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid cargo capacity format");
                }
            }
            default -> throw new IllegalArgumentException("Invalid vehicle type: " + type);
        };
    }
}