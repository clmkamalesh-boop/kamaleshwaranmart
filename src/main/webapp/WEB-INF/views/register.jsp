<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Create your KamaleshwaranMart account as a Buyer or Seller">
    <title>Create Account – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<div class="auth-page">
    <div class="auth-card" style="max-width:520px">
        <div class="auth-header">
            <a href="${pageContext.request.contextPath}/home" class="auth-brand">
                <div class="brand-icon">🛒</div>
                KamaleshwaranMart
            </a>
            <h1 class="auth-title">Create your account</h1>
            <p class="auth-subtitle">Join thousands of buyers and sellers</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                ⚠️ <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form id="authForm" action="${pageContext.request.contextPath}/register" method="POST">
            <div class="form-group">
                <label class="form-label" for="name">Full Name</label>
                <input type="text" id="name" name="name" class="form-control"
                       placeholder="Your full name"
                       value="<c:out value='${name}'/>"
                       required maxlength="100">
            </div>

            <div class="form-group">
                <label class="form-label" for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="you@example.com"
                       value="<c:out value='${email}'/>"
                       required autocomplete="email">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <div class="input-wrapper">
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Min. 8 chars, include uppercase & number"
                           required autocomplete="new-password"
                           style="padding-right: 2.5rem">
                    <button type="button" class="toggle-password" data-target="password" title="Show/Hide password">
                        👁️
                    </button>
                </div>
                <div class="strength-meter">
                    <div class="strength-bar" id="strengthBar"></div>
                </div>
                <div class="strength-text">
                    <span>Password strength</span>
                    <span id="strengthLabel"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">Confirm Password</label>
                <div class="input-wrapper">
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                           placeholder="Re-enter password"
                           required autocomplete="new-password"
                           style="padding-right: 2.5rem">
                    <button type="button" class="toggle-password" data-target="confirmPassword" title="Show/Hide password">
                        👁️
                    </button>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">I want to</label>
                <div class="role-options">
                    <input type="radio" id="roleBuyer" name="role" value="BUYER" class="role-radio"
                           ${(selectedRole == null || selectedRole == 'BUYER') ? 'checked' : ''}>
                    <label for="roleBuyer" class="role-label">
                        <span style="font-size:1.5rem">🛍️</span>
                        Shop as Buyer
                    </label>

                    <input type="radio" id="roleSeller" name="role" value="SELLER" class="role-radio"
                           ${selectedRole == 'SELLER' ? 'checked' : ''}>
                    <label for="roleSeller" class="role-label">
                        <span style="font-size:1.5rem">🏪</span>
                        Sell as Seller
                    </label>
                </div>
            </div>

            <button type="submit" id="submitBtn" class="btn btn-primary btn-block btn-lg">
                Create Account
            </button>
        </form>

        <div class="auth-footer">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in here</a>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
