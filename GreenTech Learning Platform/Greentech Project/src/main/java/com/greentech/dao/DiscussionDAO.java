package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Discussion;
import com.greentech.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of Community Discussions Data Access Object.
 */
public class DiscussionDAO implements IDiscussionDAO {

    @Override
    public Discussion findById(Integer id) throws DatabaseException {
        if (id == null) return null;
        String sql = "SELECT d.id, d.course_id, c.title AS course_title, d.user_id, u.name AS user_name, u.role AS user_role, " +
                     "d.title, d.message, d.parent_id, d.created_at, " +
                     "(SELECT COUNT(*) FROM discussions r WHERE r.parent_id = d.id) AS reply_count " +
                     "FROM discussions d " +
                     "LEFT JOIN courses c ON d.course_id = c.id " +
                     "JOIN users u ON d.user_id = u.id " +
                     "WHERE d.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDiscussion(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find discussion: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Discussion> getAll() throws DatabaseException {
        return getRecentTopics(50);
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        if (id == null) return false;
        String sql = "DELETE FROM discussions WHERE id = ? OR parent_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete discussion: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean createTopic(Integer courseId, int userId, String title, String message) throws DatabaseException {
        String sql = "INSERT INTO discussions(course_id, user_id, title, message, parent_id) VALUES(?, ?, ?, ?, NULL)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (courseId != null && courseId > 0) {
                ps.setInt(1, courseId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, userId);
            ps.setString(3, title != null ? title.trim() : "General Discussion");
            ps.setString(4, message != null ? message.trim() : "");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create discussion topic: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean createReply(int parentId, int userId, String message) throws DatabaseException {
        String sql = "INSERT INTO discussions(course_id, user_id, title, message, parent_id) VALUES(NULL, ?, 'Reply', ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message != null ? message.trim() : "");
            ps.setInt(3, parentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to post discussion reply: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Discussion> getRecentTopics(int limit) throws DatabaseException {
        List<Discussion> list = new ArrayList<>();
        String sql = "SELECT d.id, d.course_id, c.title AS course_title, d.user_id, u.name AS user_name, u.role AS user_role, " +
                     "d.title, d.message, d.parent_id, d.created_at, " +
                     "(SELECT COUNT(*) FROM discussions r WHERE r.parent_id = d.id) AS reply_count " +
                     "FROM discussions d " +
                     "LEFT JOIN courses c ON d.course_id = c.id " +
                     "JOIN users u ON d.user_id = u.id " +
                     "WHERE d.parent_id IS NULL " +
                     "ORDER BY d.created_at DESC LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDiscussion(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve community discussions: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Discussion> getTopicsByCourse(Integer courseId) throws DatabaseException {
        List<Discussion> list = new ArrayList<>();
        String sql = "SELECT d.id, d.course_id, c.title AS course_title, d.user_id, u.name AS user_name, u.role AS user_role, " +
                     "d.title, d.message, d.parent_id, d.created_at, " +
                     "(SELECT COUNT(*) FROM discussions r WHERE r.parent_id = d.id) AS reply_count " +
                     "FROM discussions d " +
                     "LEFT JOIN courses c ON d.course_id = c.id " +
                     "JOIN users u ON d.user_id = u.id " +
                     "WHERE d.parent_id IS NULL AND d.course_id = ? " +
                     "ORDER BY d.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDiscussion(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve course discussions: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Discussion> getReplies(int topicId) throws DatabaseException {
        List<Discussion> list = new ArrayList<>();
        String sql = "SELECT d.id, d.course_id, NULL AS course_title, d.user_id, u.name AS user_name, u.role AS user_role, " +
                     "d.title, d.message, d.parent_id, d.created_at, 0 AS reply_count " +
                     "FROM discussions d " +
                     "JOIN users u ON d.user_id = u.id " +
                     "WHERE d.parent_id = ? " +
                     "ORDER BY d.created_at ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, topicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDiscussion(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve discussion replies: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public int getTotalDiscussionCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM discussions";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count discussions: " + e.getMessage(), e);
        }
        return 0;
    }

    private Discussion mapDiscussion(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int courseIdVal = rs.getInt("course_id");
        Integer courseId = rs.wasNull() ? null : courseIdVal;
        String courseTitle = rs.getString("course_title");
        int userId = rs.getInt("user_id");
        String userName = rs.getString("user_name");
        String userRole = rs.getString("user_role");
        String title = rs.getString("title");
        String message = rs.getString("message");
        int parentIdVal = rs.getInt("parent_id");
        Integer parentId = rs.wasNull() ? null : parentIdVal;
        Timestamp createdAt = rs.getTimestamp("created_at");
        int replyCount = rs.getInt("reply_count");

        return new Discussion(id, courseId, courseTitle, userId, userName, userRole, title, message, parentId, createdAt, replyCount);
    }
}
