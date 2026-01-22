<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container rental-container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-6">
            <div class="card rental-card rounded-4 shadow-sm border-0">
                <div class="card-header bg-primary text-white rounded-top-4">
                    <h3 class="mb-0 fw-semibold">
                        <c:choose>
                            <c:when test="${vehicle.available}">Rental Request</c:when>
                            <c:otherwise>Rental Waitlist Request</c:otherwise>
                        </c:choose>
                    </h3>
                </div>
                <div class="card-body px-4 py-4">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger mb-4">
                            <c:out value="${error}" />
                        </div>
                    </c:if>
                    
                    <c:if test="${not vehicle.available}">
                        <div class="alert alert-warning mb-4">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            This vehicle is currently rented. Your request will be added to the waitlist and you'll be notified when the vehicle becomes available.
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/rentals/request" method="post" class="needs-validation" novalidate>
                        <input type="hidden" name="vehicleId" value="${vehicle.id}">
                        <input type="hidden" name="dailyRate" value="${vehicle.dailyRate}">

                        <div class="mb-4">
                            <h4 class="fw-bold">${vehicle.make} ${vehicle.model} (${vehicle.year})</h4>
                            <p class="text-muted">Daily Rate: <span class="text-primary fw-medium">LKR${vehicle.dailyRate}/day</span></p>
                            <c:if test="${!vehicle.available}">
                                <p class="text-warning">
                                    <i class="bi bi-exclamation-triangle"></i>
                                    Currently Rented
                                </p>
                            </c:if>
                        </div>

                        <div class="row mb-4">
                            <div class="col-md-6 mb-3 mb-md-0">
                                <label for="startDate" class="form-label fw-medium">Start Date</label>
                                <input type="date" id="startDate" name="startDate"
                                       class="form-control rounded-3" required
                                       min="${java.time.LocalDate.now()}">
                                <div class="invalid-feedback">Please select a start date.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="endDate" class="form-label fw-medium">End Date</label>
                                <input type="date" id="endDate" name="endDate"
                                       class="form-control rounded-3" required>
                                <div class="invalid-feedback">Please select an end date.</div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-medium">Estimated Total</label>
                            <div class="total-display bg-light p-3 rounded-3 border-start border-4 border-primary">
                                <h5 id="totalAmount" class="mb-1 text-primary fw-bold">LKR0.00</h5>
                                <small class="text-muted" id="daysCount">0 days</small>
                            </div>
                        </div>

                        <div class="d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/vehicles/list" class="btn btn-outline-secondary rounded-pill px-4">Cancel</a>
                            <button type="submit" class="btn btn-primary rounded-pill px-4">
                                <c:choose>
                                    <c:when test="${vehicle.available}">Confirm Rental</c:when>
                                    <c:otherwise>Join Waitlist</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const startDate = document.getElementById('startDate');
        const endDate = document.getElementById('endDate');
        const totalAmount = document.getElementById('totalAmount');
        const daysCount = document.getElementById('daysCount');
        const dailyRate = parseFloat(document.querySelector('input[name="dailyRate"]').value);

        // Set minimum date for end date based on start date
        startDate.addEventListener('change', function() {
            endDate.min = startDate.value;
            if (endDate.value && endDate.value < startDate.value) {
                endDate.value = startDate.value;
            }
            calculateTotal();
        });

        function calculateTotal() {
            if (startDate.value && endDate.value) {
                const start = new Date(startDate.value);
                const end = new Date(endDate.value);
                const diffTime = Math.max(end - start, 0);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

                daysCount.textContent = diffDays + (diffDays === 1 ? ' day' : ' days');
                totalAmount.textContent = 'LKR' + (diffDays * dailyRate).toFixed(2);
            }
        }

        startDate.addEventListener('change', calculateTotal);
        endDate.addEventListener('change', calculateTotal);

        // Form validation
        const form = document.querySelector('form');
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
</script>

<style>
    .rental-container {
        background-color: #f8f9fa;
    }

    .rental-card {
        background-color: #ffffff;
    }

    .total-display {
        font-size: 1.1rem;
    }
</style>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
