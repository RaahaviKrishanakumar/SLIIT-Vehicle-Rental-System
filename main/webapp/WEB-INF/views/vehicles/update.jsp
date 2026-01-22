<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container mt-4">
    <h2>Update Vehicle</h2>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/vehicles/update" method="post" class="needs-validation" novalidate>
        <input type="hidden" name="id" value="${vehicle.id}">
        <input type="hidden" name="type" value="${vehicle.type}">
        
        <div class="mb-3">
            <label for="make" class="form-label">Make</label>
            <input type="text" class="form-control" id="make" name="make" value="${vehicle.make}" required>
        </div>
        
        <div class="mb-3">
            <label for="model" class="form-label">Model</label>
            <input type="text" class="form-control" id="model" name="model" value="${vehicle.model}" required>
        </div>
        
        <div class="mb-3">
            <label for="year" class="form-label">Year</label>
            <input type="number" class="form-control" id="year" name="year" value="${vehicle.year}" 
                   min="1900" max="2024" required>
        </div>
        
        <div class="mb-3">
            <label for="price" class="form-label">Daily Rate (LKR)</label>
            <input type="number" class="form-control" id="price" name="price" 
                   value="${vehicle.dailyRate}" min="0" step="0.01" required>
        </div>
        
        <c:choose>
            <c:when test="${vehicle.type == 'Car'}">
                <div class="mb-3">
                    <label for="numDoors" class="form-label">Number of Doors</label>
                    <input type="number" class="form-control" id="numDoors" name="numDoors" 
                           value="${vehicle.numDoors}" min="2" max="6" required>
                </div>
            </c:when>
            <c:when test="${vehicle.type == 'Bike'}">
                <div class="mb-3">
                    <label for="engineCC" class="form-label">Engine Capacity (CC)</label>
                    <input type="text" class="form-control" id="engineCC" name="engineCC" 
                           value="${vehicle.engineCC}" pattern="[0-9]+" required>
                </div>
            </c:when>
            <c:when test="${vehicle.type == 'Truck'}">
                <div class="mb-3">
                    <label for="cargoCapacity" class="form-label">Cargo Capacity (tons)</label>
                    <input type="number" class="form-control" id="cargoCapacity" name="cargoCapacity" 
                           value="${vehicle.cargoCapacity}" min="0" step="0.1" required>
                </div>
            </c:when>
        </c:choose>
        
        <div class="mb-3">
            <div class="form-check">
                <input class="form-check-input" type="checkbox" id="available" name="available" 
                       ${vehicle.available ? 'checked' : ''}>
                <label class="form-check-label" for="available">
                    Available for Rent
                </label>
            </div>
        </div>

        <button type="submit" class="btn btn-primary">Update Vehicle</button>
        <a href="${pageContext.request.contextPath}/vehicles/details?id=${vehicle.id}" 
           class="btn btn-secondary">Cancel</a>
    </form>
</div>

<script>
// Form validation
(function () {
    'use strict'
    var forms = document.querySelectorAll('.needs-validation')
    Array.prototype.slice.call(forms)
        .forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
            }, false)
        })
})()
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" /> 