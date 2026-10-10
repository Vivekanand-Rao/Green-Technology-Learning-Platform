package com.greentech.filter;

import com.greentech.model.User;
import com.greentech.service.AnalyticsService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Server-side authorization filter enforcing role-based access control and session verification.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/admin/*", "/admin", "/create-course", "/assignment", "/enroll"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Verify authentication
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        // Enforce Admin access control
        if (path.startsWith("/admin")) {
            if (!user.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator role required.");
                return;
            }
        }

        // Enforce Instructor course creation control
        if ("/create-course".equals(path)) {
            if (!user.canCreateCourse()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only instructors can create courses.");
                return;
            }
        }

        // Enforce Learner enrollment control
        if ("/enroll".equals(path) && "POST".equalsIgnoreCase(req.getMethod())) {
            if (!user.canEnrollInCourses()) {
                resp.sendRedirect(req.getContextPath() + "/courses?error=role");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
