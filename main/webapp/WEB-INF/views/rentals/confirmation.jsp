<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container confirmation-container py-5" style="background-color: #f8f9fa;">
    <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-6 text-center">
            <c:choose>
                <c:when test="${empty rental}">
                    <div class="alert alert-danger">
                        Rental details could not be found. Please try again or contact support.
                    </div>
                    <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-primary btn-lg rounded-pill">
                        Browse Vehicles
                    </a>
                </c:when>
                <c:otherwise>
                    <div class="confirmation-icon mb-4 animate-pop">
                        <span class="check-icon">✔</span>
                    </div>

                    <h1 class="mb-3 fw-bold text-primary">
                        <c:choose>
                            <c:when test="${edited}">Rental Updated!</c:when>
                            <c:otherwise>Rental Confirmed!</c:otherwise>
                        </c:choose>
                    </h1>
                    <p class="lead text-muted mb-4">
                        <c:choose>
                            <c:when test="${edited}">Your rental details have been successfully updated.</c:when>
                            <c:otherwise>Your rental has been successfully confirmed.</c:otherwise>
                        </c:choose>
                    </p>

                    <div class="confirmation-details card shadow-sm border-0 rounded-4 mb-4 px-4 py-3 bg-white border-start border-4 border-primary">
                        <div class="card-body text-start">
                            <h5 class="card-title mb-3">Rental #${rental.rentalId}</h5>
                            <hr>
                            <p><strong>Vehicle:</strong> ${vehicle.make} ${vehicle.model}</p>
                            <p><strong>Dates:</strong> ${rental.startDate} to ${rental.endDate}</p>
                            <p><strong>Total Cost:</strong> <span class="text-primary fw-semibold">LKR ${rental.totalCost}</span></p>
                        </div>
                    </div>

                    <div class="d-grid gap-3 col-md-10 col-lg-8 mx-auto">
                        <a href="${pageContext.request.contextPath}/rentals/dashboard" class="btn btn-primary btn-lg rounded-pill">
                            View My Rentals
                        </a>
                        <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-outline-primary btn-lg rounded-pill">
                            Browse Vehicles
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<style>
    .confirmation-container { padding: 5rem 1rem; }
    .confirmation-icon {
        width: 120px;
        height: 120px;
        border: 4px solid #0d6efd;
        border-radius: 50%;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem auto;
        background-color: #ffffff;
    }
    .check-icon { font-size: 64px; color: #0d6efd; }
    .animate-pop { animation: pop 0.5s ease-out; }
    @keyframes pop {
        0% { transform: scale(0.5); opacity: 0; }
        80% { transform: scale(1.1); opacity: 1; }
        100% { transform: scale(1); }
    }
</style>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />