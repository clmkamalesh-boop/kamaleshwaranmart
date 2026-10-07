package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.CartItem;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.service.CartService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing shopping cart views and item mutations.
 */
@WebServlet(urlPatterns = {"/cart", "/api/v1/cart", "/api/v1/cart/*"})
public class CartServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CartServlet.class);
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=auth_required");
            return;
        }

        boolean isApi = req.getServletPath().startsWith("/api/v1/cart");

        try {
            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            BigDecimal cartTotal = cartService.getCartTotal(user.getId());
            int itemCount = cartService.getItemCount(user.getId());

            if (isApi) {
                Map<String, Object> data = new HashMap<>();
                data.put("items", cartItems);
                data.put("total", cartTotal);
                data.put("itemCount", itemCount);
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(data));
            } else {
                req.setAttribute("cartItems", cartItems);
                req.setAttribute("cartTotal", cartTotal);
                req.setAttribute("itemCount", itemCount);
                req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            logger.error("Error loading cart for user {}: {}", user.getId(), e.getMessage(), e);
            if (isApi) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Failed to retrieve cart items"));
            } else {
                req.setAttribute("errorMessage", "Unable to load cart at this time.");
                req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
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
                    ApiResponseDTO.error("UNAUTHORIZED", "Please log in to manage your cart."));
            return;
        }

        String pathInfo = req.getPathInfo();
        String action = req.getParameter("action");
        if (action == null && pathInfo != null) {
            action = pathInfo.replace("/", "");
        }

        Long productId = null;
        Long cartItemId = null;
        Integer quantity = null;

        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try {
                JsonObject json = JsonParser.parseReader(req.getReader()).getAsJsonObject();
                if (json.has("action") && !json.get("action").isJsonNull()) {
                    action = json.get("action").getAsString();
                }
                if (json.has("productId") && !json.get("productId").isJsonNull()) {
                    productId = json.get("productId").getAsLong();
                }
                if (json.has("cartItemId") && !json.get("cartItemId").isJsonNull()) {
                    cartItemId = json.get("cartItemId").getAsLong();
                }
                if (json.has("quantity") && !json.get("quantity").isJsonNull()) {
                    quantity = json.get("quantity").getAsInt();
                }
            } catch (Exception e) {
                logger.warn("Could not parse JSON body in CartServlet: {}", e.getMessage());
            }
        }

        if (productId == null && req.getParameter("productId") != null) {
            try { productId = Long.parseLong(req.getParameter("productId").trim()); } catch (Exception ignored) {}
        }
        if (cartItemId == null && req.getParameter("cartItemId") != null) {
            try { cartItemId = Long.parseLong(req.getParameter("cartItemId").trim()); } catch (Exception ignored) {}
        }
        if (quantity == null && req.getParameter("quantity") != null) {
            try { quantity = Integer.parseInt(req.getParameter("quantity").trim()); } catch (Exception ignored) {}
        }

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"))
                || (contentType != null && contentType.toLowerCase().contains("application/json"))
                || req.getServletPath().startsWith("/api/v1/cart");

        try {
            if ("add".equalsIgnoreCase(action)) {
                if (productId == null) {
                    throw new ValidationException("Product ID is required to add to cart.");
                }
                int qty = (quantity != null && quantity > 0) ? quantity : 1;
                CartItem item = cartService.addToCart(user.getId(), productId, qty);
                int totalItems = cartService.getItemCount(user.getId());

                if (isAjax) {
                    Map<String, Object> resData = new HashMap<>();
                    resData.put("item", item);
                    resData.put("cartCount", totalItems);
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(resData));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/cart?added=true");
                }

            } else if ("update".equalsIgnoreCase(action)) {
                if (cartItemId == null || quantity == null) {
                    throw new ValidationException("Cart item ID and quantity are required.");
                }

                boolean updated = cartService.updateQuantity(user.getId(), cartItemId, quantity);
                BigDecimal newTotal = cartService.getCartTotal(user.getId());
                int totalItems = cartService.getItemCount(user.getId());

                if (isAjax) {
                    Map<String, Object> resData = new HashMap<>();
                    resData.put("updated", updated);
                    resData.put("cartTotal", newTotal);
                    resData.put("cartCount", totalItems);
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(resData));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/cart");
                }

            } else if ("remove".equalsIgnoreCase(action)) {
                if (cartItemId == null) {
                    throw new ValidationException("Cart item ID is required to remove item.");
                }

                boolean removed = cartService.removeItem(user.getId(), cartItemId);
                BigDecimal newTotal = cartService.getCartTotal(user.getId());
                int totalItems = cartService.getItemCount(user.getId());

                if (isAjax) {
                    Map<String, Object> resData = new HashMap<>();
                    resData.put("removed", removed);
                    resData.put("cartTotal", newTotal);
                    resData.put("cartCount", totalItems);
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(resData));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/cart");
                }

            } else if ("clear".equalsIgnoreCase(action)) {
                boolean cleared = cartService.clearCart(user.getId());

                if (isAjax) {
                    Map<String, Object> resData = new HashMap<>();
                    resData.put("cleared", cleared);
                    resData.put("cartTotal", BigDecimal.ZERO);
                    resData.put("cartCount", 0);
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(resData));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/cart");
                }

            } else {
                if (isAjax) {
                    JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                            ApiResponseDTO.error("BAD_REQUEST", "Unknown cart action: " + action));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/cart");
                }
            }

        } catch (ValidationException e) {
            logger.warn("Cart validation error: {}", e.getMessage());
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponseDTO.error("VALIDATION_ERROR", e.getMessage()));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
            }
        } catch (Exception e) {
            logger.error("Cart mutation error: {}", e.getMessage(), e);
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Cart operation failed."));
            } else {
                req.setAttribute("errorMessage", "Could not update cart.");
                doGet(req, resp);
            }
        }
    }
}
