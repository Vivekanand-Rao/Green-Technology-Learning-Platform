package com.greentech.servlet;

import com.greentech.dao.CourseDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles learner course enrollment and ongoing progress tracking.
 * Inputs: Course selection / progress percentage.
 * Outputs: Enrollment confirmation and completion updates.
 */
@WebServlet("/enroll")
public class EnrollServlet extends HttpServlet {
    private final CourseDAO courseDAO = new CourseDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!user.isLearner()) {
            resp.sendRedirect(req.getContextPath() + "/courses?error=role");
            return;
        }

        String action = req.getParameter("action");

        try {
            if ("updateProgress".equalsIgnoreCase(action)) {
                int courseId = Integer.parseInt(req.getParameter("courseId"));
                int progress = Integer.parseInt(req.getParameter("progress"));
                boolean updated = courseDAO.updateProgress(user.getId(), courseId, progress);
                if (updated) {
                    if (progress >= 100) {
                        session.setAttribute("feedbackMsg", "Congratulations! You have completed the course and earned an official Certificate!");
                    } else {
                        session.setAttribute("feedbackMsg", "Course progress updated to " + progress + "%.");
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }

            // Default action: Enroll in course
            String courseIdParam = req.getParameter("courseId");
            if (courseIdParam != null && !courseIdParam.trim().isEmpty()) {
                int courseId = Integer.parseInt(courseIdParam.trim());
                boolean enrolled = courseDAO.enroll(user.getId(), courseId);
                if (enrolled) {
                    session.setAttribute("feedbackMsg", "Enrollment confirmed! You now have full access to the course curriculum and assignments.");
                    resp.sendRedirect(req.getContextPath() + "/dashboard?enrolled=true");
                    return;
                }
            }
        } catch (NumberFormatException | DatabaseException e) {
            session.setAttribute("errorMsg", "Enrollment action failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/courses?error=enroll_failed");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/courses");
    }
}
