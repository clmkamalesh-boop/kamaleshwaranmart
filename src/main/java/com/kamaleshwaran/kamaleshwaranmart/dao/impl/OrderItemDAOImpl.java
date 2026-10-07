package com.kamaleshwaran.kamaleshwaranmart.dao.impl;

import com.kamaleshwaran.kamaleshwaranmart.dao.OrderItemDAO;
import com.kamaleshwaran.kamaleshwaranmart.exception.DatabaseException;
import com.kamaleshwaran.kamaleshwaranmart.model.OrderItem;
import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of OrderItemDAO.
 */
public class OrderItemDAOImpl implements OrderItemDAO {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemDAOImpl.class);

    @Override
    public OrderItem create(Connection conn, OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, item.getOrderId());
            stmt.setLong(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getUnitPrice());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    item.setId(gk.getLong(1));
                }
            }
            return item;
        } catch (SQLException e) {
            logger.error("Error creating order item: {}", e.getMessage());
            throw new DatabaseException("Failed to save order item", e);
        }
    }

    @Override
    public List<OrderItem> createBatch(Connection conn, List<OrderItem> items) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (OrderItem item : items) {
                stmt.setLong(1, item.getOrderId());
                stmt.setLong(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setBigDecimal(4, item.getUnitPrice());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return items;
        } catch (SQLException e) {
            logger.error("Error batch inserting order items: {}", e.getMessage());
            throw new DatabaseException("Failed to batch save order items", e);
        }
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image, p.seller_id " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ? " +
                     "ORDER BY oi.id ASC";

        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image"));
                    item.setSellerId(rs.getLong("seller_id"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException e) {
            logger.error("Error finding order items for order {}: {}", orderId, e.getMessage());
            throw new DatabaseException("Failed to retrieve order items", e);
        }
    }

    @Override
    public List<OrderItem> findBySellerId(Long sellerId) {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image, p.seller_id " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE p.seller_id = ? " +
                     "ORDER BY oi.created_at DESC";

        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image"));
                    item.setSellerId(rs.getLong("seller_id"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException e) {
            logger.error("Error finding order items for seller {}: {}", sellerId, e.getMessage());
            throw new DatabaseException("Failed to retrieve seller items", e);
        }
    }
}
