package com.kamaleshwaran.kamaleshwaranmart.service;

import com.kamaleshwaran.kamaleshwaranmart.dao.ProductDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.ProductDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.exception.AuthorizationException;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import com.kamaleshwaran.kamaleshwaranmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for product catalog management, searching, filtering, and CRUD.
 */
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getAllProducts(String search, String category, String sortBy) {
        return productDAO.findAll(search, category, sortBy);
    }

    public Optional<Product> getProductById(Long id) {
        if (id == null) return Optional.empty();
        return productDAO.findById(id);
    }

    public List<Product> getProductsBySeller(Long sellerId) {
        if (sellerId == null) return List.of();
        return productDAO.findBySellerId(sellerId);
    }

    public List<String> getAllCategories() {
        return productDAO.findAllCategories();
    }

    public Product createProduct(Long sellerId, String name, String description, BigDecimal price, 
                                 int stockQty, String category, String imageUrl) {
        ValidationUtil.validateRequiredString(name, "Product Name", 255);
        ValidationUtil.validatePrice(price);
        ValidationUtil.validateStock(stockQty);
        ValidationUtil.validateRequiredString(category, "Category", 100);

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80";
        }

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(name.trim());
        product.setDescription(description != null ? description.trim() : "");
        product.setPrice(price);
        product.setStockQty(stockQty);
        product.setCategory(category.trim());
        product.setImageUrl(imageUrl.trim());

        Product created = productDAO.create(product);
        logger.info("Product created successfully: id={}, sellerId={}, name={}", created.getId(), sellerId, created.getName());
        return created;
    }

    public boolean updateProduct(Long sellerId, Long productId, String name, String description, 
                                 BigDecimal price, int stockQty, String category, String imageUrl) {
        Optional<Product> existing = productDAO.findById(productId);
        if (existing.isEmpty()) {
            throw new ValidationException("Product not found.");
        }
        if (!existing.get().getSellerId().equals(sellerId)) {
            throw new AuthorizationException("You are not authorized to modify this product.");
        }

        ValidationUtil.validateRequiredString(name, "Product Name", 255);
        ValidationUtil.validatePrice(price);
        ValidationUtil.validateStock(stockQty);
        ValidationUtil.validateRequiredString(category, "Category", 100);

        Product p = existing.get();
        p.setName(name.trim());
        p.setDescription(description != null ? description.trim() : "");
        p.setPrice(price);
        p.setStockQty(stockQty);
        p.setCategory(category.trim());
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            p.setImageUrl(imageUrl.trim());
        }

        return productDAO.update(p);
    }

    public boolean deleteProduct(Long requesterId, Long productId, boolean isAdmin) {
        Optional<Product> existing = productDAO.findById(productId);
        if (existing.isEmpty()) {
            return false;
        }
        if (!isAdmin && !existing.get().getSellerId().equals(requesterId)) {
            throw new AuthorizationException("You are not authorized to delete this product.");
        }
        return productDAO.deleteById(productId);
    }
}
