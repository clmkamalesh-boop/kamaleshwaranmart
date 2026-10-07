package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import com.kamaleshwaran.kamaleshwaranmart.service.ProductService;
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
 * Controller for the marketplace home landing page.
 */
@WebServlet(urlPatterns = {"", "/home", "/index"})
public class HomeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Product> featuredProducts = productService.getAllProducts(null, null, "rating");
            List<String> categories = productService.getAllCategories();

            req.setAttribute("featuredProducts", featuredProducts);
            req.setAttribute("categories", categories);

            req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Error loading home page: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", "Unable to load homepage products at this time.");
            req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
        }
    }
}
