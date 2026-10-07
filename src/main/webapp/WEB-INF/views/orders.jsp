<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Orders – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .order-card { background:white;border-radius:var(--radius-lg);box-shadow:var(--shadow-sm);border:1px solid var(--gray-200);overflow:hidden;margin-bottom:1.25rem; }
        .order-header { display:flex;align-items:center;justify-content:space-between;padding:1rem 1.5rem;border-bottom:1px solid var(--gray-100);background:var(--gray-50);flex-wrap:wrap;gap:0.75rem; }
        .order-id { font-weight:700;color:var(--gray-900);font-family:monospace;font-size:0.95rem; }
        .order-date { font-size:0.82rem;color:var(--gray-500); }
        .order-status { padding:0.3rem 0.85rem;border-radius:50px;font-size:0.8rem;font-weight:700; }
        .status-PENDING { background:#fef3c7;color:#92400e; }
        .status-PROCESSING { background:#dbeafe;color:#1e40af; }
        .status-SHIPPED { background:#e0f2fe;color:#075985; }
        .status-DELIVERED { background:#d1fae5;color:#065f46; }
        .status-CANCELLED { background:#fee2e2;color:#991b1b; }
        .order-items { padding:1rem 1.5rem; }
        .order-item-row { display:flex;align-items:center;gap:1rem;padding:0.5rem 0;border-bottom:1px solid var(--gray-100); }
        .order-item-row:last-child { border-bottom:none; }
        .order-item-img { width:56px;height:56px;border-radius:var(--radius-sm);object-fit:cover; }
        .order-footer { padding:0.75rem 1.5rem;background:var(--gray-50);border-top:1px solid var(--gray-100);display:flex;align-items:center;justify-content:space-between; }
    </style>
</head>
<body>
<script>window.CONTEXT_PATH = '${pageContext.request.contextPath}';</script>

<nav class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/home" class="brand">
            <div class="brand-icon">🛒</div>
            <span class="brand-name">Kamaleshwaran<span>Mart</span></span>
        </a>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/products" class="nav-link">🏪 Products</a></li>
            <li><a href="${pageContext.request.contextPath}/cart" class="nav-link">🛒 Cart</a></li>
            <li><a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a></li>
        </ul>
    </div>
</nav>

<main>
    <div class="container">
        <h1 style="font-size:1.8rem;font-weight:800;color:var(--gray-900);margin:2rem 0 0.5rem">📦 My Orders</h1>
        <p style="color:var(--gray-500);margin-bottom:1.5rem">Track and manage all your orders</p>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">✅ <c:out value="${successMessage}"/></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">⚠️ <c:out value="${errorMessage}"/></div>
        </c:if>

        <c:choose>
            <c:when test="${not empty orders}">
                <c:forEach var="order" items="${orders}">
                    <div class="order-card">
                        <div class="order-header">
                            <div>
                                <div class="order-id">Order #<c:out value="${order.id}"/></div>
                                <div class="order-date">
                                    Placed on <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
                                </div>
                            </div>
                            <div style="display:flex;align-items:center;gap:0.75rem">
                                <span class="order-status status-${order.status}">
                                    <c:out value="${order.status}"/>
                                </span>
                                <span style="font-weight:800;color:var(--primary)">
                                    ₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##,##0.00"/>
                                </span>
                            </div>
                        </div>

                        <div class="order-items">
                            <c:forEach var="item" items="${order.items}">
                                <div class="order-item-row">
                                    <img class="order-item-img"
                                         src="https://via.placeholder.com/100x100?text=P"
                                         alt="Product">
                                    <div style="flex:1">
                                        <div style="font-weight:600;font-size:0.95rem"><c:out value="${item.productName}"/></div>
                                        <div style="font-size:0.82rem;color:var(--gray-500)">
                                            Qty: ${item.quantity} × ₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##,##0.00"/>
                                        </div>
                                    </div>
                                    <div style="font-weight:700;color:var(--gray-900)">
                                        ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##,##0.00"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <div class="order-footer">
                            <div style="font-size:0.82rem;color:var(--gray-500)">
                                📍 <c:out value="${order.shippingAddress}"/>
                            </div>
                            <c:if test="${order.status == 'PENDING'}">
                                <form action="${pageContext.request.contextPath}/orders" method="POST" style="margin:0">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <button type="submit" class="btn btn-sm"
                                            style="background:var(--danger);color:white"
                                            onclick="return confirm('Cancel this order?')">
                                        ✕ Cancel Order
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">📦</div>
                    <h2 class="empty-title">No Orders Yet</h2>
                    <p class="empty-desc">You haven't placed any orders. Start shopping to see your orders here.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">🛍 Shop Now</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<footer class="footer">
    <div class="container footer-bottom">
        <p>© 2026 KamaleshwaranMart – Anna University R2025 Java Capstone Project</p>
    </div>
</footer>
<div id="toastContainer" class="toast-container"></div>
</body>
</html>
