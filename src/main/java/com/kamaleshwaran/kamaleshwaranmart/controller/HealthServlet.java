package com.kamaleshwaran.kamaleshwaranmart.controller;

import com.kamaleshwaran.kamaleshwaranmart.util.DatabaseUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Health check endpoint: GET /api/v1/health
 * Returns: { "status": "UP", "db": "UP" }
 */
@WebServlet(urlPatterns = {"/api/v1/health"})
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String dbStatus = "DOWN";
        try (Connection conn = DatabaseUtil.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                dbStatus = "UP";
            }
        } catch (Exception ignored) {
            dbStatus = "DOWN";
        }

        String overallStatus = "UP".equals(dbStatus) ? "UP" : "DEGRADED";
        if ("UP".equals(overallStatus)) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        try (PrintWriter writer = resp.getWriter()) {
            writer.printf("{\n  \"status\": \"%s\",\n  \"db\": \"%s\"\n}\n", overallStatus, dbStatus);
            writer.flush();
        }
    }
}
