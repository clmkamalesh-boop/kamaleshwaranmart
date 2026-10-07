package com.kamaleshwaran.kamaleshwaranmart.dao.impl;

import com.kamaleshwaran.kamaleshwaranmart.dao.ProductDAO;
import com.kamaleshwaran.kamaleshwaranmart.exception.DatabaseException;
import com.kamaleshwaran.kamaleshwaranmart.model.Product;
import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ProductDAO.
 */
public class ProductDAOImpl implements ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAOImpl.class);

    private static final String BASE_SELECT =
            "SELECT p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.created_at, " +
            "u.name AS seller_name, " +
            "COALESCE(AVG(r.rating), 0.0) AS avg_rating, " +
            "COUNT(r.id) AS review_count " +
            "FROM products p " +
            "JOIN users u ON p.seller_id = u.id " +
            "LEFT JOIN reviews r ON p.id = r.product_id ";

    private static final String GROUP_BY =
            " GROUP BY p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.created_at, u.name ";

    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, product.getSellerId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setInt(5, product.getStockQty());
            stmt.setString(6, product.getCategory());
            stmt.setString(7, product.getImageUrl());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating product failed, no rows affected.");
            }

            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    product.setId(gk.getLong(1));
                }
            }
            return product;
        } catch (SQLException e) {
            logger.error("Error creating product: {}", e.getMessage());
            throw new DatabaseException("Failed to create product", e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = BASE_SELECT + " WHERE p.id = ? " + GROUP_BY;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding product by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find product", e);
        }
    }

    @Override
    public List<Product> findAll(String search, String category, String sortBy) {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" WHERE (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.category) LIKE ?) ");
            String term = "%" + search.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            hasWhere = true;
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            if (hasWhere) {
                sql.append(" AND LOWER(p.category) = LOWER(?) ");
            } else {
                sql.append(" WHERE LOWER(p.category) = LOWER(?) ");
                hasWhere = true;
            }
            params.add(category.trim());
        }

        sql.append(GROUP_BY);

        // Sorting
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY p.price ASC ");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY p.price DESC ");
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY avg_rating DESC, review_count DESC ");
        } else {
            sql.append(" ORDER BY p.created_at DESC ");
        }

        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            logger.error("Error querying products: {}", e.getMessage());
            throw new DatabaseException("Failed to search products", e);
        }
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) {
        String sql = BASE_SELECT + " WHERE p.seller_id = ? " + GROUP_BY + " ORDER BY p.created_at DESC";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            logger.error("Error finding products by seller {}: {}", sellerId, e.getMessage());
            throw new DatabaseException("Failed to retrieve seller products", e);
        }
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_qty = ?, category = ?, image_url = ? " +
                     "WHERE id = ? AND seller_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getStockQty());
            stmt.setString(5, product.getCategory());
            stmt.setString(6, product.getImageUrl());
            stmt.setLong(7, product.getId());
            stmt.setLong(8, product.getSellerId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating product {}: {}", product.getId(), e.getMessage());
            throw new DatabaseException("Failed to update product", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting product {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete product", e);
        }
    }

    @Override
    public boolean updateStock(Connection conn, Long productId, int quantityChange) {
        // quantityChange is negative when purchasing, positive when restocking
        String sql = "UPDATE products SET stock_qty = stock_qty + ? WHERE id = ? AND (stock_qty + ?) >= 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantityChange);
            stmt.setLong(2, productId);
            stmt.setInt(3, quantityChange);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating stock for product {}: {}", productId, e.getMessage());
            throw new DatabaseException("Failed to update stock", e);
        }
    }

    @Override
    public int countTotalProducts() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting products: {}", e.getMessage());
            throw new DatabaseException("Failed to count products", e);
        }
    }

    @Override
    public int countBySellerId(Long sellerId) {
        String sql = "SELECT COUNT(*) FROM products WHERE seller_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting products for seller {}: {}", sellerId, e.getMessage());
            throw new DatabaseException("Failed to count seller products", e);
        }
    }

    @Override
    public List<String> findAllCategories() {
        String sql = "SELECT DISTINCT category FROM products ORDER BY category ASC";
        List<String> categories = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            return categories;
        } catch (SQLException e) {
            logger.error("Error fetching categories: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch categories", e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setSellerName(rs.getString("seller_name"));
        p.setAverageRating(Math.round(rs.getDouble("avg_rating") * 10.0) / 10.0);
        p.setReviewCount(rs.getInt("review_count"));
        return p;
    }
}
