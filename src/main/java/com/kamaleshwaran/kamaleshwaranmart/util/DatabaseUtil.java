package com.kamaleshwaran.kamaleshwaranmart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

/**
 * Manages HikariCP connection pooling for MySQL with automatic H2 standalone fallback.
 */
public final class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
    private static volatile HikariDataSource dataSource;
    private static final Object LOCK = new Object();

    private DatabaseUtil() {
    }

    /**
     * Initializes HikariCP Connection Pool.
     */
    public static void initializePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        synchronized (LOCK) {
            if (dataSource != null && !dataSource.isClosed()) {
                return;
            }

            try {
                Properties props = new Properties();
                InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties");
                if (in != null) {
                    props.load(in);
                }

                // Ensure data directory exists for embedded H2 fallback
                File dataDir = new File("./data");
                if (!dataDir.exists()) {
                    dataDir.mkdirs();
                }

                String dbType = System.getProperty("db.type", System.getenv().getOrDefault("DB_TYPE", props.getProperty("db.type", "mysql")));
                String driver = System.getProperty("db.driver", System.getenv().getOrDefault("DB_DRIVER", props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver")));
                String url = System.getProperty("db.url", System.getenv().getOrDefault("DB_URL", props.getProperty("db.url", "jdbc:mysql://localhost:3306/kamaleshwaranmart?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true&characterEncoding=UTF-8")));
                String username = System.getProperty("db.username", System.getenv().getOrDefault("DB_USERNAME", props.getProperty("db.username", "root")));
                String password = System.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", props.getProperty("db.password", "root")));

                int maxPoolSize = Integer.parseInt(props.getProperty("pool.maximumPoolSize", "10"));
                int minIdle = Integer.parseInt(props.getProperty("pool.minimumIdle", "2"));
                long idleTimeout = Long.parseLong(props.getProperty("pool.idleTimeout", "30000"));
                long connectionTimeout = Long.parseLong(props.getProperty("pool.connectionTimeout", "10000"));
                long maxLifetime = Long.parseLong(props.getProperty("pool.maxLifetime", "1800000"));
                String poolName = props.getProperty("pool.poolName", "KamaleshwaranMartHikariPool");

                boolean connected = false;

                // Try configuring primary database (MySQL or configured driver)
                try {
                    Class.forName(driver);
                    HikariConfig config = new HikariConfig();
                    config.setDriverClassName(driver);
                    config.setJdbcUrl(url);
                    config.setUsername(username);
                    config.setPassword(password);

                    config.setMaximumPoolSize(maxPoolSize);
                    config.setMinimumIdle(minIdle);
                    config.setIdleTimeout(idleTimeout);
                    config.setConnectionTimeout(connectionTimeout);
                    config.setMaxLifetime(maxLifetime);
                    config.setPoolName(poolName);

                    HikariDataSource ds = new HikariDataSource(config);
                    // Test connection immediately to ensure database server is running
                    try (Connection testConn = ds.getConnection()) {
                        logger.info("Database connection verified successfully with URL: {}", url);
                        dataSource = ds;
                        connected = true;
                    }
                } catch (Exception primaryEx) {
                    logger.warn("Could not establish connection to primary database ({}) at {}: {}", driver, url, primaryEx.getMessage());
                    if ("mysql".equalsIgnoreCase(dbType) || driver.contains("mysql")) {
                        logger.warn("Falling back to embedded H2 database for standalone operation...");
                        String h2Driver = "org.h2.Driver";
                        String h2Url = "jdbc:h2:./data/kamaleshwaranmart;DB_CLOSE_DELAY=-1;MODE=LEGACY;NON_KEYWORDS=USER,VALUE";
                        Class.forName(h2Driver);

                        HikariConfig h2Config = new HikariConfig();
                        h2Config.setDriverClassName(h2Driver);
                        h2Config.setJdbcUrl(h2Url);
                        h2Config.setUsername("sa");
                        h2Config.setPassword("");
                        h2Config.setMaximumPoolSize(maxPoolSize);
                        h2Config.setMinimumIdle(minIdle);
                        h2Config.setPoolName("KamaleshwaranMartH2FallbackPool");

                        dataSource = new HikariDataSource(h2Config);
                        logger.info("Embedded H2 fallback database pool initialized successfully with URL: {}", h2Url);
                        connected = true;
                    } else {
                        throw primaryEx;
                    }
                }

                if (!connected || dataSource == null) {
                    throw new SQLException("Unable to initialize any database connection pool.");
                }

                // Run schema and seed scripts
                initializeSchemaAndSeed();

            } catch (Exception e) {
                logger.error("Failed to initialize database connection pool: {}", e.getMessage(), e);
                throw new RuntimeException("Database initialization failed", e);
            }
        }
    }

    /**
     * Obtains a pooled JDBC connection from HikariCP.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initializePool();
        }
        return dataSource.getConnection();
    }

    /**
     * Closes the HikariCP connection pool.
     */
    public static void closePool() {
        synchronized (LOCK) {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                logger.info("HikariCP connection pool closed gracefully.");
            }
        }
    }

    /**
     * Executes schema and initial seed data if not present.
     */
    private static void initializeSchemaAndSeed() {
        try (Connection conn = getConnection()) {
            logger.info("Applying database schema from db/schema.sql...");
            executeSqlScript(conn, "db/schema.sql");

            // Check if admin user exists, if not apply seed data
            boolean needSeed = false;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    needSeed = true;
                }
            } catch (Exception e) {
                needSeed = true;
            }

            if (needSeed) {
                logger.info("Users table is empty. Seeding initial data from db/seed.sql...");
                executeSqlScript(conn, "db/seed.sql");

                // Update passwords with known secure BCrypt hash to guarantee login
                String defaultUserHash = PasswordUtil.hashPassword("Password123!");
                String adminHash = PasswordUtil.hashPassword("AdminPassword123!");

                try (PreparedStatement adminStmt = conn.prepareStatement(
                        "UPDATE users SET password_hash = ? WHERE role = 'ADMIN'");
                     PreparedStatement userStmt = conn.prepareStatement(
                        "UPDATE users SET password_hash = ? WHERE role <> 'ADMIN'")) {
                    adminStmt.setString(1, adminHash);
                    adminStmt.executeUpdate();

                    userStmt.setString(1, defaultUserHash);
                    userStmt.executeUpdate();
                    logger.info("Default seed account passwords initialized with verified BCrypt hashes.");
                }
            } else {
                logger.info("Database already seeded with user records.");
            }

        } catch (Exception e) {
            logger.error("Error initializing schema and seed data: {}", e.getMessage(), e);
        }
    }

    /**
     * Parses and executes SQL script delimited by semicolons.
     */
    public static void executeSqlScript(Connection conn, String scriptPath) throws IOException, SQLException {
        InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream(scriptPath);
        if (is == null) {
            // Try loading from file
            File f = new File("src/main/resources/" + scriptPath);
            if (f.exists()) {
                is = new FileInputStream(f);
            } else {
                logger.warn("SQL script not found: {}", scriptPath);
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                    continue;
                }
                sb.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sb.toString();
                    sql = sql.substring(0, sql.length() - 1).trim();
                    if (!sql.isEmpty()) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            logger.warn("SQL statement warning ({}): {}", sql, e.getMessage());
                        }
                    }
                    sb.setLength(0);
                }
            }
        }
    }
}
