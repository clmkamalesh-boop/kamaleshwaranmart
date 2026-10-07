<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Admin Dashboard – KamaleshwaranMart">
    <title>Admin Dashboard – KamaleshwaranMart</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/responsive.css">
</head>
<body>
<script>window.CONTEXT_PATH = '${pageContext.request.contextPath}';</script>

<div class="dashboard-layout">
    <aside class="sidebar sidebar-admin">
        <div class="sidebar-brand">
            <a href="${pageContext.request.contextPath}/home" class="sidebar-brand-link">
                <div class="brand-icon-sm">⚙️</div>
                <span>KMart Admin</span>
            </a>
        </div>
        <div class="sidebar-user">
            <div class="avatar avatar-admin">
                ${fn:substring(sessionScope.currentUser.name, 0, 1)}
            </div>
            <div>
                <div class="sidebar-user-name"><c:out value="${sessionScope.currentUser.name}"/></div>
                <div class="sidebar-user-role">🔑 Administrator</div>
            </div>
        </div>
        <nav class="sidebar-nav">
            <a href="#overview" class="sidebar-link active" onclick="showTab('overview', this)">📊 Overview</a>
            <a href="#users" class="sidebar-link" onclick="showTab('users', this)">👥 Users</a>
            <a href="#products" class="sidebar-link" onclick="showTab('products', this)">🏷️ Products</a>
            <a href="#orders" class="sidebar-link" onclick="showTab('orders', this)">📦 Orders</a>
            <hr style="border-color:rgba(255,255,255,0.1);margin:1rem 0">
            <a href="${pageContext.request.contextPath}/api/v1/health" class="sidebar-link" target="_blank">
                ❤️ Health Check
            </a>
            <a href="${pageContext.request.contextPath}/logout" class="sidebar-link text-danger">🚪 Sign Out</a>
        </nav>
    </aside>

    <main class="dashboard-main">
        <div class="dashboard-header">
            <h1 class="dashboard-title">⚙️ Admin Control Panel</h1>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">✅ <c:out value="${successMessage}"/></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">⚠️ <c:out value="${errorMessage}"/></div>
        </c:if>

        <!-- OVERVIEW -->
        <div id="tab-overview" class="tab-content active">
            <div class="stats-grid">
                <div class="stat-card" style="cursor:pointer" onclick="showTab('users', document.querySelector('.sidebar-link[href=\'#users\']'))" title="Click to view full Users table">
                    <div class="stat-icon">👥</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalUsers}</div>
                        <div class="stat-label">Total Users</div>
                        <div style="font-size:0.75rem;color:var(--primary,#4f46e5);font-weight:700;margin-top:0.25rem">View All Users →</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🏪</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalSellers}</div>
                        <div class="stat-label">Sellers</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🏷️</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalProducts}</div>
                        <div class="stat-label">Products</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">📦</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalOrders}</div>
                        <div class="stat-label">Orders</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">💰</div>
                    <div class="stat-body">
                        <div class="stat-value">₹<fmt:formatNumber value="${platformRevenue}" pattern="#,##,##0"/></div>
                        <div class="stat-label">Platform Revenue</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">📊</div>
                    <div class="stat-body">
                        <div class="stat-value">${pendingOrders}</div>
                        <div class="stat-label">Pending Orders</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- USERS -->
        <div id="tab-users" class="tab-content">
            <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:1.25rem;flex-wrap:wrap;gap:1rem">
                <div>
                    <h2 style="font-weight:800;font-size:1.35rem;color:var(--gray-900);margin-bottom:0.2rem">👥 Registered Users Management</h2>
                    <p style="color:var(--gray-500);font-size:0.875rem;margin:0">Complete list of registered accounts in the database.</p>
                </div>
                <div style="display:flex;align-items:center;gap:0.5rem">
                    <span style="background:var(--primary-light,#e0e7ff);color:var(--primary,#4338ca);font-weight:700;font-size:0.85rem;padding:0.35rem 0.85rem;border-radius:2rem">
                        Total Registered: ${totalUsers}
                    </span>
                </div>
            </div>

            <!-- User Stat Cards -->
            <div class="stats-grid" style="margin-bottom:1.5rem">
                <div class="stat-card" style="border-left:4px solid var(--primary,#4f46e5)">
                    <div class="stat-icon">👥</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalUsers}</div>
                        <div class="stat-label">Total Users</div>
                    </div>
                </div>
                <div class="stat-card" style="border-left:4px solid #ef4444">
                    <div class="stat-icon">🔑</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalAdmins}</div>
                        <div class="stat-label">Admins</div>
                    </div>
                </div>
                <div class="stat-card" style="border-left:4px solid #3b82f6">
                    <div class="stat-icon">🏪</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalSellers}</div>
                        <div class="stat-label">Sellers</div>
                    </div>
                </div>
                <div class="stat-card" style="border-left:4px solid #10b981">
                    <div class="stat-icon">🛒</div>
                    <div class="stat-body">
                        <div class="stat-value">${totalBuyers}</div>
                        <div class="stat-label">Buyers</div>
                    </div>
                </div>
            </div>

            <!-- Search Box & Role Filter Bar -->
            <div class="card" style="margin-bottom:1.25rem;border:1px solid var(--gray-200);border-radius:var(--radius-lg)">
                <div class="card-body" style="padding:1.25rem">
                    <div style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:1rem">
                        <!-- Search Box -->
                        <div class="user-search-wrapper">
                            <span class="user-search-icon">🔍</span>
                            <input type="text" id="userSearchInput" class="user-search-input"
                                   placeholder="Search users by name or email..."
                                   oninput="filterUsers()" autocomplete="off">
                        </div>

                        <!-- Role Filter Buttons -->
                        <div style="display:flex;align-items:center;gap:0.4rem;flex-wrap:wrap">
                            <span style="font-size:0.8rem;font-weight:700;color:var(--gray-500);text-transform:uppercase;letter-spacing:0.04em;margin-right:0.25rem">Role:</span>
                            <button type="button" class="role-filter-btn active" data-role="ALL" onclick="setRoleFilter('ALL', this)">
                                ALL (${totalUsers})
                            </button>
                            <button type="button" class="role-filter-btn" data-role="ADMIN" onclick="setRoleFilter('ADMIN', this)">
                                ADMIN (${totalAdmins})
                            </button>
                            <button type="button" class="role-filter-btn" data-role="SELLER" onclick="setRoleFilter('SELLER', this)">
                                SELLER (${totalSellers})
                            </button>
                            <button type="button" class="role-filter-btn" data-role="BUYER" onclick="setRoleFilter('BUYER', this)">
                                BUYER (${totalBuyers})
                            </button>
                        </div>
                    </div>

                    <!-- Search / Filter Status Summary -->
                    <div style="display:flex;align-items:center;justify-content:space-between;margin-top:0.75rem;padding-top:0.75rem;border-top:1px solid var(--gray-100);font-size:0.82rem;color:var(--gray-500)">
                        <span>
                            Showing <strong id="visibleUsersCount" style="color:var(--gray-900)">${fn:length(allUsers)}</strong> of <strong style="color:var(--gray-900)">${totalUsers}</strong> registered users
                        </span>
                        <button type="button" id="resetUserFilterBtn" class="btn btn-link btn-sm" onclick="resetUserFilters()" style="padding:0;font-size:0.82rem;color:var(--primary);text-decoration:none;display:none;background:none;border:none;cursor:pointer">
                            ✕ Clear Filter
                        </button>
                    </div>
                </div>
            </div>

            <!-- Users Table Card -->
            <div class="card" style="border:1px solid var(--gray-200);border-radius:var(--radius-lg);overflow:hidden">
                <div class="card-body" style="padding:0">
                    <c:choose>
                        <c:when test="${not empty allUsers}">
                            <div class="table-responsive">
                                <table class="data-table" id="usersTable">
                                    <thead style="background:var(--gray-50);border-bottom:2px solid var(--gray-200)">
                                        <tr>
                                            <th style="width:80px;text-align:center">User ID</th>
                                            <th>Name</th>
                                            <th>Email</th>
                                            <th style="width:130px;text-align:center">Role</th>
                                            <th style="width:180px">Created Date</th>
                                            <th style="width:100px;text-align:center">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody id="usersTableBody">
                                        <c:forEach var="u" items="${allUsers}">
                                            <tr class="user-row"
                                                data-id="${u.id}"
                                                data-name="${fn:escapeXml(fn:toLowerCase(u.name))}"
                                                data-email="${fn:escapeXml(fn:toLowerCase(u.email))}"
                                                data-role="${u.role}">
                                                <td style="font-family:monospace;font-weight:700;color:var(--gray-600);text-align:center">
                                                    #${u.id}
                                                </td>
                                                <td>
                                                    <div style="display:flex;align-items:center;gap:0.6rem">
                                                        <div class="avatar avatar-sm" style="width:2rem;height:2rem;border-radius:50%;background:linear-gradient(135deg,#6366f1,#8b5cf6);color:white;display:flex;align-items:center;justify-content:center;font-size:0.8rem;font-weight:700;flex-shrink:0">
                                                            ${fn:substring(u.name, 0, 1)}
                                                        </div>
                                                        <div>
                                                            <div style="font-weight:700;color:var(--gray-900)"><c:out value="${u.name}"/></div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <a href="mailto:${u.email}" style="color:var(--primary);text-decoration:none">
                                                        <c:out value="${u.email}"/>
                                                    </a>
                                                </td>
                                                <td style="text-align:center">
                                                    <c:choose>
                                                        <c:when test="${u.role == 'ADMIN'}">
                                                            <span class="badge badge-admin" style="background:#fee2e2;color:#991b1b;border:1px solid #fecaca;padding:0.25rem 0.65rem;border-radius:2rem;font-weight:700;font-size:0.75rem">
                                                                🔑 ADMIN
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${u.role == 'SELLER'}">
                                                            <span class="badge badge-seller" style="background:#dbeafe;color:#1e40af;border:1px solid #bfdbfe;padding:0.25rem 0.65rem;border-radius:2rem;font-weight:700;font-size:0.75rem">
                                                                🏪 SELLER
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-buyer" style="background:#dcfce7;color:#166534;border:1px solid #bbf7d0;padding:0.25rem 0.65rem;border-radius:2rem;font-weight:700;font-size:0.75rem">
                                                                🛒 BUYER
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td style="color:var(--gray-600);font-size:0.85rem">
                                                    📅 <fmt:formatDate value="${u.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                                </td>
                                                <td style="text-align:center">
                                                    <c:choose>
                                                        <c:when test="${u.role == 'ADMIN'}">
                                                            <span style="font-size:0.75rem;color:var(--gray-400);font-weight:600" title="Admin accounts cannot be deleted">Protected</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form action="${pageContext.request.contextPath}/admin/user/delete" method="POST" style="margin:0;display:inline">
                                                                <input type="hidden" name="userId" value="${u.id}">
                                                                <button type="submit" class="btn btn-sm btn-delete-user"
                                                                        style="background:#fee2e2;color:#dc2626;border:1px solid #fecaca;border-radius:0.375rem;padding:0.25rem 0.6rem;font-size:0.8rem;transition:all 0.2s;cursor:pointer"
                                                                        onmouseover="this.style.background='#dc2626';this.style.color='#fff'"
                                                                        onmouseout="this.style.background='#fee2e2';this.style.color='#dc2626'"
                                                                        onclick="return confirm('Are you sure you want to delete user \'${fn:escapeXml(u.name)}\' (ID: #${u.id})?\n\nThis action cannot be undone.')"
                                                                        title="Delete user">
                                                                    🗑 Delete
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <!-- No Search Match State -->
                                <div id="noUsersMatchMsg" style="display:none;padding:2.5rem;text-align:center;color:var(--gray-500)">
                                    <div style="font-size:2.5rem;margin-bottom:0.5rem">🔍</div>
                                    <h4 style="font-weight:700;color:var(--gray-800);margin-bottom:0.25rem">No Matching Users</h4>
                                    <p style="font-size:0.875rem;margin-bottom:1rem">No registered users found matching your search and role filters.</p>
                                    <button type="button" class="btn btn-secondary btn-sm" onclick="resetUserFilters()">Clear Filters</button>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" style="padding:3rem;text-align:center">
                                <div style="font-size:3rem;margin-bottom:0.5rem">👥</div>
                                <h3 style="font-weight:800;color:var(--gray-900)">No Users Found in Database</h3>
                                <p style="color:var(--gray-500);font-size:0.9rem">No registered user accounts are currently present.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- PRODUCTS -->
        <div id="tab-products" class="tab-content">
            <h2 style="font-weight:800;font-size:1.2rem;margin-bottom:1.25rem">🏷️ All Products</h2>
            <div class="card">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty allProducts}">
                            <div class="table-responsive">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Product</th>
                                            <th>Seller</th>
                                            <th>Category</th>
                                            <th>Price</th>
                                            <th>Stock</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="p" items="${allProducts}">
                                            <tr>
                                                <td style="font-family:monospace">${p.id}</td>
                                                <td style="font-weight:600"><c:out value="${p.name}"/></td>
                                                <td><c:out value="${p.sellerName}"/></td>
                                                <td><c:out value="${p.category}"/></td>
                                                <td>₹<fmt:formatNumber value="${p.price}" pattern="#,##,##0.00"/></td>
                                                <td>${p.stockQty}</td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/admin/product/delete" method="POST" style="margin:0">
                                                        <input type="hidden" name="productId" value="${p.id}">
                                                        <button type="submit" class="btn btn-sm"
                                                                style="background:var(--danger);color:white"
                                                                onclick="return confirm('Delete product?')">
                                                            🗑
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state"><p>No products listed yet.</p></div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- ORDERS -->
        <div id="tab-orders" class="tab-content">
            <h2 style="font-weight:800;font-size:1.2rem;margin-bottom:1.25rem">📦 All Orders</h2>
            <div class="card">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty allOrders}">
                            <div class="table-responsive">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>Order ID</th>
                                            <th>Buyer</th>
                                            <th>Amount</th>
                                            <th>Status</th>
                                            <th>Payment</th>
                                            <th>Date</th>
                                            <th>Update</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="o" items="${allOrders}">
                                            <tr>
                                                <td style="font-family:monospace">#${o.id}</td>
                                                <td><c:out value="${o.buyerName}"/></td>
                                                <td>₹<fmt:formatNumber value="${o.totalAmount}" pattern="#,##,##0.00"/></td>
                                                <td>
                                                    <span class="status-badge status-${o.status}">
                                                        <c:out value="${o.status}"/>
                                                    </span>
                                                </td>
                                                <td><c:out value="${o.paymentMethod}"/></td>
                                                <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yy"/></td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/admin/order/update" method="POST" style="margin:0;display:flex;gap:0.4rem">
                                                        <input type="hidden" name="orderId" value="${o.id}">
                                                        <select name="status" class="form-control" style="padding:0.3rem;font-size:0.8rem;width:auto">
                                                            <option value="PENDING" ${o.status.name() == 'PENDING' ? 'selected' : ''}>Pending</option>
                                                            <option value="PROCESSING" ${o.status.name() == 'PROCESSING' ? 'selected' : ''}>Processing</option>
                                                            <option value="SHIPPED" ${o.status.name() == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                                            <option value="DELIVERED" ${o.status.name() == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                                            <option value="CANCELLED" ${o.status.name() == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
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
                            <div class="empty-state"><p>No orders placed yet.</p></div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>
</div>

<div id="toastContainer" class="toast-container"></div>
<script>
function showTab(tabName, linkEl) {
    document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.sidebar-link').forEach(l => l.classList.remove('active'));
    const tab = document.getElementById('tab-' + tabName);
    if (tab) tab.classList.add('active');
    if (linkEl) {
        linkEl.classList.add('active');
    } else {
        const link = document.querySelector('.sidebar-link[href="#' + tabName + '"]');
        if (link) link.classList.add('active');
    }
    return false;
}

const serverTab = '${activeTab}';
const hash = window.location.hash;
if (hash) {
    const link = document.querySelector('[href="' + hash + '"]');
    showTab(hash.slice(1), link);
} else if (serverTab) {
    showTab(serverTab);
}

// User Search and Role Filter Functions
let currentRoleFilter = 'ALL';

function setRoleFilter(role, btnEl) {
    currentRoleFilter = role;
    document.querySelectorAll('.role-filter-btn').forEach(b => {
        b.classList.remove('active');
    });
    if (btnEl) {
        btnEl.classList.add('active');
    }
    filterUsers();
}

function filterUsers() {
    const searchInput = document.getElementById('userSearchInput');
    const query = searchInput ? searchInput.value.trim().toLowerCase() : '';
    const rows = document.querySelectorAll('.user-row');
    const resetBtn = document.getElementById('resetUserFilterBtn');
    let visibleCount = 0;

    rows.forEach(row => {
        const name = row.getAttribute('data-name') || '';
        const email = row.getAttribute('data-email') || '';
        const role = row.getAttribute('data-role') || '';

        const matchesQuery = !query || name.includes(query) || email.includes(query);
        const matchesRole = (currentRoleFilter === 'ALL') || (role.toUpperCase() === currentRoleFilter.toUpperCase());

        if (matchesQuery && matchesRole) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    const visibleCounterEl = document.getElementById('visibleUsersCount');
    if (visibleCounterEl) {
        visibleCounterEl.textContent = visibleCount;
    }

    const noMatchEl = document.getElementById('noUsersMatchMsg');
    if (noMatchEl) {
        noMatchEl.style.display = (visibleCount === 0 && rows.length > 0) ? 'block' : 'none';
    }

    if (resetBtn) {
        const isFiltered = (query !== '') || (currentRoleFilter !== 'ALL');
        resetBtn.style.display = isFiltered ? 'inline-block' : 'none';
    }
}

function resetUserFilters() {
    const searchInput = document.getElementById('userSearchInput');
    if (searchInput) searchInput.value = '';
    const allBtn = document.querySelector('.role-filter-btn[data-role="ALL"]');
    setRoleFilter('ALL', allBtn);
}
</script>
</body>
</html>
