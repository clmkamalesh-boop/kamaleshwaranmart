<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Seller Dashboard – KamaleshwaranMart">
    <title>Seller Dashboard – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
</head>
<body>
<script>window.CONTEXT_PATH = '${pageContext.request.contextPath}';</script>

<!-- Sidebar -->
<div class="dashboard-layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <a href="${pageContext.request.contextPath}/home" class="sidebar-brand-link">
                <div class="brand-icon-sm">🛒</div>
                <span>KMart</span>
            </a>
        </div>
        <div class="sidebar-user">
            <div class="avatar">
                ${fn:substring(sessionScope.currentUser.name, 0, 1)}
            </div>
            <div>
                <div class="sidebar-user-name"><c:out value="${sessionScope.currentUser.name}"/></div>
                <div class="sidebar-user-role">🏪 Seller</div>
            </div>
        </div>
        <nav class="sidebar-nav">
            <a href="#overview" class="sidebar-link active" onclick="showTab('overview', this)">
                📊 Overview
            </a>
            <a href="#products" class="sidebar-link" onclick="showTab('products', this)">
                🏷️ My Products
            </a>
            <a href="#add-product" class="sidebar-link" onclick="showTab('add-product', this)">
                ➕ Add Product
            </a>
            <a href="#orders" class="sidebar-link" onclick="showTab('orders', this)">
                📦 Orders
            </a>
            <hr style="border-color:rgba(255,255,255,0.1);margin:1rem 0">
            <a href="${pageContext.request.contextPath}/logout" class="sidebar-link text-danger">
                🚪 Sign Out
            </a>
        </nav>
    </aside>

    <!-- Main Content -->
    <main class="dashboard-main">
        <div class="dashboard-header">
            <div>
                <h1 class="dashboard-title">Seller Dashboard</h1>
                <p style="color:var(--gray-500)">Welcome back, <c:out value="${sessionScope.currentUser.name}"/>!</p>
            </div>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">✅ <c:out value="${successMessage}"/></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">⚠️ <c:out value="${errorMessage}"/></div>
        </c:if>

        <!-- === OVERVIEW TAB === -->
        <div id="tab-overview" class="tab-content active">
            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">🏷️</div>
                    <div class="stat-body">
                        <div class="stat-value">${fn:length(products)}</div>
                        <div class="stat-label">Total Products</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">📦</div>
                    <div class="stat-body">
                        <div class="stat-value">${fn:length(sellerOrders)}</div>
                        <div class="stat-label">Total Orders</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">💰</div>
                    <div class="stat-body">
                        <div class="stat-value">₹<fmt:formatNumber value="${totalRevenue}" pattern="#,##,##0"/></div>
                        <div class="stat-label">Total Revenue</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">⭐</div>
                    <div class="stat-body">
                        <div class="stat-value">
                            <fmt:formatNumber value="${averageRating}" maxFractionDigits="1"/>
                        </div>
                        <div class="stat-label">Avg. Rating</div>
                    </div>
                </div>
            </div>

            <!-- Recent Orders Preview -->
            <div class="card" style="margin-top:1.5rem">
                <div class="card-body">
                    <h2 style="font-weight:700;font-size:1.1rem;margin-bottom:1rem">🕐 Recent Orders</h2>
                    <c:choose>
                        <c:when test="${not empty sellerOrders}">
                            <div class="table-responsive">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>Order ID</th>
                                            <th>Buyer</th>
                                            <th>Amount</th>
                                            <th>Status</th>
                                            <th>Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="order" items="${sellerOrders}" begin="0" end="4">
                                            <tr>
                                                <td style="font-family:monospace">#${order.id}</td>
                                                <td><c:out value="${order.buyerName}"/></td>
                                                <td>₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##,##0.00"/></td>
                                                <td>
                                                    <span class="status-badge status-${order.status}">
                                                        <c:out value="${order.status}"/>
                                                    </span>
                                                </td>
                                                <td>
                                                    <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yy"/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" style="padding:1.5rem">
                                <p class="empty-desc">No orders yet. List your products to start receiving orders.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- === PRODUCTS TAB === -->
        <div id="tab-products" class="tab-content">
            <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:1.25rem">
                <h2 style="font-weight:800;font-size:1.25rem">🏷️ My Products</h2>
                <button class="btn btn-primary btn-sm" onclick="showTab('add-product', document.querySelector('[href=\'#add-product\']'))">
                    ➕ Add New
                </button>
            </div>
            <c:choose>
                <c:when test="${not empty products}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Category</th>
                                    <th>Price</th>
                                    <th>Stock</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="product" items="${products}">
                                    <tr>
                                        <td>
                                            <div style="font-weight:600;font-size:0.9rem">
                                                <c:out value="${product.name}"/>
                                            </div>
                                        </td>
                                        <td><span class="badge"><c:out value="${product.category}"/></span></td>
                                        <td>₹<fmt:formatNumber value="${product.price}" pattern="#,##,##0.00"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${product.stockQty == 0}">
                                                    <span class="stock-indicator out-of-stock">${product.stockQty}</span>
                                                </c:when>
                                                <c:when test="${product.stockQty <= 5}">
                                                    <span class="stock-indicator low-stock">${product.stockQty}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="stock-indicator in-stock">${product.stockQty}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div style="display:flex;gap:0.5rem">
                                                <button class="btn btn-sm btn-secondary btn-edit-product"
                                                        data-product-id="${product.id}"
                                                        data-product-name="<c:out value='${product.name}'/>"
                                                        data-product-price="${product.price}"
                                                        data-product-stock="${product.stockQty}"
                                                        data-product-category="<c:out value='${product.category}'/>"
                                                        data-product-desc="<c:out value='${product.description}'/>"
                                                        data-product-image="<c:out value='${product.imageUrl}'/>">
                                                    ✏️ Edit
                                                </button>
                                                <form action="${pageContext.request.contextPath}/seller/product/delete" method="POST" style="margin:0">
                                                    <input type="hidden" name="productId" value="${product.id}">
                                                    <button type="submit" class="btn btn-sm"
                                                            style="background:var(--danger);color:white"
                                                            onclick="return confirm('Delete this product?')">
                                                        🗑
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-icon">🏷️</div>
                        <h3 class="empty-title">No Products Listed</h3>
                        <p class="empty-desc">You haven't listed any products yet. Start selling now!</p>
                        <button class="btn btn-primary" onclick="showTab('add-product', document.querySelector('[href=\'#add-product\']'))">
                            ➕ Add First Product
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- === ADD PRODUCT TAB === -->
        <div id="tab-add-product" class="tab-content">
            <h2 style="font-weight:800;font-size:1.25rem;margin-bottom:1.25rem" id="productFormTitle">
                ➕ Add New Product
            </h2>
            <div class="card">
                <div class="card-body">
                    <form id="productForm" action="${pageContext.request.contextPath}/seller/product/save" method="POST">
                        <input type="hidden" id="productId" name="productId" value="">
                        <div style="display:grid;grid-template-columns:1fr 1fr;gap:1.25rem">
                            <div class="form-group">
                                <label class="form-label" for="productName">Product Name *</label>
                                <input type="text" id="productName" name="name" class="form-control"
                                       placeholder="e.g. Wireless Headphones Pro" required maxlength="200">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="productCategory">Category *</label>
                                <select id="productCategory" name="category" class="form-control" required>
                                    <option value="">-- Select Category --</option>
                                    <option>Electronics</option>
                                    <option>Fashion</option>
                                    <option>Home &amp; Kitchen</option>
                                    <option>Sports &amp; Fitness</option>
                                    <option>Books</option>
                                    <option>Beauty &amp; Health</option>
                                    <option>Toys &amp; Games</option>
                                    <option>Groceries</option>
                                    <option>Automotive</option>
                                    <option>Other</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="productPrice">Price (₹) *</label>
                                <input type="number" id="productPrice" name="price" class="form-control"
                                       placeholder="e.g. 2499.00" min="1" step="0.01" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="productStock">Stock Quantity *</label>
                                <input type="number" id="productStock" name="stockQty" class="form-control"
                                       placeholder="e.g. 100" min="0" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="productImageUrl">Image URL</label>
                            <input type="url" id="productImageUrl" name="imageUrl" class="form-control"
                                   placeholder="https://example.com/product-image.jpg">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="productDescription">Description *</label>
                            <textarea id="productDescription" name="description" class="form-control" rows="4"
                                      placeholder="Describe your product in detail..." required maxlength="2000"></textarea>
                        </div>
                        <div style="display:flex;gap:0.75rem">
                            <button type="submit" id="productSubmitBtn" class="btn btn-primary">
                                💾 Save Product
                            </button>
                            <button type="button" class="btn btn-secondary" onclick="resetProductForm()">
                                ✕ Cancel
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- === ORDERS TAB === -->
        <div id="tab-orders" class="tab-content">
            <h2 style="font-weight:800;font-size:1.25rem;margin-bottom:1.25rem">📦 Orders for My Products</h2>
            <c:choose>
                <c:when test="${not empty sellerOrders}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th>Buyer</th>
                                    <th>Products</th>
                                    <th>Total</th>
                                    <th>Status</th>
                                    <th>Date</th>
                                    <th>Update</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${sellerOrders}">
                                    <tr>
                                        <td style="font-family:monospace">#${order.id}</td>
                                        <td><c:out value="${order.buyerName}"/></td>
                                        <td style="font-size:0.82rem;color:var(--gray-500)">
                                            ${fn:length(order.items)} item(s)
                                        </td>
                                        <td>₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##,##0.00"/></td>
                                        <td>
                                            <span class="status-badge status-${order.status}">
                                                <c:out value="${order.status}"/>
                                            </span>
                                        </td>
                                        <td><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yy"/></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/seller/order/update" method="POST" style="margin:0;display:flex;gap:0.4rem">
                                                <input type="hidden" name="orderId" value="${order.id}">
                                                <select name="status" class="form-control" style="padding:0.3rem 0.5rem;font-size:0.82rem;width:auto">
                                                    <option value="PROCESSING" ${order.status == 'PROCESSING' ? 'selected' : ''}>Processing</option>
                                                    <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                                    <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                                    <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                                </select>
                                                <button type="submit" class="btn btn-primary btn-sm">✓</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-icon">📦</div>
                        <p class="empty-desc">No orders for your products yet.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<!-- Edit Product Modal -->
