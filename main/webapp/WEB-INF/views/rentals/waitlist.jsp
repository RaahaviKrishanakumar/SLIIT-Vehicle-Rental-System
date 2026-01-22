<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container py-5">
    <div class="row mb-4">
        <div class="col">
            <h2 class="mb-0">Rental Waitlist</h2>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/rentals/dashboard">Rentals</a></li>
                    <li class="breadcrumb-item active">Waitlist</li>
                </ol>
            </nav>
        </div>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${not empty message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <div class="row mb-4">
                <div class="col-md-6">
                    <form class="d-flex gap-2" action="${pageContext.request.contextPath}/rentals/waitlist" method="get">
                        <select name="status" class="form-select" onchange="this.form.submit()">
                            <option value="all" ${param.status == 'all' ? 'selected' : ''}>All Requests</option>
                            <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="FULFILLED" ${param.status == 'FULFILLED' ? 'selected' : ''}>Fulfilled</option>
                            <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                        </select>
                        <button type="submit" class="btn btn-primary">Filter</button>
                    </form>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty waitlistRequests}">
                    <div class="text-center py-5">
                        <i class="bi bi-clipboard-check text-muted" style="font-size: 3rem;"></i>
                        <h4 class="mt-3">No Waitlist Requests</h4>
                        <p class="text-muted">There are currently no requests in the waitlist.</p>
                        <a href="${pageContext.request.contextPath}/vehicles/list" class="btn btn-primary">
                            Browse Vehicles
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                            <tr>
                                <th>Request ID</th>
                                <th>Vehicle</th>
                                <th>Requested Dates</th>
                                <th>Request Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="request" items="${waitlistRequests}">
                                <c:set var="vehicle" value="${requestScope['vehicle_'.concat(request.requestId)]}" />
                                <tr>
                                    <td>
                                        <span class="text-primary fw-medium">${request.requestId}</span>
                                    </td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div>
                                                <div class="fw-medium">${vehicle.make} ${vehicle.model}</div>
                                                <small class="text-muted">${vehicle.year}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div>${request.desiredStartDate}</div>
                                        <small class="text-muted">to ${request.desiredEndDate}</small>
                                    </td>
                                    <td>
                                        <div>${request.requestDate}</div>
                                    </td>
                                    <td>
                                            <span class="badge bg-${request.status == 'PENDING' ? 'warning' :
                                                                    request.status == 'FULFILLED' ? 'success' : 'secondary'}">
                                                    ${request.status}
                                            </span>
                                    </td>
                                    <td>
                                        <c:if test="${request.status == 'PENDING'}">
                                            <button type="button" class="btn btn-sm btn-outline-danger"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#cancelModal${request.requestId}">
                                                Cancel
                                            </button>
                                        </c:if>
                                    </td>
                                </tr>

                                <!-- Cancel Modal -->
                                <div class="modal fade" id="cancelModal${request.requestId}" tabindex="-1">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title">Cancel Waitlist Request</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                            </div>
                                            <div class="modal-body">
                                                <p>Are you sure you want to cancel this waitlist request?</p>
                                                <div class="alert alert-warning">
                                                    <i class="bi bi-exclamation-triangle me-2"></i>
                                                    This action cannot be undone.
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                    Keep Request
                                                </button>
                                                <form action="${pageContext.request.contextPath}/rentals/waitlist/cancel"
                                                      method="post" style="display: inline;">
                                                    <input type="hidden" name="requestId" value="${request.requestId}">
                                                    <button type="submit" class="btn btn-danger">
                                                        Cancel Request
                                                    </button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />