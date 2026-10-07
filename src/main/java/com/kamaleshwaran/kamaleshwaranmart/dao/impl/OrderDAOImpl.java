package com.kamaleshwaran.kamaleshwaranmart.dao.impl;

import com.kamaleshwaran.kamaleshwaranmart.dao.OrderDAO;
import com.kamaleshwaran.kamaleshwaranmart.exception.DatabaseException;
import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.OrderStatus;
import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of OrderDAO.
 */
public class OrderDAOImpl implements OrderDAO {
    private static final Logger logger = LoggerFactory.getLogger(OrderDAOImpl.class);

    private static final String BASE_SELECT =
            "SELECT o.id, o.buyer_id, o.status, o.total_amount, o.created_at, " +
            "u.name AS buyer_name, u.email AS buyer_email " +
            "FROM orders o " +
            "JOIN users u ON o.buyer_id = u.id ";

    @Override
    public Order create(Connection conn, Order order) {
        String sql = "INSERT INTO orders (buyer_id, status, total_amount, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, order.getBuyerId());
            stmt.setString(2, order.getStatus().name());
            stmt.setBigDecimal(3, order.getTotalAmount());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating order failed, no rows affected.");
            }

            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    order.setId(gk.getLong(1));
                }
            }
            return order;
        } catch (SQLException e) {
            logger.error("Error creating order: {}", e.getMessage());
            throw new DatabaseException("Failed to create order", e);
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = BASE_SELECT + " WHERE o.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding order by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find order", e);
        }
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        String sql = BASE_SELECT + " WHERE o.buyer_id = ? ORDER BY o.created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
            return orders;
        } catch (SQLException e) {
            logger.error("Error finding orders for buyer {}: {}", buyerId, e.getMessage());
            throw new DatabaseException("Failed to retrieve buyer orders", e);
        }
    }

    @Override
    public List<Order> findAllOrders() {
        String sql = BASE_SELECT + " ORDER BY o.created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            return orders;
        } catch (SQLException e) {
            logger.error("Error finding all orders: {}", e.getMessage());
            throw new DatabaseException("Failed to retrieve orders", e);
        }
    }

    @Override
    public List<Order> findBySellerId(Long sellerId) {
        String sql = "SELECT DISTINCT o.id, o.buyer_id, o.status, o.total_amount, o.created_at, " +
                     "u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE p.seller_id = ? " +
                     "ORDER BY o.created_at DESC";

        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
            return orders;
        } catch (SQLException e) {
            logger.error("Error finding orders for seller {}: {}", sellerId, e.getMessage());
            throw new DatabaseException("Failed to retrieve seller orders", e);
        }
    }

    @Override
    public boolean updateStatus(Long orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setLong(2, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating status for order {}: {}", orderId, e.getMessage());
            throw new DatabaseException("Failed to update order status", e);
        }
    }

    @Override
    public int countTotalOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting total orders: {}", e.getMessage());
            throw new DatabaseException("Failed to count orders", e);
        }
    }

    @Override
    public int countOrdersBySeller(Long sellerId) {
        String sql = "SELECT COUNT(DISTINCT o.id) FROM orders o " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE p.seller_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting seller orders: {}", e.getMessage());
            throw new DatabaseException("Failed to count seller orders", e);
        }
    }

    @Override
    public BigDecimal calculatePlatformRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0.00) FROM orders WHERE status <> 'CANCELLED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            logger.error("Error calculating platform revenue: {}", e.getMessage());
            throw new DatabaseException("Failed to calculate revenue", e);
        }
    }

    @Override
    public BigDecimal calculateSellerRevenue(Long sellerId) {
        String sql = "SELECT COALESCE(SUM(oi.quantity * oi.unit_price), 0.00) " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN orders o ON oi.order_id = o.id " +
                     "WHERE p.seller_id = ? AND o.status <> 'CANCELLED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            logger.error("Error calculating seller revenue: {}", e.getMessage());
            throw new DatabaseException("Failed to calculate seller revenue", e);
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setBuyerId(rs.getLong("buyer_id"));
        order.setStatus(OrderStatus.fromString(rs.getString("status")));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setBuyerName(rs.getString("buyer_name"));
        order.setBuyerEmail(rs.getString("buyer_email"));
        return order;
    }
}