<div id="editModal" class="modal-overlay" style="display:none">
    <div class="modal-card">
        <div class="modal-header">
            <h3>✏️ Edit Product</h3>
            <button class="modal-close" onclick="document.getElementById('editModal').style.display='none'">✕</button>
        </div>
        <form action="${pageContext.request.contextPath}/seller/product/save" method="POST">
            <input type="hidden" id="editProductId" name="productId">
            <div class="form-group">
                <label class="form-label" for="editProductName">Name</label>
                <input type="text" id="editProductName" name="name" class="form-control" required>
            </div>
            <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem">
                <div class="form-group">
                    <label class="form-label" for="editProductPrice">Price (₹)</label>
                    <input type="number" id="editProductPrice" name="price" class="form-control" min="1" step="0.01" required>
                </div>
                <div class="form-group">
                    <label class="form-label" for="editProductStockQty">Stock</label>
                    <input type="number" id="editProductStockQty" name="stockQty" class="form-control" min="0" required>
                </div>
            </div>
            <div class="form-group">
                <label class="form-label" for="editProductCategory">Category</label>
                <select id="editProductCategory" name="category" class="form-control">
                    <option>Electronics</option><option>Fashion</option>
                    <option>Home &amp; Kitchen</option><option>Sports &amp; Fitness</option>
                    <option>Books</option><option>Beauty &amp; Health</option>
                    <option>Toys &amp; Games</option><option>Groceries</option>
                    <option>Automotive</option><option>Other</option>
                </select>
            </div>
            <div class="form-group">
                <label class="form-label" for="editProductImage">Image URL</label>
                <input type="url" id="editProductImage" name="imageUrl" class="form-control">
            </div>
            <div class="form-group">
                <label class="form-label" for="editProductDesc">Description</label>
                <textarea id="editProductDesc" name="description" class="form-control" rows="3" required></textarea>
            </div>
            <div style="display:flex;gap:0.75rem">
                <button type="submit" class="btn btn-primary">💾 Save Changes</button>
                <button type="button" class="btn btn-secondary" onclick="document.getElementById('editModal').style.display='none'">Cancel</button>
            </div>
        </form>
    </div>
</div>

<div id="toastContainer" class="toast-container"></div>
<script src="${pageContext.request.contextPath}/js/products.js"></script>
<script>
// Tab navigation
function showTab(tabName, linkEl) {
    document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.sidebar-link').forEach(l => l.classList.remove('active'));
    const tab = document.getElementById('tab-' + tabName);
    if (tab) tab.classList.add('active');
    if (linkEl) linkEl.classList.add('active');
    return false;
}

// Edit product button handler
document.querySelectorAll('.btn-edit-product').forEach(btn => {
    btn.addEventListener('click', () => {
        const d = btn.dataset;
        document.getElementById('editProductId').value = d.productId;
        document.getElementById('editProductName').value = d.productName;
        document.getElementById('editProductPrice').value = d.productPrice;
        document.getElementById('editProductStockQty').value = d.productStock;
        document.getElementById('editProductImage').value = d.productImage || '';
        document.getElementById('editProductDesc').value = d.productDesc;
        const cat = document.getElementById('editProductCategory');
        for(let i=0; i<cat.options.length; i++){
            if(cat.options[i].text === d.productCategory || cat.options[i].value === d.productCategory)
                cat.selectedIndex = i;
        }
        document.getElementById('editModal').style.display = 'flex';
    });
});

function resetProductForm() {
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    showTab('products', document.querySelector('[href="#products"]'));
}

// Highlight tab from URL hash
const hash = window.location.hash;
if (hash) {
    const tabName = hash.slice(1);
    const link = document.querySelector('[href="' + hash + '"]');
    showTab(tabName, link);
}
</script>
</body>
</html>
