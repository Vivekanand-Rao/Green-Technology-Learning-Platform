package com.greentech.servlet;

import com.greentech.dao.DiscussionDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.exception.ValidationException;
import com.greentech.model.Discussion;
import com.greentech.model.User;
import com.greentech.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Community Discussions and Learner-Instructor interactions.
 * Inputs: Discussion topic details / reply messages.
 * Outputs: Participation confirmation and live discussion threads.
 */
@WebServlet("/community")
public class CommunityServlet extends HttpServlet {
    private final DiscussionDAO discussionDAO = new DiscussionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        req.setAttribute("currentUser", user);

        String topicIdParam = req.getParameter("topicId");
        try {
            if (topicIdParam != null && !topicIdParam.trim().isEmpty()) {
                int topicId = Integer.parseInt(topicIdParam.trim());
                Discussion topic = discussionDAO.findById(topicId);
                List<Discussion> replies = discussionDAO.getReplies(topicId);
                req.setAttribute("activeTopic", topic);
                req.setAttribute("replies", replies);
            }
            List<Discussion> topics = discussionDAO.getRecentTopics(30);
            req.setAttribute("topics", topics);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to retrieve discussions: " + e.getMessage());
        }

        req.getRequestDispatcher("/community.jsp").forward(req, resp);
    }

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
            if ("createTopic".equalsIgnoreCase(action)) {
                String title = req.getParameter("title");
                String message = req.getParameter("message");
                String courseIdParam = req.getParameter("courseId");

                ValidationUtil.requireNonEmpty(title, "Discussion Title");
                ValidationUtil.requireNonEmpty(message, "Discussion Message");

                Integer courseId = null;
                if (courseIdParam != null && !courseIdParam.trim().isEmpty()) {
                    courseId = Integer.parseInt(courseIdParam.trim());
                }

                boolean ok = discussionDAO.createTopic(courseId, user.getId(), title, message);
                if (ok) {
                    session.setAttribute("feedbackMsg", "Discussion topic posted successfully! Community members can now participate.");
                } else {
                    session.setAttribute("errorMsg", "Failed to create discussion topic.");
                }

            } else if ("createReply".equalsIgnoreCase(action)) {
                int parentId = Integer.parseInt(req.getParameter("parentId"));
                String message = req.getParameter("message");

                ValidationUtil.requireNonEmpty(message, "Reply Message");

                boolean ok = discussionDAO.createReply(parentId, user.getId(), message);
                if (ok) {
                    session.setAttribute("feedbackMsg", "Reply posted successfully to the community discussion!");
                } else {
                    session.setAttribute("errorMsg", "Failed to post reply.");
                }
                resp.sendRedirect(req.getContextPath() + "/community?topicId=" + parentId);
                return;
            }

        } catch (ValidationException ve) {
            session.setAttribute("errorMsg", ve.getMessage());
        } catch (DatabaseException de) {
            session.setAttribute("errorMsg", "Database error: " + de.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/community");
    }
}
