package com.greentech.servlet;

import com.greentech.dao.CourseDAO;
import com.greentech.dao.DiscussionDAO;
import com.greentech.dao.SystemSettingDAO;
import com.greentech.dao.UserDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.exception.ValidationException;
import com.greentech.model.*;
import com.greentech.service.AnalyticsService;
import com.greentech.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Controller for Administrator Dashboard functionalities:
 * 1. User Management (CRUD users & roles)
 * 2. Course Content Management (Approve/Reject courses)
 * 3. System-wide Configuration Settings
 * 4. Course Enrollment & Completion Analytical Statistics
 * 5. Community Discussion Moderation Overview
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SystemSettingDAO settingDAO = new SystemSettingDAO();
    private final DiscussionDAO discussionDAO = new DiscussionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        User u = (session != null) ? (User) session.getAttribute("user") : null;
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=auth");
            return;
        }
        if (!u.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access restricted to Administrators only.");
            return;
        }

        try {
            List<User> users = userDAO.getAll();
            List<Course> courses = courseDAO.getAllCourses();
            List<SystemSetting> settings = settingDAO.getAllSettings();
            List<EnrollmentStats> stats = courseDAO.getCourseEnrollmentStats();
            List<Discussion> discussions = discussionDAO.getRecentTopics(30);

            req.setAttribute("users", users);
            req.setAttribute("courses", courses);
            req.setAttribute("settings", settings);
            req.setAttribute("enrollmentStats", stats);
            req.setAttribute("discussions", discussions);

            req.setAttribute("totalUsers", users.size());
            req.setAttribute("totalCourses", courses.size());
            req.setAttribute("pendingCount", courseDAO.getPendingCount());
            req.setAttribute("totalDiscussions", discussionDAO.getTotalDiscussionCount());
            req.setAttribute("activeSessions", AnalyticsService.getInstance().getActiveSessionCount());
        } catch (DatabaseException e) {
            req.setAttribute("dbError", "Error retrieving administration records: " + e.getMessage());
            req.setAttribute("users", Collections.emptyList());
            req.setAttribute("courses", Collections.emptyList());
            req.setAttribute("settings", Collections.emptyList());
            req.setAttribute("enrollmentStats", Collections.emptyList());
            req.setAttribute("discussions", Collections.emptyList());
        }

        req.getRequestDispatcher("/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        User u = (session != null) ? (User) session.getAttribute("user") : null;
        if (u == null || !u.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        String message = null;
        String errorMessage = null;

        try {
            if ("createUser".equalsIgnoreCase(action)) {
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                String role = req.getParameter("role");

                ValidationUtil.requireNonEmpty(name, "Full Name");
                ValidationUtil.validateEmail(email);
                ValidationUtil.validatePassword(password, 6);
                ValidationUtil.validateRole(role, true);

                if (userDAO.emailExists(email)) {
                    errorMessage = "User creation failed: Email is already in use.";
                } else {
                    boolean ok = userDAO.createUser(name, email, password, role, "ACTIVE");
                    if (ok) {
                        message = "User '" + name + "' created successfully with role " + role + ".";
                    } else {
                        errorMessage = "Could not create user account.";
                    }
                }

            } else if ("updateUser".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                String role = req.getParameter("role");
                String status = req.getParameter("status");

                ValidationUtil.requireNonEmpty(name, "Full Name");
                ValidationUtil.validateEmail(email);
                ValidationUtil.validateRole(role, true);

                boolean ok = userDAO.updateUser(id, name, email, role, status);
                if (ok) {
                    message = "User ID #" + id + " updated successfully.";
                } else {
                    errorMessage = "Failed to update user account.";
                }

            } else if ("deleteUser".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                if (id == u.getId()) {
                    errorMessage = "Cannot delete currently logged-in administrator account.";
                } else {
                    boolean ok = userDAO.delete(id);
                    if (ok) {
                        message = "User account ID #" + id + " deleted successfully.";
                    } else {
                        errorMessage = "Could not delete user account.";
                    }
                }

            } else if ("updateCourseStatus".equalsIgnoreCase(action)) {
                int courseId = Integer.parseInt(req.getParameter("courseId"));
                String status = req.getParameter("status");

                if ("APPROVED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) {
                    boolean ok = courseDAO.updateStatus(courseId, status.toUpperCase());
                    if (ok) {
                        message = "Course ID #" + courseId + " status updated to " + status.toUpperCase() + ".";
                    } else {
                        errorMessage = "Could not update course status.";
                    }
                }

            } else if ("updateSettings".equalsIgnoreCase(action)) {
                String platformName = req.getParameter("platform_name");
                String maintenanceMode = req.getParameter("maintenance_mode");
                String allowRegistration = req.getParameter("allow_registration");
                String defaultCap = req.getParameter("default_enrollment_cap");
                String contactEmail = req.getParameter("contact_email");

                if (platformName != null) settingDAO.updateSetting("platform_name", platformName);
                if (maintenanceMode != null) settingDAO.updateSetting("maintenance_mode", maintenanceMode);
                if (allowRegistration != null) settingDAO.updateSetting("allow_registration", allowRegistration);
                if (defaultCap != null) settingDAO.updateSetting("default_enrollment_cap", defaultCap);
                if (contactEmail != null) settingDAO.updateSetting("contact_email", contactEmail);

                message = "System settings updated successfully.";

            } else if ("deleteDiscussion".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                boolean ok = discussionDAO.delete(id);
                if (ok) {
                    message = "Discussion topic #" + id + " removed successfully.";
                } else {
                    errorMessage = "Failed to remove discussion topic.";
                }
            }

        } catch (ValidationException ve) {
            errorMessage = ve.getMessage();
        } catch (Exception e) {
            errorMessage = "Operation error: " + e.getMessage();
        }

        if (message != null) {
            session.setAttribute("adminSuccess", message);
        }
        if (errorMessage != null) {
            session.setAttribute("adminError", errorMessage);
        }

        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}
