<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1>Available Vehicles</h1>
    <a href="${pageContext.request.contextPath}/vehicles/add" class="btn btn-success">Add Vehicle</a>
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

  <div class="row">
    <c:forEach var="vehicle" items="${vehicles}">
      <div class="col-md-4 mb-4">
        <div class="card vehicle-card ${vehicle.available ? '' : 'border-secondary'}">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start">
              <h5 class="card-title">${vehicle.make} ${vehicle.model}</h5>
              <c:if test="${!vehicle.available}">
                <span class="badge bg-secondary">Currently Rented</span>
              </c:if>
            </div>
            <p class="card-text">
              Type: ${vehicle.type}<br>
              Year: ${vehicle.year}<br>
              Daily Rate: LKR${vehicle.dailyRate}<br>
              Status: <span class="text-${vehicle.available ? 'success' : 'secondary'}">${vehicle.available ? 'Available' : 'Not Available'}</span>
            </p>
            <div class="btn-group">
              <a href="${pageContext.request.contextPath}/vehicles/details?id=${vehicle.id}" class="btn btn-info btn-sm">Details</a>
              <c:choose>
                <c:when test="${vehicle.available}">
                  <a href="${pageContext.request.contextPath}/rentals/request?vehicleId=${vehicle.id}" 
                     class="btn btn-primary btn-sm">Rent Now</a>
                </c:when>
                <c:otherwise>
                  <a href="${pageContext.request.contextPath}/rentals/request?vehicleId=${vehicle.id}" 
                     class="btn btn-secondary btn-sm">Request Rental</a>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />