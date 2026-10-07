<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="KamaleshwaranMart - Discover amazing products from multiple sellers across India">
    <title>KamaleshwaranMart – Shop Smart, Shop Fast</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
</head>
<body>
<script>window.CONTEXT_PATH = '${pageContext.request.contextPath}';</script>

<!-- Navigation -->
<nav class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/home" class="brand">
            <div class="brand-icon">🛒</div>
            <span class="brand-name">Kamaleshwaran<span>Mart</span></span>
        </a>

        <div class="nav-search">
            <form class="search-form" action="${pageContext.request.contextPath}/products" method="GET" id="searchForm">
                <input type="text" name="search" placeholder="Search products, brands, categories..." 
                       value="<c:out value='${param.search}'/>">
                <button type="submit">🔍 Search</button>
            </form>
        </div>

        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/products" class="nav-link">🏪 Products</a></li>
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <li>
                        <a href="${pageContext.request.contextPath}/cart" class="nav-link cart-badge-container">
                            🛒 Cart
                        </a>
                    </li>
                    <li class="user-menu">
                        <span class="user-badge badge-${fn:toLowerCase(sessionScope.currentUser.role)}">
                            ${sessionScope.currentUser.role}
                        </span>
                        <span style="font-weight:600;color:var(--gray-700)">
                            <c:out value="${sessionScope.currentUser.name}"/>
                        </span>
                        <c:if test="${sessionScope.currentUser.role == 'SELLER'}">
                            <a href="${pageContext.request.contextPath}/seller/dashboard" class="nav-link">📊 Dashboard</a>
                        </c:if>
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">⚙️ Admin</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm">Sign In</a></li>
                    <li><a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</nav>

<!-- Hero Banner -->
<section class="hero">
    <div class="container">
        <div class="hero-content">
            <div class="hero-text">
                <div class="hero-badge">🎉 Anna University R2025 Capstone Project</div>
                <h1 class="hero-title">Your One-Stop Multi-Seller Marketplace</h1>
                <p class="hero-subtitle">Discover thousands of products from verified sellers. 
                   Shop electronics, fashion, home goods, sports gear and more — all in one place.</p>
                <div class="hero-actions">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">🛍️ Shop Now</a>
                    <c:if test="${empty sessionScope.currentUser}">
                        <a href="${pageContext.request.contextPath}/register?role=SELLER" class="btn" 
                           style="background:rgba(255,255,255,0.15);color:white;border:2px solid rgba(255,255,255,0.4)">
                            🏪 Start Selling
                        </a>
                    </c:if>
                </div>
            </div>
            <div style="font-size:8rem;opacity:0.15;user-select:none">🛒</div>
        </div>
    </div>
</section>

<!-- Category Filter Bar -->
<div class="categories-bar">
    <div class="container">
        <div class="category-chips">
            <a href="${pageContext.request.contextPath}/products" class="chip ${empty param.category ? 'active' : ''}">All</a>
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/products?category=<c:out value='${cat}'/>" class="chip">
                    <c:out value="${cat}"/>
                </a>
            </c:forEach>
        </div>
    </div>
</div>

