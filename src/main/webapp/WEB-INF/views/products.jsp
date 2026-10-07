<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Browse all products on KamaleshwaranMart - filter by category, search and sort">
    <title>Products – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
</head>
<body>
<script>window.CONTEXT_PATH = '${pageContext.request.contextPath}';</script>

<!-- Navbar -->
<nav class="navbar">
    <div class="container nav-container">
        <a href="${pageContext.request.contextPath}/home" class="brand">
            <div class="brand-icon">🛒</div>
            <span class="brand-name">Kamaleshwaran<span>Mart</span></span>
        </a>
        <div class="nav-search">
            <form class="search-form" action="${pageContext.request.contextPath}/products" method="GET" id="searchForm">
                <input type="text" name="search" placeholder="Search products..." 
                       value="<c:out value='${searchQuery}'/>">
                <button type="submit">🔍</button>
            </form>
        </div>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/home" class="nav-link">🏠 Home</a></li>
            <c:if test="${not empty sessionScope.currentUser}">
                <li>
                    <a href="${pageContext.request.contextPath}/cart" class="nav-link cart-badge-container">
                        🛒 Cart
                    </a>
                </li>
                <li><a href="${pageContext.request.contextPath}/orders" class="nav-link">📦 Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a></li>
            </c:if>
            <c:if test="${empty sessionScope.currentUser}">
                <li><a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm">Sign In</a></li>
                <li><a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a></li>
            </c:if>
        </ul>
    </div>
</nav>

<!-- Category Chips -->
<div class="categories-bar">
    <div class="container">
        <div class="category-chips">
            <a href="${pageContext.request.contextPath}/products" 
               class="chip ${empty selectedCategory ? 'active' : ''}">All Products</a>
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/products?category=${fn:escapeXml(cat)}" 
                   class="chip ${selectedCategory == cat ? 'active' : ''}">
                    <c:out value="${cat}"/>
                </a>
            </c:forEach>
        </div>
    </div>
</div>

<main>
    <div class="container">
        <!-- Toolbar -->
        <div style="display:flex;align-items:center;justify-content:space-between;margin:1.5rem 0;flex-wrap:wrap;gap:1rem">
            <div>
                <h1 style="font-size:1.6rem;font-weight:800;color:var(--gray-900)">
                    <c:choose>
                        <c:when test="${not empty searchQuery}">Search: "<c:out value="${searchQuery}"/>"</c:when>
                        <c:when test="${not empty selectedCategory}"><c:out value="${selectedCategory}"/></c:when>
                        <c:otherwise>All Products</c:otherwise>
                    </c:choose>
                </h1>
                <p style="color:var(--gray-500);font-size:0.9rem">${fn:length(products)} product(s) found</p>
            </div>
            <form id="filterForm" action="${pageContext.request.contextPath}/products" method="GET">
                <c:if test="${not empty searchQuery}">
                    <input type="hidden" name="search" value="<c:out value='${searchQuery}'/>">
                </c:if>
                <c:if test="${not empty selectedCategory}">
                    <input type="hidden" name="category" value="<c:out value='${selectedCategory}'/>">
                </c:if>
                <select name="sort" id="sortSelect" class="form-control" style="padding:0.5rem 1rem;font-size:0.9rem;border-radius:var(--radius-md);width:auto">
                    <option value="" ${empty selectedSort ? 'selected' : ''}>Newest First</option>
                    <option value="price_asc" ${selectedSort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                    <option value="price_desc" ${selectedSort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                    <option value="rating" ${selectedSort == 'rating' ? 'selected' : ''}>Best Rated</option>
                </select>
            </form>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
        </c:if>

        <c:choose>
            <c:when test="${not empty products}">
                <div class="products-grid">
                    <c:forEach var="product" items="${products}">
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
                                <h2 class="product-title">
                                    <a href="${pageContext.request.contextPath}/product-details?id=${product.id}"
                                       style="color:inherit;text-decoration:none">
                                        <c:out value="${product.name}"/>
                                    </a>
                                </h2>
                                <div class="product-rating">
                                    ⭐ <fmt:formatNumber value="${product.averageRating}" maxFractionDigits="1"/>
                                    <span class="rating-count">(${product.reviewCount} reviews)</span>
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
                                            <span class="stock-indicator low-stock">Only ${product.stockQty} left</span>
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
                                       class="btn btn-secondary btn-sm">Details</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">🔍</div>
                    <h2 class="empty-title">No Products Found</h2>
                    <p class="empty-desc">
                        <c:choose>
                            <c:when test="${not empty searchQuery}">
                                No results for "<c:out value='${searchQuery}'/>". Try a different search term.
                            </c:when>
                            <c:otherwise>No products in this category yet.</c:otherwise>
                        </c:choose>
                    </p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Browse All Products</a>
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
<script src="${pageContext.request.contextPath}/js/products.js"></script>
</body>
</html>
