package com.kamaleshwaran.kamaleshwaranmart.service;

import com.kamaleshwaran.kamaleshwaranmart.dao.ProductDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.ReviewDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.ProductDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.ReviewDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.Review;
import com.kamaleshwaran.kamaleshwaranmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service managing product reviews and ratings.
 */
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewDAO reviewDAO;
    private final ProductDAO productDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    public ReviewService(ReviewDAO reviewDAO, ProductDAO productDAO) {
        this.reviewDAO = reviewDAO;
        this.productDAO = productDAO;
    }

    public Review addReview(Long userId, Long productId, int rating, String comment) {
        if (userId == null || productId == null) {
            throw new ValidationException("User ID and Product ID are required.");
        }
        ValidationUtil.validateRating(rating);
        ValidationUtil.validateRequiredString(comment, "Review comment", 2000);

        productDAO.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found."));

        if (!reviewDAO.hasUserPurchasedProduct(userId, productId)) {
            throw new ValidationException("Only customers who have purchased this product can leave a verified review.");
        }

        if (reviewDAO.hasUserReviewedProduct(userId, productId)) {
            throw new ValidationException("You have already reviewed this product.");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment.trim());

        Review created = reviewDAO.create(review);
        logger.info("Review created for product #{} by user #{}: rating={}", productId, userId, rating);
        return created;
    }

    public List<Review> getReviewsForProduct(Long productId) {
        return reviewDAO.findByProductId(productId);
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewDAO.findByUserId(userId);
    }

    public boolean canUserReview(Long userId, Long productId) {
        if (userId == null || productId == null) return false;
        return reviewDAO.hasUserPurchasedProduct(userId, productId) && !reviewDAO.hasUserReviewedProduct(userId, productId);
    }
}
