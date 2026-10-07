package com.kamaleshwaran.kamaleshwaranmart.filter;

import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.model.Role;
import com.kamaleshwaran.kamaleshwaranmart.model.User;
import com.kamaleshwaran.kamaleshwaranmart.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter enforcing Role-Based Access Control (RBAC) for Seller and Admin sections.
 */
@WebFilter(urlPatterns = {
        "/seller/*", "/admin/*",
        "/api/v1/seller/*", "/api/v1/admin/*"
})
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            // Let AuthFilter handle unauthenticated state
            chain.doFilter(request, response);
            return;
        }

        String uri = httpRequest.getRequestURI();
        Role userRole = currentUser.getRole();

        boolean isSellerEndpoint = uri.contains("/seller/") || uri.contains("/api/v1/seller/");
        boolean isAdminEndpoint = uri.contains("/admin/") || uri.contains("/api/v1/admin/");

        boolean allowed = false;

        if (isAdminEndpoint) {
            // Only ADMIN allowed
            allowed = (userRole == Role.ADMIN);
        } else if (isSellerEndpoint) {
            // SELLER or ADMIN allowed
            allowed = (userRole == Role.SELLER || userRole == Role.ADMIN);
        } else {
            allowed = true;
        }

        if (allowed) {
            chain.doFilter(request, response);
        } else {
            if (uri.contains("/api/v1/")) {
                JsonUtil.writeJsonResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN,
                        ApiResponseDTO.error("FORBIDDEN", "Access denied. You lack permissions for this resource."));
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/home?error=forbidden");
            }
        }
    }

    @Override
    public void destroy() {
    }
}
