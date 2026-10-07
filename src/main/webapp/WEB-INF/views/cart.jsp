<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Your shopping cart - KamaleshwaranMart">
    <title>Shopping Cart – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <style>
        .cart-layout { display:grid; grid-template-columns:1fr 340px; gap:2rem; margin:2rem 0; }
        @media(max-width:900px){ .cart-layout{grid-template-columns:1fr;} }
        .cart-item { display:flex;gap:1rem;align-items:flex-start;padding:1.25rem;background:white;border-radius:var(--radius-lg);box-shadow:var(--shadow-sm);border:1px solid var(--gray-200);transition:box-shadow 0.2s; }
        .cart-item:hover { box-shadow:var(--shadow-md); }
        .cart-item-img { width:96px;height:96px;border-radius:var(--radius-md);object-fit:cover;flex-shrink:0; }
        .cart-item-info { flex:1;min-width:0; }
        .cart-item-name { font-weight:700;color:var(--gray-900);font-size:1rem;margin-bottom:0.25rem; }
        .cart-item-seller { font-size:0.8rem;color:var(--gray-500); }
        .cart-item-price { font-weight:700;color:var(--primary);font-size:1.1rem; }
        .cart-qty { display:flex;align-items:center;gap:0.5rem;margin-top:0.5rem; }
        .cart-qty-btn { width:2rem;height:2rem;border-radius:50%;border:2px solid var(--primary);background:transparent;color:var(--primary);font-size:1.1rem;cursor:pointer;font-weight:700; }
        .cart-qty-btn:hover { background:var(--primary);color:white; }
        .cart-qty-val { font-weight:700;min-width:2rem;text-align:center; }
        .cart-remove { color:var(--danger);background:none;border:none;cursor:pointer;font-size:0.85rem;margin-left:auto; }
        .cart-summary-card { position:sticky;top:5rem;background:white;border-radius:var(--radius-xl);box-shadow:var(--shadow-lg);border:1px solid var(--gray-200);padding:1.75rem; }
        .summary-row { display:flex;justify-content:space-between;align-items:center;padding:0.6rem 0;font-size:0.95rem; }
        .summary-row.total { font-size:1.3rem;font-weight:800;color:var(--gray-900);border-top:2px solid var(--gray-200);margin-top:0.5rem;padding-top:0.75rem; }
        .summary-row.total .price { color:var(--primary); }
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
            <li><a href="${pageContext.request.contextPath}/orders" class="nav-link">📦 Orders</a></li>
            <li><a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a></li>
        </ul>
    </div>
</nav>

<main>
    <div class="container">
        <h1 style="font-size:1.8rem;font-weight:800;color:var(--gray-900);margin:2rem 0 1.5rem">
            🛒 Shopping Cart
        </h1>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">✅ <c:out value="${successMessage}"/></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">⚠️ <c:out value="${errorMessage}"/></div>
        </c:if>

        <c:choose>
            <c:when test="${not empty cartItems}">
                <div class="cart-layout">
                    <!-- Cart Items -->
                    <div>
                        <div style="display:flex;flex-direction:column;gap:1rem" id="cartItemsList">
                            <c:forEach var="item" items="${cartItems}">
                                <div class="cart-item" data-cart-item-id="${item.id}">
                                    <img class="cart-item-img"
                                         src="${not empty item.productImage ? item.productImage : 'https://via.placeholder.com/200x200?text=Product'}"
                                         alt="<c:out value='${item.productName}'/>"
                                         onerror="this.src='https://via.placeholder.com/200x200?text=No+Image'">
                                    <div class="cart-item-info">
                                        <div class="cart-item-name"><c:out value="${item.productName}"/></div>
                                        <div class="cart-item-seller">Sold by <c:out value="${item.sellerName}"/></div>
                                        <div class="cart-item-price">
                                            ₹<fmt:formatNumber value="${item.price}" pattern="#,##,##0.00"/>
                                        </div>
                                        <div class="cart-qty">
                                            <button class="cart-qty-btn btn-cart-minus" data-item-id="${item.id}"
                                                    data-product-id="${item.productId}">−</button>
                                            <span class="cart-qty-val" id="qty-${item.id}">${item.quantity}</span>
                                            <button class="cart-qty-btn btn-cart-plus" data-item-id="${item.id}"
                                                    data-product-id="${item.productId}"
                                                    data-max="${item.stockQty}">+</button>
                                            <span style="font-size:0.8rem;color:var(--gray-400)">
                                                × ₹<fmt:formatNumber value="${item.price}" pattern="#,##,##0.00"/> each
                                            </span>
                                        </div>
                                    </div>
                                    <button class="cart-remove btn-cart-remove" data-item-id="${item.id}">🗑 Remove</button>
                                </div>
                            </c:forEach>
                        </div>

                        <div style="margin-top:1.5rem;display:flex;gap:1rem;flex-wrap:wrap">
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">← Continue Shopping</a>
                            <button id="clearCartBtn" class="btn" style="background:var(--danger);color:white">🗑 Clear Cart</button>
                        </div>
                    </div>

                    <!-- Order Summary -->
                    <div>
                        <div class="cart-summary-card">
                            <h2 style="font-weight:800;font-size:1.2rem;margin-bottom:1rem">Order Summary</h2>

                            <div class="summary-row">
                                <span>Subtotal (<span id="summaryCount">${fn:length(cartItems)}</span> items)</span>
                                <span id="summarySubtotal">₹<fmt:formatNumber value="${cartTotal}" pattern="#,##,##0.00"/></span>
                            </div>
                            <div class="summary-row">
                                <span>Shipping</span>
                                <span style="color:var(--success);font-weight:600">FREE</span>
                            </div>
                            <div class="summary-row">
                                <span>Taxes (18% GST)</span>
                                <span id="summaryTax">₹<fmt:formatNumber value="${cartTotal * 0.18}" pattern="#,##,##0.00"/></span>
                            </div>
                            <div class="summary-row total">
                                <span>Total</span>
                                <span class="price" id="summaryTotal">
                                    ₹<fmt:formatNumber value="${cartTotal * 1.18}" pattern="#,##,##0.00"/>
                                </span>
                            </div>

                            <form action="${pageContext.request.contextPath}/checkout" method="GET">
                                <button type="submit" class="btn btn-primary btn-block" 
                                        style="margin-top:1.25rem;font-size:1.05rem;padding:0.875rem">
                                    🔒 Proceed to Checkout
                                </button>
                            </form>

                            <div style="margin-top:1rem;text-align:center;font-size:0.78rem;color:var(--gray-400)">
                                🔐 Secure checkout · 💳 Multiple payment options
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state" style="margin-top:3rem">
                    <div class="empty-icon">🛒</div>
                    <h2 class="empty-title">Your cart is empty</h2>
                    <p class="empty-desc">Add some products to get started!</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">🛍 Start Shopping</a>
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
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
</body>
</html>
