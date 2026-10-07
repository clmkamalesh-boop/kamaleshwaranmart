package com.kamaleshwaran.kamaleshwaranmart.dao;

import com.kamaleshwaran.kamaleshwaranmart.model.OrderItem;

import java.sql.Connection;
import java.util.List;

/**
 * Data Access Object interface for individual items in an order.
 */
public interface OrderItemDAO {
    OrderItem create(Connection conn, OrderItem item);
    List<OrderItem> createBatch(Connection conn, List<OrderItem> items);
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findBySellerId(Long sellerId);
}
