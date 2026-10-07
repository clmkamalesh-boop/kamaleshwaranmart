package com.kamaleshwaran.kamaleshwaranmart.dao.impl;

import com.kamaleshwaran.kamaleshwaranmart.dao.ReviewDAO;
import com.kamaleshwaran.kamaleshwaranmart.exception.DatabaseException;
import com.kamaleshwaran.kamaleshwaranmart.model.Review;
import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ReviewDAO.
 */
public class ReviewDAOImpl implements ReviewDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReviewDAOImpl.class);

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, review.getProductId());
            stmt.setLong(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    review.setId(gk.getLong(1));
                }
            }
            return review;
        } catch (SQLException e) {
            logger.error("Error creating review: {}", e.getMessage());
            throw new DatabaseException("Failed to save review", e);
        }
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        String sql = "SELECT r.id, r.product_id, r.user_id, r.rating, r.comment, r.created_at, " +
                     "u.name AS user_name, p.name AS product_name " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN products p ON r.product_id = p.id " +
                     "WHERE r.product_id = ? " +
                     "ORDER BY r.created_at DESC";

        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setUserName(rs.getString("user_name"));
                    r.setProductName(rs.getString("product_name"));
                    reviews.add(r);
                }
            }
            return reviews;
        } catch (SQLException e) {
            logger.error("Error finding reviews for product {}: {}", productId, e.getMessage());
            throw new DatabaseException("Failed to retrieve product reviews", e);
        }
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        String sql = "SELECT r.id, r.product_id, r.user_id, r.rating, r.comment, r.created_at, " +
                     "u.name AS user_name, p.name AS product_name " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN products p ON r.product_id = p.id " +
                     "WHERE r.user_id = ? " +
                     "ORDER BY r.created_at DESC";

        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setUserName(rs.getString("user_name"));
                    r.setProductName(rs.getString("product_name"));
                    reviews.add(r);
                }
            }
            return reviews;
        } catch (SQLException e) {
            logger.error("Error finding reviews for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to retrieve user reviews", e);
        }
    }

    @Override
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        String sql = "SELECT COUNT(*) FROM orders o " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status <> 'CANCELLED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error checking purchase history: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error checking existing reviews: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public double getAverageRating(Long productId) {
        String sql = "SELECT COALESCE(AVG(rating), 0.0) FROM reviews WHERE product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Math.round(rs.getDouble(1) * 10.0) / 10.0;
                }
            }
            return 0.0;
        } catch (SQLException e) {
            logger.error("Error calculating average rating: {}", e.getMessage());
            return 0.0;
        }
    }

    @Override
    public int countReviews(Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting reviews: {}", e.getMessage());
            return 0;
        }
    }
}
