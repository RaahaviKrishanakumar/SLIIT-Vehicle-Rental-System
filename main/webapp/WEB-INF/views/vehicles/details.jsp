<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/vehicles/list">Vehicles</a></li>
            <li class="breadcrumb-item active">${vehicle.make} ${vehicle.model}</li>
        </ol>
    </nav>

    <div class="row">
        <div class="col-md-6">
            <div class="card mb-4">
                <div class="card-body">
                    <h2 class="card-title">${vehicle.make} ${vehicle.model}</h2>
                    <h3 class="text-muted">${vehicle.year}</h3>
                    <div class="vehicle-details mt-4">
                        <p><strong>Type:</strong> ${vehicle.type}</p>
                        <p><strong>Daily Rate:</strong> LKR${vehicle.dailyRate}</p>
                        <p><strong>Status:</strong> 
                            <span class="badge ${vehicle.available ? 'bg-success' : 'bg-danger'}">
                                ${vehicle.available ? 'Available' : 'Not Available'}
                            </span>
                        </p>
                        
                        <c:if test="${vehicle.type == 'Car'}">
                            <p><strong>Number of Seats:</strong> ${vehicle.seats}</p>
                        </c:if>
                        <c:if test="${vehicle.type == 'Bike'}">
                            <p><strong>Engine:</strong> ${vehicle.engineCC} CC</p>
                        </c:if>
                        <c:if test="${vehicle.type == 'Truck'}">
                            <p><strong>Cargo Capacity:</strong> ${vehicle.cargoCapacity} tons</p>
                        </c:if>
                    </div>

                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/vehicles/list" class="btn btn-secondary">Back to List</a>
                        <c:if test="${vehicle.available}">
                            <a href="${pageContext.request.contextPath}/rentals/request?vehicleId=${vehicle.id}" 
                               class="btn btn-primary">Rent Now</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/vehicles/update?id=${vehicle.id}" 
                           class="btn btn-warning">Edit</a>
                        <button type="button" class="btn btn-danger" data-bs-toggle="modal" 
                                data-bs-target="#deleteModal">Delete</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirm Delete</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete this vehicle?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form action="${pageContext.request.contextPath}/vehicles/delete" method="post">
                    <input type="hidden" name="id" value="${vehicle.id}">
                    <button type="submit" class="btn btn-danger">Delete</button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />