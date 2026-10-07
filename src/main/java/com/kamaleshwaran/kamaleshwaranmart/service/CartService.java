package com.kamaleshwaran.kamaleshwaranmart.service;

import com.kamaleshwaran.kamaleshwaranmart.dao.CartDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.ProductDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.CartDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.ProductDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.CartItem;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service managing shopping cart operations.
 */
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public List<CartItem> getCartItems(Long userId) {
        return cartDAO.findByUserId(userId);
    }

    public CartItem addToCart(Long userId, Long productId, int quantity) {
        if (userId == null || productId == null) {
            throw new ValidationException("User and product IDs are required.");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero.");
        }

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found."));

        if (product.getStockQty() < quantity) {
            throw new ValidationException("Insufficient stock. Only " + product.getStockQty() + " items available.");
        }

        // Check if item already in cart
        Optional<CartItem> existing = cartDAO.findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            int totalDesired = existing.get().getQuantity() + quantity;
            if (totalDesired > product.getStockQty()) {
                throw new ValidationException("Cannot add " + quantity + " more. Cart already has " + 
                        existing.get().getQuantity() + " and available stock is " + product.getStockQty() + ".");
            }
        }

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(quantity);

        CartItem saved = cartDAO.addToCart(item);
        logger.info("Product added to cart: userId={}, productId={}, quantity={}", userId, productId, quantity);
        return saved;
    }

    public boolean updateQuantity(Long userId, Long cartItemId, int quantity) {
        if (userId == null || cartItemId == null) {
            throw new ValidationException("Invalid cart item.");
        }
        if (quantity <= 0) {
            return cartDAO.removeItem(cartItemId, userId);
        }

        // Check stock availability
        List<CartItem> userItems = cartDAO.findByUserId(userId);
        CartItem target = userItems.stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Cart item not found."));

        if (quantity > target.getStockQty()) {
            throw new ValidationException("Requested quantity (" + quantity + ") exceeds stock available (" + target.getStockQty() + ").");
        }

        return cartDAO.updateQuantity(cartItemId, userId, quantity);
    }

    public boolean removeItem(Long userId, Long cartItemId) {
        if (userId == null || cartItemId == null) {
            return false;
        }
        return cartDAO.removeItem(cartItemId, userId);
    }

    public BigDecimal getCartTotal(Long userId) {
        return cartDAO.calculateCartTotal(userId);
    }

    public int getItemCount(Long userId) {
        return cartDAO.countTotalItems(userId);
    }

    public boolean clearCart(Long userId) {
        if (userId == null) {
            return false;
        }
        return cartDAO.clearCart(userId);
    }
}
