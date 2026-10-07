package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.dto.UserResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.exception.ValidationException;
import com.kamaleshwaran.kamaleshwaranmart.model.Role;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.service.AuthService;
import com.kamaleshwaran.kamaleshwaranmart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller handling user registration for Buyers and Sellers.
 */
@WebServlet(urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String roleStr = req.getParameter("role");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        try {
            Role role = Role.fromString(roleStr);
            User user = authService.register(name, email, password, confirmPassword, role);

            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_CREATED,
                        ApiResponseDTO.success(UserResponseDTO.fromUser(user)));
            } else {
                resp.sendRedirect(req.getContextPath() + "/login?registered=true");
            }

        } catch (ValidationException e) {
            logger.warn("Validation error during registration for {}: {}", email, e.getMessage());
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponseDTO.error("VALIDATION_ERROR", e.getMessage()));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("name", name);
                req.setAttribute("email", email);
                req.setAttribute("selectedRole", roleStr);
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            logger.error("Unexpected error during registration for {}: {}", email, e.getMessage(), e);
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "Registration failed due to a server error."));
            } else {
                req.setAttribute("errorMessage", "An error occurred while creating your account. Please try again.");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            }
        }
    }
}
