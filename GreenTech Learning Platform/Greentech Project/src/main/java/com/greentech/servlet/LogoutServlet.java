package com.greentech.servlet;

import com.greentech.service.AnalyticsService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles user logout by invalidating the HTTP session and updating telemetry.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            AnalyticsService.getInstance().decrementActiveSessions();
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp?logout=true");
    }
}
