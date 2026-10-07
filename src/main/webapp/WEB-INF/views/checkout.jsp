<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Checkout - KamaleshwaranMart">
    <title>Checkout – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <style>
        .checkout-layout { display:grid;grid-template-columns:1fr 340px;gap:2rem;margin:2rem 0; }
        @media(max-width:900px){ .checkout-layout{grid-template-columns:1fr;} }
        .step-badge { display:inline-flex;align-items:center;justify-content:center;width:2rem;height:2rem;border-radius:50%;background:var(--primary);color:white;font-weight:700;font-size:0.85rem;margin-right:0.5rem; }
        .order-line { display:flex;align-items:center;gap:0.75rem;padding:0.75rem 0;border-bottom:1px solid var(--gray-100); }
        .order-line:last-child { border-bottom:none; }
        .order-line-img { width:56px;height:56px;border-radius:var(--radius-sm);object-fit:cover; }
        .payment-option { display:flex;align-items:center;gap:0.75rem;padding:1rem;border:2px solid var(--gray-200);border-radius:var(--radius-md);cursor:pointer;transition:all 0.2s; }
        .payment-option:has(input:checked) { border-color:var(--primary);background:var(--primary-light); }
        .payment-option input[type=radio] { accent-color:var(--primary); }
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
            <li><a href="${pageContext.request.contextPath}/cart" class="nav-link">← Back to Cart</a></li>
        </ul>
    </div>
</nav>

