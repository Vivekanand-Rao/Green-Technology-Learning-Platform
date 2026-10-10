package com.greentech.servlet;

import com.greentech.dao.SystemSettingDAO;
import com.greentech.dao.UserDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.exception.ValidationException;
import com.greentech.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles public visitor registration.
 * Explicitly prevents unauthorized administrator registration through public forms.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final SystemSettingDAO settingDAO = new SystemSettingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            // Check if public registration is enabled in system settings
            String allowReg = settingDAO.getSettingValue("allow_registration", "true");
            if (!"true".equalsIgnoreCase(allowReg)) {
                request.setAttribute("error", "Public registration is currently disabled by system administration.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Server-side validations
            ValidationUtil.requireNonEmpty(name, "Full Name");
            ValidationUtil.validateEmail(email);
            ValidationUtil.validatePassword(password, 6);
            ValidationUtil.validateRole(role, false); // False: forbid public admin creation!

            if (userDAO.emailExists(email.trim())) {
                request.setAttribute("error", "Email is already registered. Please login or use a different email.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            boolean created = userDAO.register(name, email, password, role);
            if (created) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?registered=true");
            } else {
                request.setAttribute("error", "Registration could not be completed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (DatabaseException de) {
            request.setAttribute("error", "Database error during registration: " + de.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
