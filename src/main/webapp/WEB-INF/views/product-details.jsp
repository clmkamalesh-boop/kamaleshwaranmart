<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="${not empty product ? product.name : 'Product Details'} - KamaleshwaranMart">
    <title><c:out value="${product.name}"/> – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
    <style>
        .pdp-layout { display:grid; grid-template-columns:1fr 1.1fr; gap:2.5rem; margin:2rem 0; }
        @media(max-width:768px){ .pdp-layout{grid-template-columns:1fr;} }
        .pdp-image { border-radius:1rem;overflow:hidden;box-shadow:var(--shadow-lg);background:var(--gray-100);aspect-ratio:4/3;display:flex;align-items:center;justify-content:center; }
        .pdp-image img { width:100%;height:100%;object-fit:cover; }
        .pdp-info { display:flex;flex-direction:column;gap:1rem; }
        .pdp-category { font-size:0.78rem;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;color:var(--primary);background:var(--primary-light);padding:0.25rem 0.75rem;border-radius:50px;width:fit-content; }
        .pdp-title { font-size:1.9rem;font-weight:800;color:var(--gray-900);line-height:1.25; }
        .pdp-price { font-size:2rem;font-weight:800;color:var(--primary); }
        .pdp-stock { display:flex;align-items:center;gap:0.5rem;font-size:0.9rem; }
        .pdp-seller-card { background:var(--gray-50);border:1px solid var(--gray-200);border-radius:var(--radius-md);padding:1rem 1.25rem;font-size:0.9rem; }
        .qty-selector { display:flex;align-items:center;gap:0.75rem; }
        .qty-btn { width:2.25rem;height:2.25rem;border-radius:50%;border:2px solid var(--primary);background:transparent;color:var(--primary);font-size:1.2rem;cursor:pointer;font-weight:700;transition:all 0.2s; }
        .qty-btn:hover { background:var(--primary);color:white; }
        .qty-display { font-size:1.3rem;font-weight:700;min-width:2.5rem;text-align:center; }
        .star-filled { color:#f59e0b; }
        .star-empty { color:#d1d5db; }
        .review-card { background:var(--gray-50);border-radius:var(--radius-md);padding:1.25rem;border-left:4px solid var(--primary); }
        .review-author { font-weight:600;color:var(--gray-900);margin-bottom:0.25rem; }
        .review-date { font-size:0.78rem;color:var(--gray-400); }
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
            <c:if test="${not empty sessionScope.currentUser}">
                <li><a href="${pageContext.request.contextPath}/cart" class="nav-link">🛒 Cart</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Sign Out</a></li>
            </c:if>
            <c:if test="${empty sessionScope.currentUser}">
                <li><a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm">Sign In</a></li>
            </c:if>
        </ul>
    </div>
</nav>

<!-- Breadcrumb -->
<div class="container" style="margin-top:1rem">
    <nav aria-label="Breadcrumb" style="font-size:0.85rem;color:var(--gray-500)">
        <a href="${pageContext.request.contextPath}/home" style="color:var(--primary)">Home</a> &rsaquo;
        <a href="${pageContext.request.contextPath}/products" style="color:var(--primary)">Products</a> &rsaquo;
        <c:if test="${not empty product.category}">
            <a href="${pageContext.request.contextPath}/products?category=${product.category}" style="color:var(--primary)">
                <c:out value="${product.category}"/>
            </a> &rsaquo;
        </c:if>
        <span><c:out value="${product.name}"/></span>
    </nav>
</div>

<main>
    <div class="container">
        <c:choose>
            <c:when test="${not empty product}">
                <div class="pdp-layout">
                    <!-- Product Image -->
                    <div class="pdp-image">
                        <img src="${not empty product.imageUrl ? product.imageUrl : 'https://via.placeholder.com/800x600?text=No+Image'}"
                             alt="<c:out value='${product.name}'/>"
                             onerror="this.src='https://via.placeholder.com/800x600?text=No+Image'">
                    </div>

                    <!-- Product Info -->
                    <div class="pdp-info">
                        <div class="pdp-category"><c:out value="${product.category}"/></div>

                        <h1 class="pdp-title"><c:out value="${product.name}"/></h1>

                        <div class="product-rating">
                            <c:set var="rating" value="${product.averageRating}"/>
                            <c:forEach begin="1" end="5" var="star">
                                <c:choose>
                                    <c:when test="${star <= rating}">⭐</c:when>
                                    <c:otherwise>☆</c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <fmt:formatNumber value="${rating}" maxFractionDigits="1"/>
                            <span class="rating-count">(${product.reviewCount} reviews)</span>
                        </div>

                        <div class="pdp-price">
                            ₹<fmt:formatNumber value="${product.price}" pattern="#,##,##0.00"/>
                        </div>

                        <div class="pdp-seller-card">
                            🏪 Sold by: <strong><c:out value="${product.sellerName}"/></strong>
                        </div>

                        <div class="pdp-stock">
                            <c:choose>
                                <c:when test="${product.stockQty == 0}">
                                    <span class="stock-indicator out-of-stock">❌ Out of Stock</span>
                                </c:when>
                                <c:when test="${product.stockQty <= 5}">
                                    <span class="stock-indicator low-stock">⚠️ Only ${product.stockQty} left in stock!</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="stock-indicator in-stock">✅ In Stock (${product.stockQty} available)</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <c:if test="${product.stockQty > 0}">
                            <div class="qty-selector">
                                <span style="font-weight:600">Qty:</span>
                                <button class="qty-btn" id="qtyDown">−</button>
                                <span class="qty-display" id="qtyDisplay">1</span>
                                <button class="qty-btn" id="qtyUp">+</button>
                                <input type="hidden" id="qtyInput" value="1">
                            </div>
                        </c:if>

                        <div style="display:flex;gap:0.75rem;flex-wrap:wrap">
                            <c:if test="${product.stockQty > 0}">
                                <button class="btn btn-primary btn-lg" id="btnAddToCart"
                                        data-product-id="${product.id}" style="flex:1;min-width:180px">
                                    🛒 Add to Cart
                                </button>
                            </c:if>
                            <c:if test="${product.stockQty == 0}">
                                <button class="btn btn-lg" disabled
                                        style="flex:1;background:var(--gray-300);color:var(--gray-600);cursor:not-allowed">
                                    Out of Stock
                                </button>
                            </c:if>
                        </div>

                        <div style="border-top:1px solid var(--gray-200);padding-top:1rem;margin-top:0.5rem">
                            <h3 style="font-weight:700;margin-bottom:0.5rem">Product Description</h3>
                            <p style="color:var(--gray-600);line-height:1.8;font-size:0.95rem">
                                <c:out value="${product.description}"/>
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Reviews Section -->
                <div style="margin:2rem 0">
                    <h2 style="font-size:1.5rem;font-weight:800;color:var(--gray-900);margin-bottom:1.5rem">
                        ⭐ Customer Reviews (${fn:length(reviews)})
                    </h2>

                    <!-- Write Review -->
                    <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.role == 'BUYER'}">
                        <div class="card" style="margin-bottom:1.5rem">
                            <div class="card-body">
                                <h3 style="font-weight:700;margin-bottom:1rem">Write a Review</h3>
                                <form id="reviewForm">
                                    <input type="hidden" id="reviewProductId" value="${product.id}">
                                    <div class="form-group">
                                        <label class="form-label">Your Rating</label>
                                        <div id="starRating" style="font-size:2rem;cursor:pointer;letter-spacing:0.25rem">
                                            <span class="star-pick" data-val="1">☆</span>
                                            <span class="star-pick" data-val="2">☆</span>
                                            <span class="star-pick" data-val="3">☆</span>
                                            <span class="star-pick" data-val="4">☆</span>
                                            <span class="star-pick" data-val="5">☆</span>
                                        </div>
                                        <input type="hidden" id="reviewRating" value="0">
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label" for="reviewText">Your Review</label>
                                        <textarea id="reviewText" class="form-control" rows="3" 
                                                  placeholder="Share your experience with this product..." 
                                                  maxlength="1000"></textarea>
                                    </div>
                                    <button type="submit" class="btn btn-primary">Submit Review</button>
                                </form>
                            </div>
                        </div>
                    </c:if>

                    <!-- Review List -->
                    <c:choose>
                        <c:when test="${not empty reviews}">
                            <div style="display:flex;flex-direction:column;gap:1rem">
                                <c:forEach var="review" items="${reviews}">
                                    <div class="review-card">
                                        <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:0.5rem">
                                            <div class="review-author">
                                                <c:out value="${review.reviewerName}"/>
                                            </div>
                                            <div class="review-date">
                                                <fmt:formatDate value="${review.createdAt}" pattern="dd MMM yyyy"/>
                                            </div>
                                        </div>
                                        <div style="color:#f59e0b;font-size:1.1rem;margin-bottom:0.5rem">
                                            <c:forEach begin="1" end="${review.rating}" var="s">⭐</c:forEach>
                                        </div>
                                        <p style="color:var(--gray-700);line-height:1.7"><c:out value="${review.comment}"/></p>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" style="padding:2rem">
                                <div class="empty-icon" style="font-size:2rem">💬</div>
                                <p class="empty-desc">No reviews yet. Be the first to review this product!</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </c:when>
            <c:otherwise>
                <div class="empty-state" style="margin-top:4rem">
                    <div class="empty-icon">❌</div>
                    <h2 class="empty-title">Product Not Found</h2>
                    <p class="empty-desc">This product might have been removed or you have an invalid link.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Browse Products</a>
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