<main>
    <div class="container">
        <h1 style="font-size:1.8rem;font-weight:800;color:var(--gray-900);margin:2rem 0 1.5rem">
            🔒 Secure Checkout
        </h1>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">⚠️ <c:out value="${errorMessage}"/></div>
        </c:if>

        <div class="checkout-layout">
            <!-- Left: Form -->
            <div>
                <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout" method="POST">
                    <!-- Shipping Address -->
                    <div class="card" style="margin-bottom:1.5rem">
                        <div class="card-body">
                            <h2 style="font-weight:800;font-size:1.1rem;margin-bottom:1.25rem">
                                <span class="step-badge">1</span> Shipping Address
                            </h2>
                            <div class="form-group">
                                <label class="form-label" for="shippingName">Full Name</label>
                                <input type="text" id="shippingName" name="shippingName" class="form-control"
                                       value="<c:out value='${sessionScope.currentUser.name}'/>" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="shippingAddress">Street Address</label>
                                <input type="text" id="shippingAddress" name="shippingAddress" class="form-control"
                                       placeholder="House no., street, area" required>
                            </div>
                            <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem">
                                <div class="form-group">
                                    <label class="form-label" for="shippingCity">City</label>
                                    <input type="text" id="shippingCity" name="shippingCity" class="form-control"
                                           placeholder="Chennai" required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label" for="shippingPincode">PIN Code</label>
                                    <input type="text" id="shippingPincode" name="shippingPincode" class="form-control"
                                           placeholder="600001" pattern="\d{6}" maxlength="6" required>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="shippingPhone">Mobile Number</label>
                                <input type="tel" id="shippingPhone" name="shippingPhone" class="form-control"
                                       placeholder="10-digit mobile number" pattern="\d{10}" maxlength="10" required>
                            </div>
                        </div>
                    </div>

                    <!-- Payment Method -->
                    <div class="card" style="margin-bottom:1.5rem">
                        <div class="card-body">
                            <h2 style="font-weight:800;font-size:1.1rem;margin-bottom:1.25rem">
                                <span class="step-badge">2</span> Payment Method
                            </h2>

                            <div style="display:flex;flex-direction:column;gap:0.75rem">
                                <label class="payment-option">
                                    <input type="radio" name="paymentMethod" value="COD" checked>
                                    <span style="font-size:1.5rem">💵</span>
                                    <div>
                                        <div style="font-weight:600">Cash on Delivery</div>
                                        <div style="font-size:0.8rem;color:var(--gray-500)">Pay when your order arrives</div>
                                    </div>
                                </label>

                                <label class="payment-option">
                                    <input type="radio" name="paymentMethod" value="UPI">
                                    <span style="font-size:1.5rem">📱</span>
                                    <div>
                                        <div style="font-weight:600">UPI (Simulated)</div>
                                        <div style="font-size:0.8rem;color:var(--gray-500)">GPay, PhonePe, Paytm</div>
                                    </div>
                                </label>

                                <label class="payment-option">
                                    <input type="radio" name="paymentMethod" value="CARD">
                                    <span style="font-size:1.5rem">💳</span>
                                    <div>
                                        <div style="font-weight:600">Credit / Debit Card (Simulated)</div>
                                        <div style="font-size:0.8rem;color:var(--gray-500)">Visa, MasterCard, RuPay</div>
                                    </div>
                                </label>
                            </div>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-lg" id="placeOrderBtn">
                        ✅ Place Order
                    </button>
                    <p style="text-align:center;font-size:0.8rem;color:var(--gray-400);margin-top:0.75rem">
                        By placing the order, you agree to our <a href="#">Terms & Conditions</a>.
                    </p>
                </form>
            </div>

            <!-- Right: Order Summary -->
            <div>
                <div class="cart-summary-card" style="position:sticky;top:5rem;background:white;border-radius:var(--radius-xl);box-shadow:var(--shadow-lg);border:1px solid var(--gray-200);padding:1.75rem">
                    <h2 style="font-weight:800;font-size:1.1rem;margin-bottom:1rem">
                        <span class="step-badge">3</span> Your Order
                    </h2>

                    <div style="max-height:280px;overflow-y:auto">
                        <c:forEach var="item" items="${cartItems}">
                            <div class="order-line">
                                <img class="order-line-img"
                                     src="${not empty item.productImage ? item.productImage : 'https://via.placeholder.com/100x100?text=P'}"
                                     alt="<c:out value='${item.productName}'/>"
                                     onerror="this.src='https://via.placeholder.com/100x100?text=P'">
                                <div style="flex:1;min-width:0">
                                    <div style="font-weight:600;font-size:0.88rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">
                                        <c:out value="${item.productName}"/>
                                    </div>
                                    <div style="font-size:0.8rem;color:var(--gray-500)">Qty: ${item.quantity}</div>
                                </div>
                                <div style="font-weight:700;color:var(--primary);font-size:0.95rem;white-space:nowrap">
                                    ₹<fmt:formatNumber value="${item.price * item.quantity}" pattern="#,##,##0.00"/>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div style="margin-top:0.75rem;padding-top:0.75rem;border-top:1px solid var(--gray-200)">
                        <div style="display:flex;justify-content:space-between;font-size:0.9rem;margin-bottom:0.4rem">
                            <span>Subtotal</span><span>₹<fmt:formatNumber value="${cartTotal}" pattern="#,##,##0.00"/></span>
                        </div>
                        <div style="display:flex;justify-content:space-between;font-size:0.9rem;margin-bottom:0.4rem">
                            <span>Shipping</span><span style="color:var(--success)">FREE</span>
                        </div>
                        <div style="display:flex;justify-content:space-between;font-size:0.9rem;margin-bottom:0.4rem">
                            <span>GST (18%)</span><span>₹<fmt:formatNumber value="${cartTotal * 0.18}" pattern="#,##,##0.00"/></span>
                        </div>
                        <div style="display:flex;justify-content:space-between;font-size:1.25rem;font-weight:800;margin-top:0.5rem;padding-top:0.5rem;border-top:2px solid var(--gray-200)">
                            <span>Total</span>
                            <span style="color:var(--primary)">₹<fmt:formatNumber value="${cartTotal * 1.18}" pattern="#,##,##0.00"/></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<div id="toastContainer" class="toast-container"></div>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
</body>
</html>
