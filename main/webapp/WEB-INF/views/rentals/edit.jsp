<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-lg-8 col-xl-6">
      <div class="card shadow-sm border-0 rounded-4">
        <div class="card-header bg-primary text-white rounded-top-4">
          <h4 class="mb-0 fw-semibold">Edit Rental</h4>
        </div>
        <div class="card-body px-4 py-4">
          <form action="${pageContext.request.contextPath}/rentals/edit" method="post">
            <input type="hidden" name="id" value="${rental.rentalId}" />
            <input type="hidden" id="dailyRate" name="dailyRate" value="${vehicle.dailyRate}" />

            <div class="mb-4">
              <label class="form-label fw-medium">Vehicle</label>
              <input type="text" class="form-control bg-light rounded-3"
                     value="${vehicle.make} ${vehicle.model} (${vehicle.year})" disabled />
            </div>

            <div class="row mb-4">
              <div class="col-md-6 mb-3 mb-md-0">
                <label class="form-label fw-medium">Start Date</label>
                <input type="date" id="startDate" name="startDate" class="form-control rounded-3"
                       value="${rental.startDate}" required />
              </div>
              <div class="col-md-6">
                <label class="form-label fw-medium">End Date</label>
                <input type="date" id="endDate" name="endDate" class="form-control rounded-3"
                       value="${rental.endDate}" required />
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
              <button type="submit" class="btn btn-success rounded-pill px-4">
                Update Rental
              </button>
              <a href="${pageContext.request.contextPath}/rentals/dashboard"
                 class="btn btn-outline-secondary rounded-pill px-4">Cancel</a>
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
    const rate = parseFloat(document.getElementById('dailyRate').value);

    function calculateTotal() {
      if (startDate.value && endDate.value) {
        const start = new Date(startDate.value);
        const end = new Date(endDate.value);
        const diffTime = Math.max(end - start, 0);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

        daysCount.textContent = diffDays + (diffDays === 1 ? ' day' : ' days');
        totalAmount.textContent = 'LKR' + (diffDays * rate).toFixed(2);
      }
    }

    startDate.addEventListener('change', calculateTotal);
    endDate.addEventListener('change', calculateTotal);
    // initialize
    calculateTotal();
  });
</script>

<style>
  .total-display { font-size: 1.1rem; }
</style>

<jsp:include page="../../../footer.jsp" />
