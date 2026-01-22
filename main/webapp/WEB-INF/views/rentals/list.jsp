<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container py-5">
    <div class="row mb-4">
        <div class="col">
            <h2 class="mb-0">Available Vehicles for Rent</h2>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li class="breadcrumb-item active">Rentals</li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row g-4">
        <c:forEach var="vehicle" items="${vehicles}">
            <div class="col-md-6 col-lg-4">
                <div class="card h-100 shadow-sm border-0 rounded-4">
                    <div class="card-body">
                        <h5 class="card-title mb-3">${vehicle.make} ${vehicle.model}</h5>
                        <p class="card-text">
                            <span class="badge bg-primary mb-2">${vehicle.type}</span>
                            <br>
                            Year: ${vehicle.year}
                            <br>
                            Daily Rate: <strong>LKR${vehicle.dailyRate}</strong>
                        </p>
                        <div class="d-grid">
                            <a href="${pageContext.request.contextPath}/rentals/request?vehicleId=${vehicle.id}" 
                               class="btn btn-primary rounded-pill">
                                Rent Now
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty vehicles}">
            <div class="col-12">
                <div class="alert alert-info text-center">
                    <h4 class="alert-heading">No Vehicles Available</h4>
                    <p>Sorry, there are no vehicles available for rent at the moment. Please check back later.</p>
                </div>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" /> 