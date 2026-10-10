package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Assignment;
import com.greentech.model.Submission;
import com.greentech.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of Assignment and Submission operations.
 */
public class AssignmentDAO implements IAssignmentDAO {

    @Override
    public Assignment findById(Integer id) throws DatabaseException {
        if (id == null) return null;
        String sql = "SELECT a.id, a.course_id, c.title AS course_title, a.title, a.description, a.max_score, a.due_date, a.created_at " +
                     "FROM assignments a " +
                     "JOIN courses c ON a.course_id = c.id " +
                     "WHERE a.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Assignment(
                            rs.getInt("id"),
                            rs.getInt("course_id"),
                            rs.getString("course_title"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("max_score"),
                            rs.getString("due_date"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find assignment: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Assignment> getAll() throws DatabaseException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.id, a.course_id, c.title AS course_title, a.title, a.description, a.max_score, a.due_date, a.created_at " +
                     "FROM assignments a " +
                     "JOIN courses c ON a.course_id = c.id " +
                     "ORDER BY a.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Assignment(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("course_title"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("max_score"),
                        rs.getString("due_date"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all assignments: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        if (id == null) return false;
        String sql = "DELETE FROM assignments WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete assignment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean createAssignment(int courseId, String title, String description, int maxScore, String dueDate) throws DatabaseException {
        String sql = "INSERT INTO assignments(course_id, title, description, max_score, due_date) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, title != null ? title.trim() : "");
            ps.setString(3, description != null ? description.trim() : "");
            ps.setInt(4, maxScore > 0 ? maxScore : 100);
            ps.setString(5, dueDate != null && !dueDate.trim().isEmpty() ? dueDate.trim() : "Flexible");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create assignment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Assignment> getAssignmentsByCourse(int courseId) throws DatabaseException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT a.id, a.course_id, c.title AS course_title, a.title, a.description, a.max_score, a.due_date, a.created_at " +
                     "FROM assignments a " +
                     "JOIN courses c ON a.course_id = c.id " +
                     "WHERE a.course_id = ? " +
                     "ORDER BY a.id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Assignment(
                            rs.getInt("id"),
                            rs.getInt("course_id"),
                            rs.getString("course_title"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("max_score"),
                            rs.getString("due_date"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load course assignments: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean submitAssignment(int assignmentId, int learnerId, String submissionText) throws DatabaseException {
        String sql = "INSERT INTO submissions(assignment_id, learner_id, submission_text, status) VALUES(?, ?, ?, 'SUBMITTED')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setInt(2, learnerId);
            ps.setString(3, submissionText != null ? submissionText.trim() : "");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to submit assignment: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean gradeSubmission(int submissionId, int score, String feedback) throws DatabaseException {
        String sql = "UPDATE submissions SET score = ?, feedback = ?, status = 'GRADED', graded_at = NOW() WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setString(2, feedback != null ? feedback.trim() : "");
            ps.setInt(3, submissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record grade: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Submission> getSubmissionsByInstructor(int instructorId) throws DatabaseException {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT s.id, s.assignment_id, a.title AS assignment_title, s.learner_id, u.name AS learner_name, " +
                     "s.submission_text, s.score, s.feedback, s.status, s.submitted_at, s.graded_at " +
                     "FROM submissions s " +
                     "JOIN assignments a ON s.assignment_id = a.id " +
                     "JOIN courses c ON a.course_id = c.id " +
                     "JOIN users u ON s.learner_id = u.id " +
                     "WHERE c.instructor_id = ? " +
                     "ORDER BY s.submitted_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSubmission(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load submissions for instructor: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Submission> getSubmissionsByLearner(int learnerId) throws DatabaseException {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT s.id, s.assignment_id, a.title AS assignment_title, s.learner_id, u.name AS learner_name, " +
                     "s.submission_text, s.score, s.feedback, s.status, s.submitted_at, s.graded_at " +
                     "FROM submissions s " +
                     "JOIN assignments a ON s.assignment_id = a.id " +
                     "JOIN users u ON s.learner_id = u.id " +
                     "WHERE s.learner_id = ? " +
                     "ORDER BY s.submitted_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, learnerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSubmission(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load submissions for learner: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Submission getSubmissionById(int submissionId) throws DatabaseException {
        String sql = "SELECT s.id, s.assignment_id, a.title AS assignment_title, s.learner_id, u.name AS learner_name, " +
                     "s.submission_text, s.score, s.feedback, s.status, s.submitted_at, s.graded_at " +
                     "FROM submissions s " +
                     "JOIN assignments a ON s.assignment_id = a.id " +
                     "JOIN users u ON s.learner_id = u.id " +
                     "WHERE s.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, submissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSubmission(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve submission: " + e.getMessage(), e);
        }
        return null;
    }

    private Submission mapSubmission(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int assignmentId = rs.getInt("assignment_id");
        String assignmentTitle = rs.getString("assignment_title");
        int learnerId = rs.getInt("learner_id");
        String learnerName = rs.getString("learner_name");
        String submissionText = rs.getString("submission_text");
        int scoreVal = rs.getInt("score");
        Integer score = rs.wasNull() ? null : scoreVal;
        String feedback = rs.getString("feedback");
        String status = rs.getString("status");
        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        Timestamp gradedAt = rs.getTimestamp("graded_at");

        return new Submission(id, assignmentId, assignmentTitle, learnerId, learnerName, submissionText, score, feedback, status, submittedAt, gradedAt);
    }
}
