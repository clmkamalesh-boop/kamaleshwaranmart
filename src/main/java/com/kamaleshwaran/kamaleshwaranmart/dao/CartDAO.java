package com.kamaleshwaran.kamaleshwaranmart.dao;

import com.kamaleshwaran.kamaleshwaranmart.model.CartItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Cart items.
 */
public interface CartDAO {
    CartItem addToCart(CartItem item);
    boolean updateQuantity(Long cartItemId, Long userId, int quantity);
    boolean removeItem(Long cartItemId, Long userId);
    boolean clearCart(Long userId);
    boolean clearCart(Connection conn, Long userId);
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId);
    BigDecimal calculateCartTotal(Long userId);
    int countTotalItems(Long userId);
}
