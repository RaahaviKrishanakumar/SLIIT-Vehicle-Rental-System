<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Rental System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet" onerror="this.style.display='none'">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">Vehicle Rental</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/vehicles/list">Vehicles</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/rentals/dashboard">My Rentals</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/rentals/waitlist">
                        <i class="bi bi-clock-history"></i> Waitlist
                    </a>
                </li>
            </ul>
            <div class="d-flex">
                <c:choose>
                    <c:when test="${empty sessionScope.userId}">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-light me-2">Login</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Register</a>
                    </c:when>
                    <c:otherwise>
                            <span class="navbar-text me-3 text-light">
                                Welcome, ${sessionScope.userName}
                            </span>
                        <form action="${pageContext.request.contextPath}/logout" method="post" class="d-inline">
                            <button type="submit" class="btn btn-outline-light">Logout</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
<main class="container mt-4">
</main>
</body>
</html>