package com.greentech.servlet;

import com.greentech.dao.AssignmentDAO;
import com.greentech.dao.CertificateDAO;
import com.greentech.dao.CourseDAO;
import com.greentech.dao.DiscussionDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.model.*;
import com.greentech.service.AnalyticsService;
import com.greentech.service.RecommendationService;
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
 * Controller preparing personalized dashboard data for Learner and Instructor roles.
 * Admins are redirected to the dedicated administration control center.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final CourseDAO courseDAO = new CourseDAO();
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    private final DiscussionDAO discussionDAO = new DiscussionDAO();
    private final CertificateDAO certificateDAO = new CertificateDAO();
    private final RecommendationService recommendationService = new RecommendationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);

        // Forward Admin directly to dedicated admin control center
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }

        try {
            if (user.isLearner()) {
                // 1. Course Enrollment History & Progress
                List<Enrollment> enrollments = courseDAO.getEnrollmentsByLearner(user.getId());
                request.setAttribute("enrollments", enrollments);
                request.setAttribute("enrollmentCount", enrollments.size());

                // 2. Assignment Submissions & Feedback
                List<Submission> submissions = assignmentDAO.getSubmissionsByLearner(user.getId());
                request.setAttribute("submissions", submissions);

                // Available pending assignments for enrolled courses
                List<Assignment> availableAssignments = assignmentDAO.getAll();
                request.setAttribute("availableAssignments", availableAssignments);

                // 3. Community Discussions & Participation
                List<Discussion> communityTopics = discussionDAO.getRecentTopics(15);
                request.setAttribute("communityTopics", communityTopics);

                // 4. Certification Status
                List<Certificate> certificates = certificateDAO.getCertificatesByLearner(user.getId());
                request.setAttribute("certificates", certificates);

                // 5. Course Recommendations based on learning history
                List<Course> recommendations = recommendationService.getRecommendationsForLearner(user.getId());
                request.setAttribute("recommendations", recommendations);

            } else if (user.isInstructor()) {
                // 1. Authored Courses & History
                List<Course> authoredCourses = courseDAO.getCoursesByInstructor(user.getId());
                request.setAttribute("instructorCourses", authoredCourses);
                request.setAttribute("courseCount", authoredCourses.size());

                // 2. Assignment Grading & Feedback Panel
                List<Submission> pendingSubmissions = assignmentDAO.getSubmissionsByInstructor(user.getId());
                request.setAttribute("pendingSubmissions", pendingSubmissions);

                // 3. Learner Interactions (Discussions on instructor's courses)
                List<Discussion> learnerDiscussions = discussionDAO.getRecentTopics(20);
                request.setAttribute("learnerDiscussions", learnerDiscussions);

                // 4. Course Performance telemetry
                List<EnrollmentStats> performanceStats = courseDAO.getCourseEnrollmentStats();
                request.setAttribute("performanceStats", performanceStats);
            }

            request.setAttribute("activeSessions", AnalyticsService.getInstance().getActiveSessionCount());

        } catch (DatabaseException e) {
            request.setAttribute("dbWarning", "Warning: Could not fetch all dashboard metrics (" + e.getMessage() + ").");
            request.setAttribute("enrollments", Collections.emptyList());
            request.setAttribute("submissions", Collections.emptyList());
            request.setAttribute("communityTopics", Collections.emptyList());
            request.setAttribute("certificates", Collections.emptyList());
            request.setAttribute("recommendations", Collections.emptyList());
        }

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
