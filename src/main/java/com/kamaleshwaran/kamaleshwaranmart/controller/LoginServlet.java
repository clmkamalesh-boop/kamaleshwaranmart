package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.dto.UserResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.exception.AuthenticationException;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller handling user authentication, session creation and redirection.
 */
@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            redirectToDashboard(req, resp, user.getRole());
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        try {
            User user = authService.login(email, password);

            // Regenerate session ID upon successful authentication to protect against session fixation
            HttpSession session = req.getSession(true);
            req.changeSessionId();

            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(1800); // 30 minutes explicit timeout

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            session.removeAttribute("redirectAfterLogin");

            if (redirectUrl == null || redirectUrl.contains("/login") || redirectUrl.contains("/register")) {
                redirectUrl = getDashboardUrl(req, user.getRole());
            }

            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_OK,
                        ApiResponseDTO.success(UserResponseDTO.fromUser(user)));
            } else {
                resp.sendRedirect(redirectUrl);
            }

        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for {}: {}", email, e.getMessage());
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_UNAUTHORIZED,
                        ApiResponseDTO.error("AUTH_FAILED", e.getMessage()));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("email", email);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            logger.error("Unexpected error during login for {}: {}", email, e.getMessage(), e);
            if (isAjax) {
                JsonUtil.writeJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ApiResponseDTO.error("SERVER_ERROR", "An unexpected error occurred. Please try again."));
            } else {
                req.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            }
        }
    }

    private String getDashboardUrl(HttpServletRequest req, Role role) {
        String ctx = req.getContextPath();
        if (role == Role.ADMIN) {
            return ctx + "/admin/dashboard";
        } else if (role == Role.SELLER) {
            return ctx + "/seller/dashboard";
        } else {
            return ctx + "/home";
        }
    }

    private void redirectToDashboard(HttpServletRequest req, HttpServletResponse resp, Role role) throws IOException {
        resp.sendRedirect(getDashboardUrl(req, role));
    }
}
