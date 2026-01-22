<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>My Rentals</h1>
        <a href="${pageContext.request.contextPath}/vehicles/list" class="btn btn-primary">Browse Vehicles</a>
    </div>

    <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <c:out value="${param.error}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <c:out value="${param.message}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${empty rentals}">
        <div class="alert alert-info">
            You don't have any active rentals.
            <a href="${pageContext.request.contextPath}/vehicles/list" class="alert-link">Browse available vehicles</a>
        </div>
    </c:if>

    <div class="row">
        <c:forEach var="rental" items="${rentals}">
            <div class="col-md-6 mb-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${rental.vehicle.make} ${rental.vehicle.model}</h5>
                        <div class="badge bg-${rental.active ? 'success' : 'secondary'} mb-3">
                            ${rental.active ? 'Active' : 'Completed'}
                        </div>
                        <p class="card-text">
                            <strong>Rental ID:</strong> ${rental.rentalId}<br>
                            <strong>Start Date:</strong> ${rental.startDate}<br>
                            <strong>End Date:</strong> ${rental.endDate}<br>
                            <strong>Daily Rate:</strong> LKR${rental.vehicle.dailyRate}<br>
                            <strong>Total Cost:</strong> LKR${rental.totalCost}
                        </p>
                        <div class="btn-group">
                            <c:if test="${rental.active}">
                                <a href="${pageContext.request.contextPath}/rentals/edit?id=${rental.rentalId}" 
                                   class="btn btn-warning btn-sm">Edit</a>
                                <button type="button" class="btn btn-danger btn-sm" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#endRentalModal${rental.rentalId}">
                                    End Rental
                                </button>
                            </c:if>
                            <c:if test="${!rental.active}">
                                <span class="text-muted">Rental completed</span>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

            <!-- End Rental Modal -->
            <div class="modal fade" id="endRentalModal${rental.rentalId}" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">End Rental</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            Are you sure you want to end this rental?<br>
                            Vehicle: ${rental.vehicle.make} ${rental.vehicle.model}<br>
                            Rental ID: ${rental.rentalId}
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <form action="${pageContext.request.contextPath}/rentals/end" method="post">
                                <input type="hidden" name="id" value="${rental.rentalId}">
                                <button type="submit" class="btn btn-danger">End Rental</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />