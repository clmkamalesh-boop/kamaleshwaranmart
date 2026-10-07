<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Sign in to KamaleshwaranMart - Your trusted multi-seller e-commerce marketplace">
    <title>Sign In – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<div class="auth-page">
    <div class="auth-card">
        <div class="auth-header">
            <a href="${pageContext.request.contextPath}/home" class="auth-brand">
                <div class="brand-icon">🛒</div>
                KamaleshwaranMart
            </a>
            <h1 class="auth-title">Welcome back!</h1>
            <p class="auth-subtitle">Sign in to access your account</p>
        </div>

        <c:if test="${not empty param.registered}">
            <div class="alert alert-success">
                ✅ Account created successfully! Please log in.
            </div>
        </c:if>

        <c:if test="${not empty param.logout}">
            <div class="alert alert-success">
                👋 You have been signed out successfully.
            </div>
        </c:if>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                🔒 Your session expired or you need to sign in to continue.
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                ⚠️ <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form id="authForm" action="${pageContext.request.contextPath}/login" method="POST">
            <div class="form-group">
                <label class="form-label" for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="you@example.com"
                       value="<c:out value='${email}'/>"
                       autocomplete="email" required autofocus>
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <div class="input-wrapper">
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Enter your password"
                           autocomplete="current-password" required
                           style="padding-right: 2.5rem">
                    <button type="button" class="toggle-password" data-target="password" title="Show/Hide password">
                        👁️
                    </button>
                </div>
            </div>

            <button type="submit" id="submitBtn" class="btn btn-primary btn-block btn-lg">
                Sign In
            </button>
        </form>

        <div class="auth-footer">
            Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one for free</a>
        </div>

        <div style="margin-top:1rem;text-align:center;font-size:0.78rem;color:var(--gray-400)">
            Admin access is by invitation only. Contact your system administrator.
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
