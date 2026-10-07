package com.kamaleshwaran.kamaleshwaranmart.dao;

import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.OrderStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Order management.
 */
public interface OrderDAO {
    Order create(Connection conn, Order order);
    Optional<Order> findById(Long id);
    List<Order> findByBuyerId(Long buyerId);
    List<Order> findAllOrders();
    List<Order> findBySellerId(Long sellerId);
    boolean updateStatus(Long orderId, OrderStatus status);
    int countTotalOrders();
    int countOrdersBySeller(Long sellerId);
    BigDecimal calculatePlatformRevenue();
    BigDecimal calculateSellerRevenue(Long sellerId);
}
