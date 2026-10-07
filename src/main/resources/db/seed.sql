-- ====================================================================
-- KamaleshwaranMart: Multi-seller E-Commerce Marketplace Seed Data
-- Anna University R2025 Semester 3 Java Capstone Project
-- Compatible with MySQL 8+ and H2 Database
--
-- Default Users:
-- Admin:    admin@kamaleshwaranmart.com   / AdminPassword123!
-- Seller 1: seller1@kamaleshwaranmart.com / Password123!
-- Seller 2: seller2@kamaleshwaranmart.com / Password123!
-- Buyer 1:  buyer1@kamaleshwaranmart.com  / Password123!
-- Buyer 2:  buyer2@kamaleshwaranmart.com  / Password123!
-- ====================================================================

-- 1. USERS SEED (BCrypt encrypted passwords)
INSERT INTO users (id, name, email, password_hash, role, created_at) VALUES
(1, 'System Administrator', 'admin@kamaleshwaranmart.com', '$2a$10$/BUXmIpkcSWkOLpjVHf4iuD29wLkQbk2kxcWbVam6fpgtIPgadsji', 'ADMIN', CURRENT_TIMESTAMP),
(2, 'Apex Electronics', 'seller1@kamaleshwaranmart.com', '$2a$10$bp6ABl6DGjq//U5zyv2.ROgC7PYoYL4CfRdl.WOS1qQMk98BCR0I2', 'SELLER', CURRENT_TIMESTAMP),
(3, 'Urban Style Trends', 'seller2@kamaleshwaranmart.com', '$2a$10$bp6ABl6DGjq//U5zyv2.ROgC7PYoYL4CfRdl.WOS1qQMk98BCR0I2', 'SELLER', CURRENT_TIMESTAMP),
(4, 'Kamaleshwaran S', 'buyer1@kamaleshwaranmart.com', '$2a$10$bp6ABl6DGjq//U5zyv2.ROgC7PYoYL4CfRdl.WOS1qQMk98BCR0I2', 'BUYER', CURRENT_TIMESTAMP),
(5, 'Priya Ramanathan', 'buyer2@kamaleshwaranmart.com', '$2a$10$bp6ABl6DGjq//U5zyv2.ROgC7PYoYL4CfRdl.WOS1qQMk98BCR0I2', 'BUYER', CURRENT_TIMESTAMP);

-- 2. PRODUCTS SEED
INSERT INTO products (id, seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
(1, 2, 'Sony WH-1000XM5 Wireless Headphones', 'Industry-leading noise canceling with two processors and 8 microphones for unprecedented noise cancellation. Up to 30-hour battery life with quick charging.', 24999.00, 45, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP),
(2, 2, 'Apple iPad Air 11-inch M2 Chip', 'Brilliant 11-inch Liquid Retina display with True Tone. Supercharged by the M2 chip delivering incredible performance and all-day battery life.', 59900.00, 28, 'Electronics', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP),
(3, 2, 'Logitech MX Master 3S Wireless Mouse', 'An iconic mouse remastered. Quiet clicks and 8K DPI track-on-glass sensor for ultimate precision and comfort during long work sessions.', 8495.00, 60, 'Electronics', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP),
(4, 3, 'Men Slim Fit Premium Oxford Shirt', 'Crafted from 100% premium long-staple cotton with a classic button-down collar. Breathable, durable, and tailored for effortless style.', 1899.00, 120, 'Fashion', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP),
(5, 3, 'Women Lightweight Trench Coat', 'Timeless water-resistant double-breasted trench coat with storm flaps and a belted waist. Perfect for transitional seasonal weather.', 4599.00, 50, 'Fashion', 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP),
(6, 3, 'Genuine Leather Travel Duffle Bag', 'Handcrafted top-grain leather bag featuring heavy-duty brass hardware, reinforced stitching, and a dedicated shoe compartment.', 6499.00, 35, 'Fashion', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP),
(7, 2, 'Stainless Steel Thermal Coffee Maker', '12-cup programmable coffee machine with double-wall vacuum insulated stainless steel thermal carafe to keep coffee hot for hours.', 4999.00, 40, 'Home & Kitchen', 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP),
(8, 3, 'Smart Fitness Tracker Watch', 'Track daily activity, heart rate 24/7, SpO2, sleep stages, and workout metrics with crisp AMOLED display and 10-day battery life.', 3499.00, 75, 'Sports & Fitness', 'https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=600&auto=format&fit=crop&q=80', CURRENT_TIMESTAMP);

-- 3. ORDERS SEED
INSERT INTO orders (id, buyer_id, status, total_amount, created_at) VALUES
(1, 4, 'DELIVERED', 26898.00, '2026-09-15 10:30:00'),
(2, 4, 'SHIPPED', 8495.00, '2026-09-28 14:15:00'),
(3, 5, 'CONFIRMED', 4599.00, '2026-10-02 09:45:00');

-- 4. ORDER ITEMS SEED
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, created_at) VALUES
(1, 1, 1, 1, 24999.00, '2026-09-15 10:30:00'),
(2, 1, 4, 1, 1899.00, '2026-09-15 10:30:00'),
(3, 2, 3, 1, 8495.00, '2026-09-28 14:15:00'),
(4, 3, 5, 1, 4599.00, '2026-10-02 09:45:00');

-- 5. REVIEWS SEED
INSERT INTO reviews (id, product_id, user_id, rating, comment, created_at) VALUES
(1, 1, 4, 5, 'Outstanding sound quality and the noise cancellation is simply unmatched for work and travel!', '2026-09-18 11:20:00'),
(2, 4, 4, 4, 'Great fitting shirt, breathable fabric and looks crisp even after multiple washes.', '2026-09-20 16:45:00'),
(3, 3, 4, 5, 'Best productivity mouse ever made. The MagSpeed scroll wheel feels futuristic!', '2026-09-30 19:10:00');
