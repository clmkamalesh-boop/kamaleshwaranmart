package com.kamaleshwaran.kamaleshwaranmart.filter;

import com.kamaleshwaran.kamaleshwaranmart.dto.ApiResponseDTO;
import com.kamaleshwaran.kamaleshwaranmart.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter protecting sensitive customer and portal routes from unauthenticated access.
 */
@WebFilter(urlPatterns = {
        "/cart", "/checkout", "/orders", "/reviews",
        "/seller/*", "/admin/*",
        "/api/v1/cart/*", "/api/v1/orders/*", "/api/v1/seller/*", "/api/v1/admin/*"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("currentUser") != null);

        if (isLoggedIn) {
            chain.doFilter(request, response);
            return;
        }

        String requestUri = httpRequest.getRequestURI();
        if (requestUri.contains("/api/v1/")) {
            JsonUtil.writeJsonResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponseDTO.error("UNAUTHORIZED", "Authentication required. Please log in."));
        } else {
            String queryString = httpRequest.getQueryString();
            String redirectUrl = requestUri + (queryString != null ? "?" + queryString : "");
            httpRequest.getSession(true).setAttribute("redirectAfterLogin", redirectUrl);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=auth_required");
        }
    }

    @Override
    public void destroy() {
    }
}
