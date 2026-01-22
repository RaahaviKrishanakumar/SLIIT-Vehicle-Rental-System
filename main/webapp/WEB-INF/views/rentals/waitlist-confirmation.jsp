<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container confirmation-container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-6 text-center">
            <div class="confirmation-icon mb-4 animate-pop">
                <i class="bi bi-clock-history text-primary" style="font-size: 4rem;"></i>
            </div>

            <h1 class="mb-3 fw-bold text-primary">Added to Waitlist!</h1>
            <p class="lead text-muted mb-4">Your rental request has been added to the waitlist. We'll notify you when the vehicle becomes available.</p>

            <div class="confirmation-details card shadow-sm border-0 rounded-4 mb-4">
                <div class="card-body text-start p-4">
                    <h5 class="card-title mb-3">Waitlist Request #${waitlistRequest.requestId}</h5>
                    <hr>
                    <div class="mb-3">
                        <h6 class="fw-bold mb-2">Vehicle Details</h6>
                        <p class="mb-1">${vehicle.make} ${vehicle.model} (${vehicle.year})</p>
                        <p class="text-muted mb-0">Daily Rate: LKR${vehicle.dailyRate}</p>
                    </div>
                    <div class="mb-3">
                        <h6 class="fw-bold mb-2">Requested Dates</h6>
                        <p class="mb-1">From: ${waitlistRequest.desiredStartDate}</p>
                        <p class="mb-0">To: ${waitlistRequest.desiredEndDate}</p>
                    </div>
                    <div class="alert alert-info mb-0">
                        <i class="bi bi-info-circle me-2"></i>
                        You'll receive a notification when the vehicle becomes available for your requested dates.
                    </div>
                </div>
            </div>

            <div class="d-grid gap-3 col-md-10 col-lg-8 mx-auto">
                <a href="${pageContext.request.contextPath}/rentals/dashboard" class="btn btn-primary btn-lg rounded-pill">
                    View My Rentals
                </a>
                <a href="${pageContext.request.contextPath}/vehicles/list" class="btn btn-outline-primary btn-lg rounded-pill">
                    Browse Other Vehicles
                </a>
            </div>
        </div>
    </div>
</div>

<style>
    .confirmation-container {
        background-color: #f8f9fa;
    }
    
    .confirmation-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto;
        background: #fff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
    }
    
    .animate-pop {
        animation: pop 0.5s ease-out;
    }
    
    @keyframes pop {
        0% { transform: scale(0.8); opacity: 0; }
        100% { transform: scale(1); opacity: 1; }
    }
</style>

<jsp:include page="/WEB-INF/views/common/footer.jsp" /> 