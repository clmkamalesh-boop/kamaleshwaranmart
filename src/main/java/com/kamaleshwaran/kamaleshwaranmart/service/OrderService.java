package com.kamaleshwaran.kamaleshwaranmart.service;

import com.kamaleshwaran.kamaleshwaranmart.dao.CartDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.OrderDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.OrderItemDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.ProductDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.CartDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.OrderDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.OrderItemDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.ProductDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.exception.DatabaseException;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.*;
import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service managing order placement, transactions, mock payments, and lifecycle tracking.
 */
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderService() {
        this.orderDAO = new OrderDAOImpl();
        this.orderItemDAO = new OrderItemDAOImpl();
        this.cartDAO = new CartDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    public OrderService(OrderDAO orderDAO, OrderItemDAO orderItemDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    /**
     * Executes atomic checkout transaction:
     * 1. Validates cart items and inventory
     * 2. Creates order header
     * 3. Creates order items
     * 4. Decrements product stock
     * 5. Clears shopping cart
     * 6. Commits transaction or rolls back on any failure
     */
    public Order placeOrder(Long buyerId) {
        if (buyerId == null) {
            throw new ValidationException("Buyer ID is required.");
        }

        List<CartItem> cartItems = cartDAO.findByUserId(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("Your cart is empty. Please add items before checking out.");
        }

        BigDecimal grandTotal = BigDecimal.ZERO;

        // Verify stock for all items
        for (CartItem item : cartItems) {
            Product p = productDAO.findById(item.getProductId())
                    .orElseThrow(() -> new ValidationException("Product " + item.getProductName() + " no longer exists."));
            if (p.getStockQty() < item.getQuantity()) {
                throw new ValidationException("Insufficient stock for " + p.getName() + 
                        ". Only " + p.getStockQty() + " available.");
            }
            grandTotal = grandTotal.add(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Begin ACID transaction

            // 1. Create Order
            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setStatus(OrderStatus.PENDING);
            order.setTotalAmount(grandTotal);

            Order savedOrder = orderDAO.create(conn, order);

            // 2. Create Order Items & Decrement Stock
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                OrderItem oi = new OrderItem();
                oi.setOrderId(savedOrder.getId());
                oi.setProductId(item.getProductId());
                oi.setQuantity(item.getQuantity());
                oi.setUnitPrice(item.getProductPrice());

                orderItemDAO.create(conn, oi);
                orderItems.add(oi);

                // Decrement stock atomically
                boolean stockReduced = productDAO.updateStock(conn, item.getProductId(), -item.getQuantity());
                if (!stockReduced) {
                    throw new ValidationException("Failed to update stock for product: " + item.getProductName());
                }
            }

            // 3. Clear Cart
            cartDAO.clearCart(conn, buyerId);

            conn.commit(); // Commit transaction
            logger.info("Order #{} placed successfully for buyerId={}, total={}", savedOrder.getId(), buyerId, grandTotal);

            savedOrder.setItems(orderItems);
            return savedOrder;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.warn("Transaction rolled back for order checkout: {}", e.getMessage());
                } catch (SQLException rollbackEx) {
                    logger.error("Error during transaction rollback: {}", rollbackEx.getMessage());
                }
            }
            if (e instanceof ValidationException) {
                throw (ValidationException) e;
            }
            throw new DatabaseException("Failed to complete checkout: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error resetting connection autoCommit: {}", e.getMessage());
                }
            }
        }
    }

    public Optional<Order> getOrderDetails(Long orderId) {
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = orderItemDAO.findByOrderId(orderId);
            order.setItems(items);
            return Optional.of(order);
        }
        return Optional.empty();
    }

    public List<Order> getBuyerOrders(Long buyerId) {
        List<Order> orders = orderDAO.findByBuyerId(buyerId);
        for (Order o : orders) {
            o.setItems(orderItemDAO.findByOrderId(o.getId()));
        }
        return orders;
    }

    public List<Order> getSellerOrders(Long sellerId) {
        List<Order> orders = orderDAO.findBySellerId(sellerId);
        for (Order o : orders) {
            o.setItems(orderItemDAO.findByOrderId(o.getId()));
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderDAO.findAllOrders();
        for (Order o : orders) {
            o.setItems(orderItemDAO.findByOrderId(o.getId()));
        }
        return orders;
    }

    public boolean updateStatus(Long orderId, OrderStatus status) {
        if (orderId == null || status == null) {
            throw new ValidationException("Order ID and Status are required.");
        }
        return orderDAO.updateStatus(orderId, status);
    }

    public Order placeOrder(Long buyerId, String shippingAddress, String paymentMethod) {
        Order order = placeOrder(buyerId);
        if (order != null) {
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);
        }
        return order;
    }

    public boolean cancelOrder(Long userId, Long orderId) {
        if (userId == null || orderId == null) {
            throw new ValidationException("User ID and Order ID are required.");
        }
        Optional<Order> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new ValidationException("Order not found.");
        }
        Order order = orderOpt.get();
        if (!order.getBuyerId().equals(userId)) {
            throw new ValidationException("You are not authorized to cancel this order.");
        }
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("Cannot cancel an order that is already " + order.getStatus() + ".");
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            boolean updated = orderDAO.updateStatus(orderId, OrderStatus.CANCELLED);
            if (!updated) {
                throw new ValidationException("Failed to cancel order.");
            }

            // Restore stock
            List<OrderItem> items = orderItemDAO.findByOrderId(orderId);
            for (OrderItem item : items) {
                productDAO.updateStock(conn, item.getProductId(), item.getQuantity());
            }

            conn.commit();
            logger.info("Order #{} cancelled by user #{}, inventory stock restored.", orderId, userId);
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            if (e instanceof ValidationException) {
                throw (ValidationException) e;
            }
            throw new DatabaseException("Failed to cancel order: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }
}
