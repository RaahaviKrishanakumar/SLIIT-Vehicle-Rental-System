<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/vehicles/list">Vehicles</a></li>
            <li class="breadcrumb-item active">Add New Vehicle</li>
        </ol>
    </nav>

    <div class="card">
        <div class="card-header">
            <h2>Add New Vehicle</h2>
        </div>
        <div class="card-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${error}" />
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/vehicles/add" method="post" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="type" class="form-label">Vehicle Type</label>
                    <select name="type" id="type" class="form-select" required>
                        <option value="">Select a type</option>
                        <option value="Car">Car</option>
                        <option value="Bike">Bike</option>
                        <option value="Truck">Truck</option>
                    </select>
                    <div class="invalid-feedback">Please select a vehicle type.</div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="make" class="form-label">Make</label>
                        <input type="text" name="make" id="make" class="form-control" required>
                        <div class="invalid-feedback">Please enter the make.</div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="model" class="form-label">Model</label>
                        <input type="text" name="model" id="model" class="form-control" required>
                        <div class="invalid-feedback">Please enter the model.</div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="year" class="form-label">Year</label>
                        <input type="number" name="year" id="year" class="form-control" 
                               min="1900" max="2024" required
                               value="2024">
                        <div class="invalid-feedback">Please enter a valid year between 1900 and 2024.</div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="price" class="form-label">Daily Rate (LKR)</label>
                        <input type="number" name="price" id="price" class="form-control" 
                               min="0" step="0.01" required>
                        <div class="invalid-feedback">Please enter a valid daily rate.</div>
                    </div>
                </div>

                <!-- Car-specific fields -->
                <div id="carFields" class="vehicle-fields" style="display: none;">
                    <div class="mb-3">
                        <label for="numDoors" class="form-label">Number of Doors</label>
                        <input type="number" name="numDoors" id="numDoors" class="form-control vehicle-specific-field" 
                               min="2" max="6">
                        <div class="invalid-feedback">Please enter a valid number of doors (2-6).</div>
                    </div>
                </div>

                <!-- Bike-specific fields -->
                <div id="bikeFields" class="vehicle-fields" style="display: none;">
                    <div class="mb-3">
                        <label for="engineCC" class="form-label">Engine Capacity (CC)</label>
                        <input type="text" name="engineCC" id="engineCC" class="form-control vehicle-specific-field"
                               pattern="[0-9]+" placeholder="e.g. 600">
                        <div class="invalid-feedback">Please enter a valid engine capacity.</div>
                    </div>
                </div>

                <!-- Truck-specific fields -->
                <div id="truckFields" class="vehicle-fields" style="display: none;">
                    <div class="mb-3">
                        <label for="cargoCapacity" class="form-label">Cargo Capacity (tons)</label>
                        <input type="number" name="cargoCapacity" id="cargoCapacity" 
                               class="form-control vehicle-specific-field"
                               min="0.1" step="0.1">
                        <div class="invalid-feedback">Please enter a valid cargo capacity.</div>
                    </div>
                </div>

                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">Add Vehicle</button>
                    <a href="${pageContext.request.contextPath}/vehicles/list" 
                       class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.getElementById('type').addEventListener('change', function() {
    // Hide all vehicle-specific fields
    document.querySelectorAll('.vehicle-fields').forEach(div => {
        div.style.display = 'none';
        // Remove required attribute from all vehicle-specific fields
        div.querySelector('.vehicle-specific-field').required = false;
    });
    
    // Show fields based on selected vehicle type
    const selectedType = this.value;
    const specificFields = document.getElementById(selectedType.toLowerCase() + 'Fields');
    if (specificFields) {
        specificFields.style.display = 'block';
        // Make the visible field required
        specificFields.querySelector('.vehicle-specific-field').required = true;
    }
});

// Form validation
(function () {
    'use strict'
    var forms = document.querySelectorAll('.needs-validation')
    Array.prototype.slice.call(forms).forEach(function (form) {
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