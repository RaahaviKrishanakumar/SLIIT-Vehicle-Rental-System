<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container">
    <div class="jumbotron text-center my-5">
        <h1 class="display-4">Welcome to Vehicle Rental</h1>
        <p class="lead">Find your perfect vehicle today</p>
        <div class="action-buttons mt-4">
            <a href="${pageContext.request.contextPath}/vehicles/list" class="btn btn-primary btn-lg">Browse Vehicles</a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-lg">Login</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />