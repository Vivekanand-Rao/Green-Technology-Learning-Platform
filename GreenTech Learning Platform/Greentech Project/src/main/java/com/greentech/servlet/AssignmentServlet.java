package com.greentech.servlet;

import com.greentech.dao.AssignmentDAO;
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
 * Controller for Assignment lifecycle operations:
 * 1. Instructor: Create assignments associated with courses
 * 2. Learner: Submit assignment solutions
 * 3. Instructor: Grade submissions and provide written feedback
 */
@WebServlet("/assignment")
public class AssignmentServlet extends HttpServlet {
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        try {
            if ("createAssignment".equalsIgnoreCase(action)) {
                if (!user.canCreateCourse()) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only instructors can create assignments.");
                    return;
                }
                int courseId = Integer.parseInt(req.getParameter("courseId"));
                String title = req.getParameter("title");
                String description = req.getParameter("description");
                String maxScoreParam = req.getParameter("maxScore");
                String dueDate = req.getParameter("dueDate");

                ValidationUtil.requireNonEmpty(title, "Assignment Title");
                ValidationUtil.requireNonEmpty(description, "Assignment Description");

                int maxScore = 100;
                if (maxScoreParam != null && !maxScoreParam.trim().isEmpty()) {
                    maxScore = Integer.parseInt(maxScoreParam.trim());
                }

                boolean ok = assignmentDAO.createAssignment(courseId, title, description, maxScore, dueDate);
                if (ok) {
                    session.setAttribute("feedbackMsg", "Assignment '" + title.trim() + "' created successfully!");
                } else {
                    session.setAttribute("errorMsg", "Failed to create assignment.");
                }

            } else if ("submitAssignment".equalsIgnoreCase(action)) {
                if (!user.isLearner()) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only learners can submit assignments.");
                    return;
                }
                int assignmentId = Integer.parseInt(req.getParameter("assignmentId"));
                String submissionText = req.getParameter("submissionText");

                ValidationUtil.requireNonEmpty(submissionText, "Submission text");

                boolean ok = assignmentDAO.submitAssignment(assignmentId, user.getId(), submissionText);
                if (ok) {
                    session.setAttribute("feedbackMsg", "Assignment submitted successfully! Awaiting instructor evaluation.");
                } else {
                    session.setAttribute("errorMsg", "Failed to submit assignment.");
                }

            } else if ("gradeSubmission".equalsIgnoreCase(action)) {
                if (!user.canGradeAssignments()) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only instructors can grade submissions.");
                    return;
                }
                int submissionId = Integer.parseInt(req.getParameter("submissionId"));
                int score = Integer.parseInt(req.getParameter("score"));
                String feedback = req.getParameter("feedback");

                ValidationUtil.requireNonEmpty(feedback, "Feedback");

                boolean ok = assignmentDAO.gradeSubmission(submissionId, score, feedback);
                if (ok) {
                    session.setAttribute("feedbackMsg", "Submission evaluated successfully. Score (" + score + "/100) and feedback recorded.");
                } else {
                    session.setAttribute("errorMsg", "Failed to record grading.");
                }
            }

        } catch (ValidationException ve) {
            session.setAttribute("errorMsg", ve.getMessage());
        } catch (NumberFormatException nfe) {
            session.setAttribute("errorMsg", "Invalid numerical value provided.");
        } catch (DatabaseException de) {
            session.setAttribute("errorMsg", "Database error: " + de.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
