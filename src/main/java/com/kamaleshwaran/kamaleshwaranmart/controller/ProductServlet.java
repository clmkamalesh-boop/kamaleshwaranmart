package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.dto.ProductResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import com.kamaleshwaran.kamaleshwaranmart.model.Review;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.service.ProductService;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for product browsing, searching, category filtering, and product details.
 */
@WebServlet(urlPatterns = {"/products", "/product-details", "/api/v1/products", "/api/v1/products/*"})
public class ProductServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductServlet.class);

    private final ProductService productService = new ProductService();
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();

        boolean isApi = path.startsWith("/api/v1/products");

        try {
            if ("/product-details".equals(path) || (isApi && pathInfo != null && pathInfo.length() > 1)) {
                handleProductDetails(req, resp, isApi, pathInfo);
            } else {
                handleProductList(req, resp, isApi);
            }
        } catch (Exception e) {
            logger.error("Error processing product request: {}", e.getMessage(), e);
            if (isApi) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Failed to retrieve products"));
            } else {
                req.setAttribute("errorMessage", "Unable to load products at this time.");
                req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
            }
        }
    }

    private void handleProductList(HttpServletRequest req, HttpServletResponse resp, boolean isApi)
            throws ServletException, IOException {
        String search = req.getParameter("search");
        if (search == null) search = req.getParameter("q");
        String category = req.getParameter("category");
        String sortBy = req.getParameter("sort");

        List<Product> products = productService.getAllProducts(search, category, sortBy);
        List<String> categories = productService.getAllCategories();

        if (isApi) {
            List<ProductResponseDTO> dtos = products.stream()
                    .map(ProductResponseDTO::fromProduct)
                    .collect(Collectors.toList());
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK, ApiResponseDTO.success(dtos));
        } else {
            req.setAttribute("products", products);
            req.setAttribute("categories", categories);
            req.setAttribute("searchQuery", search);
            req.setAttribute("selectedCategory", category);
            req.setAttribute("selectedSort", sortBy);
            req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
        }
    }

    private void handleProductDetails(HttpServletRequest req, HttpServletResponse resp, boolean isApi, String pathInfo)
            throws ServletException, IOException {
        Long productId = null;
        if (isApi && pathInfo != null) {
            try {
                productId = Long.parseLong(pathInfo.substring(1));
            } catch (NumberFormatException ignored) {}
        } else {
            String idParam = req.getParameter("id");
            if (idParam != null) {
                try {
                    productId = Long.parseLong(idParam);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (productId == null) {
            if (isApi) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponseDTO.error("BAD_REQUEST", "Valid product ID required."));
            } else {
                resp.sendRedirect(req.getContextPath() + "/products");
            }
            return;
        }

        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            if (isApi) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        ApiResponseDTO.error("NOT_FOUND", "Product not found."));
            } else {
                req.setAttribute("errorMessage", "The requested product does not exist.");
                req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
            }
            return;
        }

        Product product = productOpt.get();
        List<Review> reviews = reviewService.getReviewsForProduct(productId);

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        boolean canReview = false;
        if (currentUser != null) {
            canReview = reviewService.canUserReview(currentUser.getId(), productId);
        }

        if (isApi) {
            JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK,
                    ApiResponseDTO.success(ProductResponseDTO.fromProduct(product)));
        } else {
            req.setAttribute("product", product);
            req.setAttribute("reviews", reviews);
            req.setAttribute("canReview", canReview);
            req.getRequestDispatcher("/WEB-INF/views/product-details.jsp").forward(req, resp);
        }
    }
}
