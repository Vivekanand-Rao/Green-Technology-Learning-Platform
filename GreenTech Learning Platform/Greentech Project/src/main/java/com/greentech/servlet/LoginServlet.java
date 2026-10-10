package com.greentech.servlet;

import com.greentech.dao.UserDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.exception.ValidationException;
import com.greentech.model.User;
import com.greentech.service.AnalyticsService;
import com.greentech.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles user authentication, session initiation, and secure dashboard redirection.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            response.sendRedirect(request.getContextPath() + "/" + user.getDashboardUrl());
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            ValidationUtil.validateEmail(email);
            ValidationUtil.requireNonEmpty(password, "Password");

            User user = userDAO.login(email.trim(), password);
            if (user != null) {
                if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
                    request.setAttribute("error", "Your account has been deactivated. Please contact an administrator.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                // Invalidate any prior session to defend against session fixation
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }

                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                AnalyticsService.getInstance().incrementActiveSessions();

                response.sendRedirect(request.getContextPath() + "/" + user.getDashboardUrl());
            } else {
                request.setAttribute("error", "Invalid email address or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (DatabaseException de) {
            request.setAttribute("error", "Database connection error. Please verify MySQL configuration in db.properties.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
