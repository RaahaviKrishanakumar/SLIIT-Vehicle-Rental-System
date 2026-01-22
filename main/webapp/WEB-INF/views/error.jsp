<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8 text-center">
            <div class="error-icon mb-4">
                <i class="bi bi-exclamation-triangle text-danger" style="font-size: 4rem;"></i>
            </div>
            
            <h1 class="mb-4">Oops! Something went wrong</h1>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger mb-4">
                    <c:out value="${error}" />
                </div>
            </c:if>
            
            <p class="lead text-muted mb-4">
                We apologize for the inconvenience. Please try again or contact support if the problem persists.
            </p>
            
            <div class="d-flex justify-content-center gap-3">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                    Go to Homepage
                </a>
                <a href="javascript:history.back()" class="btn btn-outline-secondary">
                    Go Back
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" /> 