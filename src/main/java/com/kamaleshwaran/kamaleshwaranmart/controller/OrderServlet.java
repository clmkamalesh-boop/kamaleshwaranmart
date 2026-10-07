package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.dto.OrderResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.CartItem;
import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.service.CartService;
import com.kamaleshwaran.kamaleshwaranmart.service.OrderService;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for checkout process, mock payments, and customer order history.
 */
@WebServlet(urlPatterns = {"/checkout", "/orders", "/api/v1/orders", "/api/v1/orders/*"})
public class OrderServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(OrderServlet.class);

    private final OrderService orderService = new OrderService();
    private final CartService cartService = new CartService();

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
        boolean isApi = path.startsWith("/api/v1/orders");

        try {
            if ("/checkout".equals(path)) {
                handleCheckoutPage(req, resp, user);
            } else if (isApi && pathInfo != null && pathInfo.length() > 1) {
                handleApiOrderDetail(resp, user, pathInfo);
            } else {
                handleOrderHistory(req, resp, user, isApi);
            }
        } catch (Exception e) {
            logger.error("Error handling order request: {}", e.getMessage(), e);
            if (isApi) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Failed to retrieve order information."));
            } else {
                req.setAttribute("errorMessage", "Unable to load order information.");
                req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
            }
        }
    }

    private void handleCheckoutPage(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?empty=true");
            return;
        }
        BigDecimal grandTotal = cartService.getCartTotal(user.getId());

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("cartTotal", grandTotal);
        req.setAttribute("grandTotal", grandTotal);
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    private void handleOrderHistory(HttpServletRequest req, HttpServletResponse resp, User user, boolean isApi)
            throws ServletException, IOException {
        List<Order> orders = orderService.getBuyerOrders(user.getId());

        if (isApi) {
            List<OrderResponseDTO> dtos = orders.stream()
                    .map(OrderResponseDTO::fromOrder)
                    .collect(Collectors.toList());
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(dtos));
        } else {
            String placedOrderId = req.getParameter("placedOrderId");
            if (placedOrderId != null) {
                req.setAttribute("successMessage", "Order #" + placedOrderId + " placed successfully! Thank you for shopping with KamaleshwaranMart.");
            }
            if (req.getParameter("cancelled") != null) {
                req.setAttribute("successMessage", "Order was cancelled successfully.");
            }
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
        }
    }

    private void handleApiOrderDetail(HttpServletResponse resp, User user, String pathInfo) throws IOException {
        try {
            Long orderId = Long.parseLong(pathInfo.substring(1));
            Optional<Order> orderOpt = orderService.getOrderDetails(orderId);
            if (orderOpt.isEmpty() || (!orderOpt.get().getBuyerId().equals(user.getId()) && user.getRole() != com.kamaleshwaran.kamaleshwaranmart.model.Role.ADMIN)) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        ApiResponseDTO.error("NOT_FOUND", "Order not found."));
                return;
            }
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK,
                    ApiResponseDTO.success(OrderResponseDTO.fromOrder(orderOpt.get())));
        } catch (NumberFormatException e) {
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponseDTO.error("BAD_REQUEST", "Invalid order ID"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponseDTO.error("UNAUTHORIZED", "Please log in to manage orders."));
            return;
        }

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"))
                || req.getServletPath().startsWith("/api/v1/orders");

        String action = req.getParameter("action");

        try {
            if ("cancel".equalsIgnoreCase(action)) {
                String orderIdStr = req.getParameter("orderId");
                if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                    throw new ValidationException("Order ID is required to cancel an order.");
                }
                Long orderId = Long.parseLong(orderIdStr.trim());
                orderService.cancelOrder(user.getId(), orderId);

                if (isAjax) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK,
                            ApiResponseDTO.success("Order #" + orderId + " cancelled successfully."));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/orders?cancelled=true");
                }
                return;
            }

            // Place Order
            String shippingAddress = req.getParameter("shippingAddress");
            String shippingCity = req.getParameter("shippingCity");
            String shippingPincode = req.getParameter("shippingPincode");
            String paymentMethod = req.getParameter("paymentMethod");

            StringBuilder fullAddress = new StringBuilder();
            if (shippingAddress != null && !shippingAddress.trim().isEmpty()) fullAddress.append(shippingAddress.trim());
            if (shippingCity != null && !shippingCity.trim().isEmpty()) fullAddress.append(", ").append(shippingCity.trim());
            if (shippingPincode != null && !shippingPincode.trim().isEmpty()) fullAddress.append(" - ").append(shippingPincode.trim());

            Order order = orderService.placeOrder(user.getId(), 
                    fullAddress.length() > 0 ? fullAddress.toString() : "Standard Address",
                    paymentMethod != null ? paymentMethod : "COD");

            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_CREATED,
                        ApiResponseDTO.success(OrderResponseDTO.fromOrder(order)));
            } else {
                resp.sendRedirect(req.getContextPath() + "/orders?placedOrderId=" + order.getId());
            }

        } catch (ValidationException e) {
            logger.warn("Validation error processing order for user {}: {}", user.getId(), e.getMessage());
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponseDTO.error("CHECKOUT_ERROR", e.getMessage()));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                if ("cancel".equalsIgnoreCase(action)) {
                    handleOrderHistory(req, resp, user, false);
                } else {
                    handleCheckoutPage(req, resp, user);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing order action {} for user {}: {}", action, user.getId(), e.getMessage(), e);
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Order operation failed. Please try again."));
            } else {
                req.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
                if ("cancel".equalsIgnoreCase(action)) {
                    handleOrderHistory(req, resp, user, false);
                } else {
                    handleCheckoutPage(req, resp, user);
                }
            }
        }
    }
}
