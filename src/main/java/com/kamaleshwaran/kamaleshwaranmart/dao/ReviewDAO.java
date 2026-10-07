package com.kamaleshwaran.kamaleshwaranmart.dao;

import com.kamaleshwaran.kamaleshwaranmart.model.Review;

import java.util.List;

/**
 * Data Access Object interface for Product Reviews and Ratings.
 */
public interface ReviewDAO {
    Review create(Review review);
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    boolean hasUserPurchasedProduct(Long userId, Long productId);
    boolean hasUserReviewedProduct(Long userId, Long productId);
    double getAverageRating(Long productId);
    int countReviews(Long productId);
}
