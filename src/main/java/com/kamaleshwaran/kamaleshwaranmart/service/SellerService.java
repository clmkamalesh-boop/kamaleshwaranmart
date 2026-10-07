package com.kamaleshwaran.kamaleshwaranmart.service;

import com.kamaleshwaran.kamaleshwaranmart.dao.OrderDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.ProductDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.OrderDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.ProductDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dto.SellerStatsDTO;
import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service providing Seller Dashboard metrics and management operations.
 */
public class SellerService {
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;

    public SellerService() {
        this.productDAO = new ProductDAOImpl();
        this.orderDAO = new OrderDAOImpl();
    }

    public SellerService(ProductDAO productDAO, OrderDAO orderDAO) {
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
    }

    public SellerStatsDTO getSellerDashboardStats(Long sellerId) {
        int totalProducts = productDAO.countBySellerId(sellerId);
        int totalOrders = orderDAO.countOrdersBySeller(sellerId);
        BigDecimal revenue = orderDAO.calculateSellerRevenue(sellerId);

        List<Product> products = productDAO.findBySellerId(sellerId);
        int lowStock = (int) products.stream().filter(p -> p.getStockQty() <= 5).count();

        return new SellerStatsDTO(totalProducts, totalOrders, revenue, lowStock);
    }

    public List<Product> getSellerProducts(Long sellerId) {
        return productDAO.findBySellerId(sellerId);
    }

    public List<Order> getSellerOrders(Long sellerId) {
        return orderDAO.findBySellerId(sellerId);
    }
}
