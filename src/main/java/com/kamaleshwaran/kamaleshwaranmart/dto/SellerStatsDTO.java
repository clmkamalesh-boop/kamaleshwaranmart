package com.kamaleshwaran.kamaleshwaranmart.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Statistics metrics for Seller Dashboard.
 */
public class SellerStatsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalProducts;
    private int totalOrders;
    private BigDecimal totalRevenue;
    private int lowStockCount;

    public SellerStatsDTO() {
        this.totalRevenue = BigDecimal.ZERO;
    }

    public SellerStatsDTO(int totalProducts, int totalOrders, BigDecimal totalRevenue, int lowStockCount) {
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.lowStockCount = lowStockCount;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    public BigDecimal getRevenue() {
        return getTotalRevenue();
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }
}
