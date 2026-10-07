package com.kamaleshwaran.kamaleshwaranmart.dao.impl;

import com.kamaleshwaran.kamaleshwaranmart.dao.CartDAO;
import com.kamaleshwaran.kamaleshwaranmart.exception.DatabaseException;
import com.kamaleshwaran.kamaleshwaranmart.model.CartItem;
import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CartDAO.
 */
public class CartDAOImpl implements CartDAO {
    private static final Logger logger = LoggerFactory.getLogger(CartDAOImpl.class);

    @Override
    public CartItem addToCart(CartItem item) {
        // Check if item exists in cart already
        Optional<CartItem> existing = findByUserAndProduct(item.getUserId(), item.getProductId());
        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + item.getQuantity();
            updateQuantity(existing.get().getId(), item.getUserId(), newQty);
            item.setId(existing.get().getId());
            item.setQuantity(newQty);
            return item;
        }

        String sql = "INSERT INTO cart_items (user_id, product_id, quantity, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, item.getUserId());
            stmt.setLong(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    item.setId(gk.getLong(1));
                }
            }
            return item;
        } catch (SQLException e) {
            logger.error("Error adding to cart: {}", e.getMessage());
            throw new DatabaseException("Failed to add product to cart", e);
        }
    }

    @Override
    public boolean updateQuantity(Long cartItemId, Long userId, int quantity) {
        if (quantity <= 0) {
            return removeItem(cartItemId, userId);
        }
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setLong(2, cartItemId);
            stmt.setLong(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating cart quantity: {}", e.getMessage());
            throw new DatabaseException("Failed to update cart quantity", e);
        }
    }

    @Override
    public boolean removeItem(Long cartItemId, Long userId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cartItemId);
            stmt.setLong(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error removing cart item: {}", e.getMessage());
            throw new DatabaseException("Failed to remove item from cart", e);
        }
    }

    @Override
    public boolean clearCart(Long userId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            return clearCart(conn, userId);
        } catch (SQLException e) {
            logger.error("Error clearing cart for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to clear cart", e);
        }
    }

    @Override
    public boolean clearCart(Connection conn, Long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Error clearing cart for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to clear cart", e);
        }
    }

    @Override
    public List<CartItem> findByUserId(Long userId) {
        String sql = "SELECT c.id, c.user_id, c.product_id, c.quantity, c.created_at, " +
                     "p.name AS product_name, p.price AS product_price, p.image_url AS product_image, " +
                     "p.stock_qty, p.category, u.name AS seller_name " +
                     "FROM cart_items c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "JOIN users u ON p.seller_id = u.id " +
                     "WHERE c.user_id = ? " +
                     "ORDER BY c.created_at DESC";

        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductPrice(rs.getBigDecimal("product_price"));
                    item.setProductImageUrl(rs.getString("product_image"));
                    item.setStockQty(rs.getInt("stock_qty"));
                    item.setCategory(rs.getString("category"));
                    item.setSellerName(rs.getString("seller_name"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException e) {
            logger.error("Error getting cart for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to retrieve cart items", e);
        }
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) {
        String sql = "SELECT id, user_id, product_id, quantity, created_at FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CartItem item = new CartItem(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getLong("product_id"),
                            rs.getInt("quantity"),
                            rs.getTimestamp("created_at")
                    );
                    return Optional.of(item);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding cart item: {}", e.getMessage());
            throw new DatabaseException("Failed to find cart item", e);
        }
    }

    @Override
    public BigDecimal calculateCartTotal(Long userId) {
        String sql = "SELECT COALESCE(SUM(c.quantity * p.price), 0.00) FROM cart_items c " +
                     "JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            logger.error("Error calculating cart total: {}", e.getMessage());
            throw new DatabaseException("Failed to calculate cart total", e);
        }
    }

    @Override
    public int countTotalItems(Long userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting cart items: {}", e.getMessage());
            throw new DatabaseException("Failed to count cart items", e);
        }
    }
}
