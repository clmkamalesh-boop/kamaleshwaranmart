package com.kamaleshwaran.kamaleshwaranmart.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Statistics metrics for Administrator Dashboard.
 */
public class AdminStatsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalUsers;
    private int totalBuyers;
    private int totalSellers;
    private int totalProducts;
    private int totalOrders;
    private BigDecimal totalPlatformRevenue;

    public AdminStatsDTO() {
        this.totalPlatformRevenue = BigDecimal.ZERO;
    }

    public AdminStatsDTO(int totalUsers, int totalBuyers, int totalSellers, 
                         int totalProducts, int totalOrders, BigDecimal totalPlatformRevenue) {
        this.totalUsers = totalUsers;
        this.totalBuyers = totalBuyers;
        this.totalSellers = totalSellers;
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.totalPlatformRevenue = totalPlatformRevenue != null ? totalPlatformRevenue : BigDecimal.ZERO;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalBuyers() {
        return totalBuyers;
    }

    public void setTotalBuyers(int totalBuyers) {
        this.totalBuyers = totalBuyers;
    }

    public int getTotalSellers() {
        return totalSellers;
    }

    public void setTotalSellers(int totalSellers) {
        this.totalSellers = totalSellers;
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

    public BigDecimal getTotalPlatformRevenue() {
        return totalPlatformRevenue != null ? totalPlatformRevenue : BigDecimal.ZERO;
    }

    public void setTotalPlatformRevenue(BigDecimal totalPlatformRevenue) {
        this.totalPlatformRevenue = totalPlatformRevenue;
    }
}
