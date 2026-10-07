package com.kamaleshwaran.kamaleshwaranmart.dao;

import com.kamaleshwaran.kamaleshwaranmart.model.Product;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Product operations.
 */
public interface ProductDAO {
    Product create(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll(String search, String category, String sortBy);
    List<Product> findBySellerId(Long sellerId);
    boolean update(Product product);
    boolean deleteById(Long id);
    boolean updateStock(Connection conn, Long productId, int quantityChange);
    int countTotalProducts();
    int countBySellerId(Long sellerId);
    List<String> findAllCategories();
}
