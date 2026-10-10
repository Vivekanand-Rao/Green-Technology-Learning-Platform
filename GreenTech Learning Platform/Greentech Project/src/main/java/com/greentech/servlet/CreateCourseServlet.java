package com.greentech.servlet;

import com.greentech.dao.CourseDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.exception.ValidationException;
import com.greentech.model.User;
import com.greentech.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles course creation by instructors.
 * Inputs: Course title, category, description, syllabus content.
 * Output: Confirmation message for course creation pending admin approval.
 */
@WebServlet("/create-course")
public class CreateCourseServlet extends HttpServlet {
    private final CourseDAO dao = new CourseDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null || !user.canCreateCourse()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only instructors can create courses.");
            return;
        }

        String title = req.getParameter("title");
        String category = req.getParameter("category");
        String description = req.getParameter("description");
        String content = req.getParameter("content");

        try {
            ValidationUtil.requireNonEmpty(title, "Course title");
            ValidationUtil.requireNonEmpty(description, "Course description");

            boolean success = dao.createCourse(
                    title.trim(),
                    category != null ? category.trim() : "Renewable Energy",
                    description.trim(),
                    content != null ? content.trim() : "",
                    user.getId()
            );

            if (success) {
                session.setAttribute("feedbackMsg", "Course '" + title.trim() + "' created successfully! It has been submitted for administrator approval.");
                resp.sendRedirect(req.getContextPath() + "/dashboard?course_created=true");
            } else {
                session.setAttribute("errorMsg", "Unable to create course. Please try again.");
                resp.sendRedirect(req.getContextPath() + "/dashboard?error=create_failed");
            }
        } catch (ValidationException ve) {
            session.setAttribute("errorMsg", ve.getMessage());
            resp.sendRedirect(req.getContextPath() + "/dashboard?error=validation");
        } catch (DatabaseException de) {
            session.setAttribute("errorMsg", "Database error: " + de.getMessage());
            resp.sendRedirect(req.getContextPath() + "/dashboard?error=db");
        }
    }
}
