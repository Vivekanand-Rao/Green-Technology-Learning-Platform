package com.greentech.servlet;

import com.greentech.dao.CourseDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.model.Course;
import com.greentech.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for browsing approved green technology courses.
 * Supports category filtering and title/description search.
 */
@WebServlet("/courses")
public class CoursesServlet extends HttpServlet {
    private final CourseDAO courseDAO = new CourseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;
        request.setAttribute("currentUser", user);

        String categoryFilter = request.getParameter("category");
        String searchQuery = request.getParameter("search");

        try {
            List<Course> courses = courseDAO.getApprovedCourses();

            // Apply category filter if present
            if (categoryFilter != null && !categoryFilter.trim().isEmpty() && !"All".equalsIgnoreCase(categoryFilter)) {
                courses = courses.stream()
                        .filter(c -> categoryFilter.equalsIgnoreCase(c.getCategory()))
                        .collect(Collectors.toList());
            }

            // Apply search query filter if present
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = searchQuery.trim().toLowerCase();
                courses = courses.stream()
                        .filter(c -> (c.getTitle() != null && c.getTitle().toLowerCase().contains(q)) ||
                                     (c.getDescription() != null && c.getDescription().toLowerCase().contains(q)))
                        .collect(Collectors.toList());
            }

            request.setAttribute("courses", courses);
            request.setAttribute("selectedCategory", categoryFilter);
            request.setAttribute("searchQuery", searchQuery);

            if (user != null && user.isLearner()) {
                List<Course> enrolled = courseDAO.getEnrolledCourses(user.getId());
                Set<Integer> enrolledIds = new HashSet<>();
                for (Course c : enrolled) {
                    enrolledIds.add(c.getId());
                }
                request.setAttribute("enrolledCourseIds", enrolledIds);
            }
        } catch (DatabaseException e) {
            request.setAttribute("error", "Unable to load courses from MySQL (" + e.getMessage() + ").");
            request.setAttribute("courses", new ArrayList<Course>());
        }

        request.getRequestDispatcher("/courses.jsp").forward(request, response);
    }
}
