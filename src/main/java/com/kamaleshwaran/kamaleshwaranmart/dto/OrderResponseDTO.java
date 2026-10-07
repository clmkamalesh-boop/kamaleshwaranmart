package com.kamaleshwaran.kamaleshwaranmart.dto;

import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.OrderItem;
import com.kamaleshwaran.kamaleshwaranmart.model.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Order response data transfer object.
 */
public class OrderResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long buyerId;
    private String buyerName;
    private String buyerEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Timestamp createdAt;
    private List<OrderItem> items = new ArrayList<>();

    public OrderResponseDTO() {
    }

    public static OrderResponseDTO fromOrder(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setBuyerId(order.getBuyerId());
        dto.setBuyerName(order.getBuyerName());
        dto.setBuyerEmail(order.getBuyerEmail());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
