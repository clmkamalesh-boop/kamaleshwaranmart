package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.dto.SellerStatsDTO;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.OrderStatus;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.service.OrderService;
import com.kamaleshwaran.kamaleshwaranmart.service.ProductService;
import com.kamaleshwaran.kamaleshwaranmart.service.SellerService;
import com.kamaleshwaran.kamaleshwaranmart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for Seller Dashboard, inventory CRUD, and incoming orders.
 */
@WebServlet(urlPatterns = {
        "/seller",
        "/seller/dashboard",
        "/seller/products",
        "/seller/orders",
        "/seller/product/save",
        "/seller/product/delete",
        "/seller/order/update",
        "/seller/*",
        "/api/v1/seller/*"
})
public class SellerServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SellerServlet.class);

    private final SellerService sellerService = new SellerService();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String fullPath = path + (pathInfo != null ? pathInfo : "");
        boolean isApi = fullPath.startsWith("/api/v1/seller");

        try {
            SellerStatsDTO stats = sellerService.getSellerDashboardStats(user.getId());
            List<Order> sellerOrders = orderService.getSellerOrders(user.getId());
            List<Product> products = sellerService.getSellerProducts(user.getId());
            List<String> categories = productService.getAllCategories();

            double averageRating = products.stream()
                    .filter(p -> p.getAverageRating() != null && p.getAverageRating() > 0)
                    .mapToDouble(Product::getAverageRating)
                    .average()
                    .orElse(5.0);

            if (isApi) {
                if (fullPath.endsWith("/products")) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(products));
                } else if (fullPath.endsWith("/orders")) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(sellerOrders));
                } else {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(stats));
                }
                return;
            }

            req.setAttribute("stats", stats);
            req.setAttribute("products", products);
            req.setAttribute("sellerOrders", sellerOrders);
            req.setAttribute("recentOrders", sellerOrders);
            req.setAttribute("totalRevenue", stats.getRevenue());
            req.setAttribute("averageRating", averageRating);
            req.setAttribute("categories", categories);

            req.getRequestDispatcher("/WEB-INF/views/seller-dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Error in SellerServlet doGet: {}", e.getMessage(), e);
            if (isApi) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Failed to load seller portal data."));
            } else {
                req.setAttribute("errorMessage", "An error occurred while loading the seller portal.");
                req.getRequestDispatcher("/WEB-INF/views/seller-dashboard.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponseDTO.error("UNAUTHORIZED", "Please log in as seller"));
            return;
        }

        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String fullPath = path + (pathInfo != null ? pathInfo : "");
        String action = req.getParameter("action");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"))
                || fullPath.startsWith("/api/v1/seller");

        try {
            if (fullPath.endsWith("/product/save") || "create".equalsIgnoreCase(action)
                    || "update".equalsIgnoreCase(action) || "save".equalsIgnoreCase(action)) {

                String productIdParam = req.getParameter("productId");
                String name = req.getParameter("name");
                String description = req.getParameter("description");
                BigDecimal price = new BigDecimal(req.getParameter("price"));

                String stockParam = req.getParameter("stockQty");
                if (stockParam == null || stockParam.trim().isEmpty()) {
                    stockParam = req.getParameter("stock");
                }
                int stock = Integer.parseInt(stockParam);

                String category = req.getParameter("category");
                String imageUrl = req.getParameter("imageUrl");

                if (productIdParam != null && !productIdParam.trim().isEmpty()) {
                    Long productId = Long.parseLong(productIdParam.trim());
                    boolean updated = productService.updateProduct(user.getId(), productId, name, description, price, stock, category, imageUrl);
                    if (isAjax) {
                        JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(updated));
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/seller/dashboard#products");
                    }
                } else {
                    Product created = productService.createProduct(user.getId(), name, description, price, stock, category, imageUrl);
                    if (isAjax) {
                        JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_CREATED, ApiResponseDTO.success(created));
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/seller/dashboard#products");
                    }
                }

            } else if (fullPath.endsWith("/product/delete") || "delete".equalsIgnoreCase(action)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                boolean deleted = productService.deleteProduct(user.getId(), productId, false);

                if (isAjax) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(deleted));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/seller/dashboard#products");
                }

            } else if (fullPath.endsWith("/order/update") || "updateOrderStatus".equalsIgnoreCase(action)) {
                Long orderId = Long.parseLong(req.getParameter("orderId"));
                OrderStatus status = OrderStatus.fromString(req.getParameter("status"));
                boolean updated = orderService.updateStatus(orderId, status);

                if (isAjax) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(updated));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/seller/dashboard#orders");
                }

            } else {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            }

        } catch (ValidationException e) {
            logger.warn("Validation error in seller mutation: {}", e.getMessage());
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponseDTO.error("VALIDATION_ERROR", e.getMessage()));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
            }
        } catch (Exception e) {
            logger.error("Error modifying seller resource: {}", e.getMessage(), e);
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Operation failed: " + e.getMessage()));
            } else {
                req.setAttribute("errorMessage", "Operation failed. Please verify inputs.");
                doGet(req, resp);
            }
        }
    }
}
