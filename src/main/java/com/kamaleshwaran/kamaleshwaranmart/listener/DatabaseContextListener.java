package com.kamaleshwaran.kamaleshwaranmart.listener;

import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Lifecycle listener for initializing and closing HikariCP connection pool.
 */
@WebListener
public class DatabaseContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing KamaleshwaranMart application context and database connection pool...");
        try {
            DatabaseUtil.initializePool();
            logger.info("KamaleshwaranMart database context initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize database context: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down KamaleshwaranMart application context and closing database connection pool...");
        try {
            DatabaseUtil.closePool();
            logger.info("Database connection pool closed successfully.");
        } catch (Exception e) {
            logger.error("Error shutting down database context: {}", e.getMessage(), e);
        }
    }
}
