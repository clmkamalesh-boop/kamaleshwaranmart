package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.dto.AdminStatsDTO;
import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.OrderStatus;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.service.AdminService;
import com.kamaleshwaran.kamaleshwaranmart.service.OrderService;
import com.kamaleshwaran.kamaleshwaranmart.service.ProductService;
import com.kamaleshwaran.kamaleshwaranmart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for Administrator Dashboard, moderation, and user/order management.
 */
@WebServlet(urlPatterns = {
        "/admin",
        "/admin/dashboard",
        "/admin/users",
        "/admin/products",
        "/admin/orders",
        "/admin/user/delete",
        "/admin/product/delete",
        "/admin/order/update",
        "/admin/*",
        "/api/v1/admin/*"
})
public class AdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);

    private final AdminService adminService = new AdminService();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String fullPath = path + (pathInfo != null ? pathInfo : "");
        boolean isApi = fullPath.startsWith("/api/v1/admin");

        try {
            AdminStatsDTO stats = adminService.getAdminDashboardStats();
            List<User> allUsers = adminService.getAllUsers();
            List<Product> allProducts = adminService.getAllProducts();
            List<Order> allOrders = adminService.getAllOrders();

            long pendingOrders = allOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.PENDING)
                    .count();

            if (isApi) {
                if (fullPath.endsWith("/users")) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(allUsers));
                } else if (fullPath.endsWith("/products")) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(allProducts));
                } else if (fullPath.endsWith("/orders")) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(allOrders));
                } else {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(stats));
                }
                return;
            }

            int totalAdmins = Math.max(0, stats.getTotalUsers() - stats.getTotalBuyers() - stats.getTotalSellers());

            // Ensure password hashes are never exposed in attributes or responses
            for (User u : allUsers) {
                u.setPasswordHash(null);
            }

            // Expose attributes required by admin-dashboard.jsp
            req.setAttribute("stats", stats);
            req.setAttribute("totalUsers", stats.getTotalUsers());
            req.setAttribute("totalBuyers", stats.getTotalBuyers());
            req.setAttribute("totalSellers", stats.getTotalSellers());
            req.setAttribute("totalAdmins", totalAdmins);
            req.setAttribute("totalProducts", stats.getTotalProducts());
            req.setAttribute("totalOrders", stats.getTotalOrders());
            req.setAttribute("platformRevenue", stats.getTotalPlatformRevenue());
            req.setAttribute("pendingOrders", pendingOrders);
            req.setAttribute("allUsers", allUsers);
            req.setAttribute("allProducts", allProducts);
            req.setAttribute("allOrders", allOrders);
            req.setAttribute("recentUsers", allUsers);
            req.setAttribute("recentOrders", allOrders);
            req.setAttribute("orderStatuses", OrderStatus.values());

            String reqTab = req.getParameter("tab");
            if (reqTab != null && !reqTab.isEmpty()) {
                req.setAttribute("activeTab", reqTab);
            } else if (fullPath.endsWith("/users")) {
                req.setAttribute("activeTab", "users");
            } else if (fullPath.endsWith("/products")) {
                req.setAttribute("activeTab", "products");
            } else if (fullPath.endsWith("/orders")) {
                req.setAttribute("activeTab", "orders");
            }

            req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Error in AdminServlet doGet: {}", e.getMessage(), e);
            if (isApi) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Failed to load admin data."));
            } else {
                req.setAttribute("errorMessage", "Failed to load admin module.");
                req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String fullPath = path + (pathInfo != null ? pathInfo : "");
        String action = req.getParameter("action");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"))
                || fullPath.startsWith("/api/v1/admin");

        try {
            if (fullPath.endsWith("/user/delete") || "deleteUser".equalsIgnoreCase(action)) {
                Long userId = Long.parseLong(req.getParameter("userId"));
                boolean deleted = adminService.deleteUser(userId);

                if (isAjax) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(deleted));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard#users");
                }

            } else if (fullPath.endsWith("/product/delete") || "deleteProduct".equalsIgnoreCase(action)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                boolean deleted = adminService.moderateRemoveProduct(productId);

                if (isAjax) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(deleted));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard#products");
                }

            } else if (fullPath.endsWith("/order/update") || "updateOrderStatus".equalsIgnoreCase(action)) {
                Long orderId = Long.parseLong(req.getParameter("orderId"));
                OrderStatus status = OrderStatus.fromString(req.getParameter("status"));
                boolean updated = orderService.updateStatus(orderId, status);

                if (isAjax) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(updated));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard#orders");
                }

            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            }
        } catch (Exception e) {
            logger.error("Error handling admin mutation action: path={}, action={}, error={}", fullPath, action, e.getMessage(), e);
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Action failed."));
            } else {
                req.setAttribute("errorMessage", "Operation failed.");
                doGet(req, resp);
            }
        }
    }
}