<!-- Featured Products -->
<main>
    <div class="container">
        <div style="display:flex;align-items:center;justify-content:space-between;margin:2rem 0 0.5rem;">
            <h2 style="font-size:1.75rem;font-weight:800;color:var(--gray-900)">⭐ Featured Products</h2>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary btn-sm">View All →</a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
        </c:if>

        <c:choose>
            <c:when test="${not empty featuredProducts}">
                <div class="products-grid">
                    <c:forEach var="product" items="${featuredProducts}" begin="0" end="7">
                        <div class="product-card">
                            <div class="product-image-container">
                                <a href="${pageContext.request.contextPath}/product-details?id=${product.id}">
                                    <img src="${not empty product.imageUrl ? product.imageUrl : 'https://via.placeholder.com/600x400?text=Product'}"
                                         alt="<c:out value='${product.name}'/>" class="product-image"
                                         loading="lazy" onerror="this.src='https://via.placeholder.com/600x400?text=No+Image'">
                                </a>
                                <span class="product-category-tag"><c:out value="${product.category}"/></span>
                            </div>
                            <div class="product-info">
                                <p class="product-seller">by <c:out value="${product.sellerName}"/></p>
                                <h3 class="product-title">
                                    <a href="${pageContext.request.contextPath}/product-details?id=${product.id}"
                                       style="color:inherit;text-decoration:none">
                                        <c:out value="${product.name}"/>
                                    </a>
                                </h3>
                                <div class="product-rating">
                                    ⭐ <fmt:formatNumber value="${product.averageRating}" maxFractionDigits="1"/>
                                    <span class="rating-count">(${product.reviewCount})</span>
                                </div>
                                <div class="product-price-row">
                                    <span class="product-price">
                                        ₹<fmt:formatNumber value="${product.price}" pattern="#,##,##0.00"/>
                                    </span>
                                    <c:choose>
                                        <c:when test="${product.stockQty == 0}">
                                            <span class="stock-indicator out-of-stock">Out of Stock</span>
                                        </c:when>
                                        <c:when test="${product.stockQty <= 5}">
                                            <span class="stock-indicator low-stock">Only ${product.stockQty} left!</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="stock-indicator in-stock">In Stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="card-actions">
                                    <c:if test="${product.stockQty > 0}">
                                        <button class="btn btn-primary btn-sm btn-add-cart"
                                                data-product-id="${product.id}">
                                            🛒 Add to Cart
                                        </button>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/product-details?id=${product.id}"
                                       class="btn btn-secondary btn-sm">View Details</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">🛍️</div>
                    <h3 class="empty-title">Products Coming Soon</h3>
                    <p class="empty-desc">Be the first seller to list amazing products on KamaleshwaranMart!</p>
                    <c:if test="${empty sessionScope.currentUser}">
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Join as Seller</a>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Seller CTA Section -->
        <div style="background:linear-gradient(135deg,#1e1b4b,#4338ca);border-radius:1.25rem;padding:2.5rem;margin:2rem 0;color:white;display:flex;align-items:center;justify-content:space-between;gap:1.5rem;flex-wrap:wrap">
            <div>
                <h2 style="font-size:1.75rem;font-weight:800;margin-bottom:0.5rem">Start Selling Today 🚀</h2>
                <p style="color:#c7d2fe;font-size:1rem">List your products and reach thousands of buyers across India.</p>
            </div>
            <c:if test="${empty sessionScope.currentUser}">
                <a href="${pageContext.request.contextPath}/register" class="btn" 
                   style="background:white;color:#4f46e5;font-weight:700;padding:0.75rem 1.75rem">
                    Register as Seller →
                </a>
            </c:if>
            <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.role == 'SELLER'}">
                <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn" 
                   style="background:white;color:#4f46e5;font-weight:700;padding:0.75rem 1.75rem">
                    Go to Seller Dashboard →
                </a>
            </c:if>
        </div>
    </div>
</main>

<!-- Footer -->
<footer class="footer">
    <div class="container">
        <div class="footer-content">
            <div class="footer-col">
                <h4>🛒 KamaleshwaranMart</h4>
                <p style="font-size:0.88rem;line-height:1.7">Multi-seller e-commerce marketplace. Anna University R2025 Semester 3 Java Capstone Project.</p>
            </div>
            <div class="footer-col">
                <h4>Shop</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/products">All Products</a></li>
                    <li><a href="${pageContext.request.contextPath}/products?category=Electronics">Electronics</a></li>
                    <li><a href="${pageContext.request.contextPath}/products?category=Fashion">Fashion</a></li>
                    <li><a href="${pageContext.request.contextPath}/products?category=Home+%26+Kitchen">Home & Kitchen</a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h4>Account</h4>
                <ul>
                    <c:if test="${empty sessionScope.currentUser}">
                        <li><a href="${pageContext.request.contextPath}/login">Sign In</a></li>
                        <li><a href="${pageContext.request.contextPath}/register">Register</a></li>
                    </c:if>
                    <c:if test="${not empty sessionScope.currentUser}">
                        <li><a href="${pageContext.request.contextPath}/orders">My Orders</a></li>
                        <li><a href="${pageContext.request.contextPath}/cart">Shopping Cart</a></li>
                        <li><a href="${pageContext.request.contextPath}/logout">Sign Out</a></li>
                    </c:if>
                </ul>
            </div>
            <div class="footer-col">
                <h4>System</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/api/v1/health">Health Check</a></li>
                </ul>
            </div>
        </div>
        <div class="footer-bottom">
            <p>© 2026 KamaleshwaranMart. Anna University R2025 Java Capstone Project by Kamaleshwaran.</p>
        </div>
    </div>
</footer>

<div id="toastContainer" class="toast-container"></div>
<script src="${pageContext.request.contextPath}/js/products.js"></script>
</body>
</html>
