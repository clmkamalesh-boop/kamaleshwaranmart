package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.Review;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.service.ReviewService;
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
import java.util.List;

/**
 * Controller for product review submissions and customer feedback ratings.
 */
@WebServlet(urlPatterns = {"/reviews", "/api/v1/reviews", "/api/v1/reviews/*"})
public class ReviewServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServlet.class);
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productIdParam = req.getParameter("productId");
        if (productIdParam != null) {
            try {
                Long productId = Long.parseLong(productIdParam);
                List<Review> reviews = reviewService.getReviewsForProduct(productId);
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(reviews));
                return;
            } catch (NumberFormatException ignored) {}
        }
        JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                ApiResponseDTO.error("BAD_REQUEST", "Valid productId parameter required"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponseDTO.error("UNAUTHORIZED", "Please log in to submit a review."));
            return;
        }

        String contentType = req.getContentType();
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"))
                || (contentType != null && contentType.toLowerCase().contains("application/json"))
                || req.getServletPath().startsWith("/api/v1/reviews");

        Long productId = null;
        Integer rating = null;
        String comment = null;

        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try {
                JsonObject json = JsonParser.parseReader(req.getReader()).getAsJsonObject();
                if (json.has("productId") && !json.get("productId").isJsonNull()) {
                    productId = json.get("productId").getAsLong();
                }
                if (json.has("rating") && !json.get("rating").isJsonNull()) {
                    rating = json.get("rating").getAsInt();
                }
                if (json.has("comment") && !json.get("comment").isJsonNull()) {
                    comment = json.get("comment").getAsString();
                }
            } catch (Exception e) {
                logger.warn("Could not parse JSON in ReviewServlet: {}", e.getMessage());
            }
        }

        if (productId == null && req.getParameter("productId") != null) {
            try { productId = Long.parseLong(req.getParameter("productId").trim()); } catch (Exception ignored) {}
        }
        if (rating == null && req.getParameter("rating") != null) {
            try { rating = Integer.parseInt(req.getParameter("rating").trim()); } catch (Exception ignored) {}
        }
        if (comment == null) {
            comment = req.getParameter("comment");
        }

        try {
            if (productId == null || rating == null) {
                throw new ValidationException("Product ID and rating are required.");
            }

            Review review = reviewService.addReview(user.getId(), productId, rating, comment);

            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_CREATED, ApiResponseDTO.success(review));
            } else {
                resp.sendRedirect(req.getContextPath() + "/product-details?id=" + productId + "&reviewAdded=true");
            }

        } catch (ValidationException e) {
            logger.warn("Validation error submitting review: {}", e.getMessage());
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponseDTO.error("VALIDATION_ERROR", e.getMessage()));
            } else {
                resp.sendRedirect(req.getContextPath() + "/product-details?id=" + (productId != null ? productId : "") + "&reviewError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
        } catch (Exception e) {
            logger.error("Error submitting review: {}", e.getMessage(), e);
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Failed to submit review."));
            } else {
                resp.sendRedirect(req.getContextPath() + "/product-details?id=" + (productId != null ? productId : "") + "&reviewError=Server+error");
            }
        }
    }
}
