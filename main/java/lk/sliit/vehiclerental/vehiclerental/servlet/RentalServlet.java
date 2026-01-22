package lk.sliit.vehiclerental.vehiclerental.servlet;

import lk.sliit.vehiclerental.vehiclerental.model.Rental;
import lk.sliit.vehiclerental.vehiclerental.model.Vehicle;
import lk.sliit.vehiclerental.vehiclerental.model.WaitlistRequest;
import lk.sliit.vehiclerental.vehiclerental.service.RentalService;
import lk.sliit.vehiclerental.vehiclerental.service.VehicleService;
import lk.sliit.vehiclerental.vehiclerental.service.WaitlistService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet(name = "RentalServlet", urlPatterns = {"/rentals/*"})
public class RentalServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RentalServlet.class.getName());
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutes in seconds
    private VehicleService vehicleService;
    private RentalService rentalService;
    private WaitlistService waitlistService;

    @Override
    public void init() throws ServletException {
        super.init();
        vehicleService = new VehicleService();
        rentalService = new RentalService(vehicleService);
        waitlistService = new WaitlistService();
    }

    private boolean requiresLogin(String action) {
        return false;
    }

    private boolean checkUserSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();

        if (action == null || action.equals("/")) {
            action = "/list";
        }

        try {
            if (requiresLogin(action) && !checkUserSession(request, response)) {
                return;
            }

            switch (action) {
                case "/list":
                    showRentalsList(request, response);
                    break;
                case "/request":
                    showRentalRequestForm(request, response);
                    break;
                case "/dashboard":
                    showActiveRentals(request, response);
                    break;
                case "/confirmation":
                    showConfirmationPage(request, response);
                    break;
                case "/waitlist":
                    showWaitlist(request, response);
                    break;
                case "/edit":
                    showEditForm(request, response);
                    break;
                case "/delete":
                    deleteRental(request, response);
                    break;
                case "/waitlist-confirmation":
                    showWaitlistConfirmation(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing GET request: " + action, e);
            request.setAttribute("error", "An error occurred while processing your request: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            if (path == null) {
                path = "/request";
            }

            if (requiresLogin(path) && !checkUserSession(request, response)) {
                if (path.equals("/request")) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("pendingRental", new RentalDetails(
                            request.getParameter("vehicleId"),
                            request.getParameter("startDate"),
                            request.getParameter("endDate")
                    ));
                }
                return;
            }

            switch (path) {
                case "/request":
                    processRentalRequest(request, response);
                    break;
                case "/edit":
                    processEditForm(request, response);
                    break;
                case "/end":
                    endRental(request, response);
                    break;
                case "/waitlist/cancel":
                    cancelWaitlistRequest(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing POST request", e);
            request.setAttribute("error", "An error occurred while processing your request: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private synchronized boolean updateVehicleAvailability(Vehicle vehicle, boolean available) {
        try {
            vehicle.setAvailable(available);
            vehicleService.saveVehiclesToFile();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating vehicle availability for vehicle: " + vehicle.getId(), e);
            return false;
        }
    }

    private Cookie createSecureCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }

    private String generateGuestId() {
        return "GUEST-" + UUID.randomUUID().toString();
    }

    private void processRentalRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String vehicleId = request.getParameter("vehicleId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            if (vehicleId == null || startDateStr == null || endDateStr == null) {
                LOGGER.warning("Missing required parameters: vehicleId, startDate, or endDate");
                request.setAttribute("error", "Missing required parameters.");
                showRentalRequestForm(request, response);
                return;
            }

            LocalDate startDate;
            LocalDate endDate;
            try {
                startDate = LocalDate.parse(startDateStr);
                endDate = LocalDate.parse(endDateStr);
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Invalid date format", e);
                request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD format.");
                showRentalRequestForm(request, response);
                return;
            }

            String guestId = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("guestId".equals(cookie.getName())) {
                        guestId = cookie.getValue();
                        break;
                    }
                }
            }

            HttpSession session = request.getSession(false);
            if (guestId == null && session != null) {
                guestId = (String) session.getAttribute("guestId");
            }

            if (guestId == null) {
                guestId = generateGuestId();
                Cookie guestIdCookie = createSecureCookie("guestId", guestId);
                response.addCookie(guestIdCookie);

                session = request.getSession(true);
                session.setAttribute("guestId", guestId);
            }

            if (startDate.isBefore(LocalDate.now())) {
                request.setAttribute("error", "Start date cannot be in the past.");
                showRentalRequestForm(request, response);
                return;
            }
            if (endDate.isBefore(startDate)) {
                request.setAttribute("error", "End date cannot be before start date.");
                showRentalRequestForm(request, response);
                return;
            }

            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                LOGGER.warning("Vehicle not found: " + vehicleId);
                request.setAttribute("error", "Vehicle not found.");
                response.sendRedirect(request.getContextPath() + "/vehicles/list");
                return;
            }

            synchronized (this) {
                // Double-check vehicle availability to avoid race conditions
                if (!vehicle.isAvailable()) {
                    String waitlistId = waitlistService.addToWaitlist(vehicleId, guestId, startDate, endDate);
                    if (waitlistId != null) {
                        response.sendRedirect(request.getContextPath() + "/rentals/waitlist-confirmation?id=" + waitlistId);
                    } else {
                        LOGGER.warning("Failed to add to waitlist for vehicle: " + vehicleId);
                        request.setAttribute("error", "Failed to add to waitlist. Please try again.");
                        showRentalRequestForm(request, response);
                    }
                    return;
                }

                String rentalId = rentalService.createRental(vehicleId, guestId, startDate, endDate);
                if (rentalId != null) {
                    if (updateVehicleAvailability(vehicle, false)) {
                        LOGGER.info("Rental created successfully: " + rentalId);
                        response.sendRedirect(request.getContextPath() + "/rentals/confirmation?id=" + rentalId);
                    } else {
                        rentalService.deleteRental(rentalId);
                        LOGGER.warning("Failed to update vehicle availability for rental: " + rentalId);
                        request.setAttribute("error", "Failed to update vehicle availability. Please try again.");
                        showRentalRequestForm(request, response);
                    }
                } else {
                    LOGGER.warning("Failed to create rental for vehicle: " + vehicleId);
                    request.setAttribute("error", "Failed to create rental. Please try again.");
                    showRentalRequestForm(request, response);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing rental request", e);
            request.setAttribute("error", "An error occurred while processing your request: " + e.getMessage());
            showRentalRequestForm(request, response);
        }
    }

    private static class RentalDetails implements java.io.Serializable {
        private final String vehicleId;
        private final String startDate;
        private final String endDate;

        public RentalDetails(String vehicleId, String startDate, String endDate) {
            this.vehicleId = vehicleId;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getVehicleId() { return vehicleId; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
    }

    private void showRentalRequestForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vehicleId = request.getParameter("vehicleId");
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle != null) {
            request.setAttribute("vehicle", vehicle);
            if (!vehicle.isAvailable()) {
                request.setAttribute("warning", "This vehicle is currently rented. You can still submit a request, but it will be subject to availability.");
            }
            request.getRequestDispatcher("/WEB-INF/views/rentals/request.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/vehicles/list?error=Vehicle not found");
        }
    }

    private void showActiveRentals(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String guestId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guestId".equals(cookie.getName())) {
                    guestId = cookie.getValue();
                    break;
                }
            }
        }

        if (guestId == null && session != null) {
            guestId = (String) session.getAttribute("guestId");
        }

        if (guestId == null) {
            guestId = generateGuestId();
            Cookie guestIdCookie = new Cookie("guestId", guestId);
            guestIdCookie.setMaxAge(30 * 24 * 60 * 60);
            guestIdCookie.setPath("/");
            response.addCookie(guestIdCookie);

            session = request.getSession(true);
            session.setAttribute("guestId", guestId);
        }

        List<Rental> rentals = rentalService.getRentalsByUserId(guestId);

        if (rentals.isEmpty()) {
            request.setAttribute("message", "You don't have any rentals yet.");
        }

        request.setAttribute("rentals", rentals);
        request.getRequestDispatcher("/WEB-INF/views/rentals/dashboard.jsp").forward(request, response);
    }

    private void showConfirmationPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rentalId = request.getParameter("id");
        if (rentalId == null || rentalId.trim().isEmpty()) {
            LOGGER.warning("Invalid rental ID in confirmation request");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Rental ID is required.");
            return;
        }

        Rental rental = rentalService.getRentalById(rentalId);
        if (rental != null) {
            request.setAttribute("rental", rental);
            request.setAttribute("vehicle", rental.getVehicle());
            String isEdit = request.getParameter("edit");
            request.setAttribute("edited", "true".equals(isEdit));
            request.getRequestDispatcher("/WEB-INF/views/rentals/confirmation.jsp").forward(request, response);
        } else {
            LOGGER.warning("Rental not found for ID: " + rentalId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Rental not found.");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rentalId = request.getParameter("id");
        Rental rental = rentalService.getRentalById(rentalId);
        if (rental != null) {
            request.setAttribute("rental", rental);
            request.setAttribute("vehicle", rental.getVehicle());
            request.getRequestDispatcher("/WEB-INF/views/rentals/edit.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void processEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rentalId = request.getParameter("id");
        LocalDate newStartDate = LocalDate.parse(request.getParameter("startDate"));
        LocalDate newEndDate = LocalDate.parse(request.getParameter("endDate"));

        if (rentalService.editRental(rentalId, newStartDate, newEndDate)) {
            response.sendRedirect(request.getContextPath() + "/rentals/confirmation?id=" + rentalId + "&edit=true");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to update rental.");
        }
    }

    private void deleteRental(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String rentalId = request.getParameter("id");
        Rental rental = rentalService.getRentalById(rentalId);
        if (rental != null) {
            rentalService.deleteRental(rentalId);
        }
        response.sendRedirect(request.getContextPath() + "/rentals/dashboard");
    }

    private void endRental(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rentalId = request.getParameter("id");
        try {
            if (rentalId == null || rentalId.trim().isEmpty()) {
                throw new IllegalArgumentException("Rental ID is required");
            }

            Rental rental = rentalService.getRentalById(rentalId);
            if (rental == null) {
                throw new IllegalArgumentException("Rental not found");
            }

            Vehicle vehicle = rental.getVehicle();
            if (vehicle != null) {
                vehicle.setAvailable(true);
                vehicleService.saveVehiclesToFile();
            }

            rentalService.endRental(rentalId);
            response.sendRedirect(request.getContextPath() + "/rentals/dashboard?message=Rental ended successfully");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error ending rental", e);
            response.sendRedirect(request.getContextPath() + "/rentals/dashboard?error=" + e.getMessage());
        }
    }

    private void showWaitlistConfirmation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String waitlistId = request.getParameter("id");
        WaitlistRequest waitlistRequest = waitlistService.getWaitlistRequest(waitlistId);
        if (waitlistRequest != null) {
            Vehicle vehicle = vehicleService.getVehicleById(waitlistRequest.getVehicleId());
            request.setAttribute("waitlistRequest", waitlistRequest);
            request.setAttribute("vehicle", vehicle);
            request.getRequestDispatcher("/WEB-INF/views/rentals/waitlist-confirmation.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Waitlist request not found.");
        }
    }

    private void showWaitlist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String status = request.getParameter("status");
        String guestId = getGuestId(request, response);
        List<WaitlistRequest> waitlistRequests = waitlistService.getWaitlistByGuestId(guestId);

        if (status != null && !status.equals("all")) {
            waitlistRequests = waitlistRequests.stream()
                    .filter(req -> req.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        for (WaitlistRequest waitlistRequest : waitlistRequests) {
            Vehicle vehicle = vehicleService.getVehicleById(waitlistRequest.getVehicleId());
            request.setAttribute("vehicle_" + waitlistRequest.getRequestId(), vehicle);
        }

        request.setAttribute("waitlistRequests", waitlistRequests);
        request.getRequestDispatcher("/WEB-INF/views/rentals/waitlist.jsp").forward(request, response);
    }

    private void cancelWaitlistRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestId = request.getParameter("requestId");
        try {
            WaitlistRequest waitlistRequest = waitlistService.getWaitlistRequest(requestId);
            if (waitlistRequest != null) {
                waitlistService.updateRequestStatus(requestId, "CANCELLED");
                response.sendRedirect(request.getContextPath() + "/rentals/waitlist?message=Request cancelled successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/rentals/waitlist?error=Request not found");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling waitlist request", e);
            response.sendRedirect(request.getContextPath() + "/rentals/waitlist?error=Failed to cancel request");
        }
    }

    private String getGuestId(HttpServletRequest request, HttpServletResponse response) {
        String guestId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guestId".equals(cookie.getName())) {
                    guestId = cookie.getValue();
                    break;
                }
            }
        }

        HttpSession session = request.getSession(false);
        if (guestId == null && session != null) {
            guestId = (String) session.getAttribute("guestId");
        }

        if (guestId == null) {
            guestId = generateGuestId();
            Cookie guestIdCookie = createSecureCookie("guestId", guestId);
            response.addCookie(guestIdCookie);

            session = request.getSession(true);
            session.setAttribute("guestId", guestId);
        }

        return guestId;
    }

    private void showRentalsList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Rental> rentals = rentalService.getAllRentals();
        request.setAttribute("rentals", rentals);
        request.getRequestDispatcher("/WEB-INF/views/rentals/list.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        try {
            vehicleService.saveVehiclesToFile();
            vehicleService = null;
            rentalService = null;
            waitlistService = null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during servlet destruction", e);
        }
        super.destroy();
    }
}