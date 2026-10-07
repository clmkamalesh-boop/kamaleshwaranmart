package com.kamaleshwaran.kamaleshwaranmart.service;

import com.kamaleshwaran.kamaleshwaranmart.dao.UserDAO;
import com.kamaleshwaran.kamaleshwaranmart.dao.impl.UserDAOImpl;
import com.kamaleshwaran.kamaleshwaranmart.exception.AuthenticationException;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.Role;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.util.PasswordUtil;
import com.kamaleshwaran.kamaleshwaranmart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service handling user authentication, registration, and credentials.
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new Buyer or Seller. Public Admin registration is forbidden.
     */
    public User register(String name, String email, String password, String confirmPassword, Role role) {
        ValidationUtil.validateRequiredString(name, "Full Name", 100);
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePassword(password);

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }

        if (role == null || role == Role.ADMIN) {
            throw new ValidationException("Invalid account role. Public registration permits BUYER or SELLER only.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> existing = userDAO.findByEmail(normalizedEmail);
        if (existing.isPresent()) {
            throw new ValidationException("An account with this email address already exists.");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(hashedPassword);
        user.setRole(role);

        User created = userDAO.create(user);
        logger.info("New user registered successfully: id={}, email={}, role={}", created.getId(), created.getEmail(), created.getRole());
        return created;
    }

    /**
     * Authenticates user credentials and returns User if valid.
     */
    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException("Email and password are required.");
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userDAO.findByEmail(normalizedEmail);

        if (userOpt.isEmpty()) {
            logger.warn("Login failed: User not found for email {}", normalizedEmail);
            throw new AuthenticationException("Invalid email or password.");
        }

        User user = userOpt.get();
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            logger.warn("Login failed: Password mismatch for email {}", normalizedEmail);
            throw new AuthenticationException("Invalid email or password.");
        }

        logger.info("User logged in successfully: id={}, email={}, role={}", user.getId(), user.getEmail(), user.getRole());
        return user;
    }

    public Optional<User> getUserById(Long id) {
        return userDAO.findById(id);
    }
}
