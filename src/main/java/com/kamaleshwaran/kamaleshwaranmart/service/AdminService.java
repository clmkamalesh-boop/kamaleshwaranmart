package com.kamaleshwaran.kamaleshwaranmart.service;

import com.kamaleshwaran.kamaleshwaranmart.dao.OrderDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.ProductDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.UserDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.OrderDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.ProductDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.UserDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dto.AdminStatsDTO;
import com.kamaleshwaran.kamaleshwaranmart.model.Order;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import com.kamaleshwaran.kamaleshwaranmart.model.Role;
import com.kamaleshwaran.kamaleshwaranmart.model.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service providing Administrator Dashboard metrics and platform moderation.
 */
public class AdminService {
    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;

    public AdminService() {
        this.userDAO = new UserDAOImpl();
        this.productDAO = new ProductDAOImpl();
        this.orderDAO = new OrderDAOImpl();
    }

    public AdminService(UserDAO userDAO, ProductDAO productDAO, OrderDAO orderDAO) {
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;
    }

    public AdminStatsDTO getAdminDashboardStats() {
        int totalUsers = userDAO.countTotalUsers();
        int totalBuyers = userDAO.countUsersByRole(Role.BUYER);
        int totalSellers = userDAO.countUsersByRole(Role.SELLER);
        int totalProducts = productDAO.countTotalProducts();
        int totalOrders = orderDAO.countTotalOrders();
        BigDecimal platformRevenue = orderDAO.calculatePlatformRevenue();

        return new AdminStatsDTO(totalUsers, totalBuyers, totalSellers, totalProducts, totalOrders, platformRevenue);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public boolean deleteUser(Long userId) {
        return userDAO.deleteById(userId);
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll(null, null, "newest");
    }

    public boolean moderateRemoveProduct(Long productId) {
        return productDAO.deleteById(productId);
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAllOrders();
    }
}
