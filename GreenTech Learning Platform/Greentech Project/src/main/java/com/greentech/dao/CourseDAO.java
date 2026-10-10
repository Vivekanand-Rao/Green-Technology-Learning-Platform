package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Course;
import com.greentech.model.Enrollment;
import com.greentech.model.EnrollmentStats;
import com.greentech.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of Course Data Access Object.
 * Employs PreparedStatement, try-with-resources, and comprehensive JDBC operations.
 */
public class CourseDAO implements ICourseDAO {

    @Override
    public Course findById(Integer id) throws DatabaseException {
        if (id == null) return null;
        String sql = "SELECT c.id, c.title, COALESCE(c.category, 'Renewable Energy') AS category, " +
                     "c.description, c.content, c.instructor_id, COALESCE(u.name, 'Instructor') AS instructor, " +
                     "c.status, c.created_at, c.created_at AS updated_at " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.instructor_id = u.id " +
                     "WHERE c.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find course by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Course> getAll() throws DatabaseException {
        return getAllCourses();
    }

    public List<Course> getAllCourses() throws DatabaseException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.title, COALESCE(c.category, 'Renewable Energy') AS category, " +
                     "c.description, c.content, c.instructor_id, COALESCE(u.name, 'Instructor') AS instructor, " +
                     "c.status, c.created_at, c.created_at AS updated_at " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.instructor_id = u.id " +
                     "ORDER BY c.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCourse(rs));
            }
        } catch (SQLException e) {
            // Fallback for legacy database tables without category column
            return getAllCoursesLegacy();
        }
        return list;
    }

    private List<Course> getAllCoursesLegacy() throws DatabaseException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.title, c.description, c.content, c.instructor_id, " +
                     "COALESCE(u.name, 'Instructor') AS instructor, c.status " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.instructor_id = u.id " +
                     "ORDER BY c.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                        rs.getString("instructor"), rs.getString("status"));
                c.setContent(rs.getString("content"));
                c.setInstructorId(rs.getInt("instructor_id"));
                list.add(c);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load courses from database: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        if (id == null) return false;
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete course: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Course> getApprovedCourses() throws DatabaseException {
        return getCoursesByStatus("APPROVED");
    }

    @Override
    public List<Course> getCoursesByStatus(String status) throws DatabaseException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.title, COALESCE(c.category, 'Renewable Energy') AS category, " +
                     "c.description, c.content, c.instructor_id, COALESCE(u.name, 'Instructor') AS instructor, " +
                     "c.status, c.created_at, c.created_at AS updated_at " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.instructor_id = u.id " +
                     "WHERE c.status = ? " +
                     "ORDER BY c.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            // Legacy schema fallback
            return getCoursesByStatusLegacy(status);
        }
        return list;
    }

    private List<Course> getCoursesByStatusLegacy(String status) throws DatabaseException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.title, c.description, c.content, c.instructor_id, " +
                     "COALESCE(u.name, 'Instructor') AS instructor, c.status " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.instructor_id = u.id " +
                     "WHERE c.status = ? " +
                     "ORDER BY c.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                            rs.getString("instructor"), rs.getString("status"));
                    c.setContent(rs.getString("content"));
                    c.setInstructorId(rs.getInt("instructor_id"));
                    list.add(c);
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to get courses by status: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public List<Course> getCoursesByInstructor(int instructorId) throws DatabaseException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.title, COALESCE(c.category, 'Renewable Energy') AS category, " +
                     "c.description, c.content, c.instructor_id, COALESCE(u.name, 'You') AS instructor, " +
                     "c.status, c.created_at, c.created_at AS updated_at " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.instructor_id = u.id " +
                     "WHERE c.instructor_id = ? " +
                     "ORDER BY c.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch instructor courses: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Course> getEnrolledCourses(int learnerId) throws DatabaseException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.title, COALESCE(c.category, 'Renewable Energy') AS category, " +
                     "c.description, c.content, c.instructor_id, COALESCE(u.name, 'Instructor') AS instructor, " +
                     "c.status, c.created_at, c.created_at AS updated_at " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "LEFT JOIN users u ON c.instructor_id = u.id " +
                     "WHERE e.learner_id = ? " +
                     "ORDER BY e.enrolled_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, learnerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch enrolled courses: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Enrollment> getEnrollmentsByLearner(int learnerId) throws DatabaseException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.id, e.learner_id, u.name AS learner_name, e.course_id, c.title AS course_title, " +
                     "e.progress, COALESCE(e.status, 'ACTIVE') AS status, e.enrolled_at, e.completed_at " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN users u ON e.learner_id = u.id " +
                     "WHERE e.learner_id = ? " +
                     "ORDER BY e.enrolled_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, learnerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("learner_id"),
                            rs.getString("learner_name"),
                            rs.getInt("course_id"),
                            rs.getString("course_title"),
                            rs.getInt("progress"),
                            rs.getString("status"),
                            rs.getTimestamp("enrolled_at"),
                            rs.getTimestamp("completed_at")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load learner enrollments: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean isEnrolled(int learnerId, int courseId) throws DatabaseException {
        String sql = "SELECT 1 FROM enrollments WHERE learner_id = ? AND course_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, learnerId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking enrollment status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean enroll(int learnerId, int courseId) throws DatabaseException {
        String sql = "INSERT INTO enrollments(learner_id, course_id, progress, status, enrolled_at) VALUES(?, ?, 0, 'ACTIVE', NOW()) " +
                     "ON DUPLICATE KEY UPDATE learner_id = learner_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, learnerId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Legacy schema syntax fallback
            String legacySql = "INSERT IGNORE INTO enrollments(learner_id, course_id) VALUES(?, ?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(legacySql)) {
                ps.setInt(1, learnerId);
                ps.setInt(2, courseId);
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                throw new DatabaseException("Failed to enroll learner: " + ex.getMessage(), ex);
            }
        }
    }

    @Override
    public boolean updateProgress(int learnerId, int courseId, int progress) throws DatabaseException {
        int bounded = Math.max(0, Math.min(100, progress));
        String status = (bounded >= 100) ? "COMPLETED" : "ACTIVE";
        Timestamp completedTime = (bounded >= 100) ? new Timestamp(System.currentTimeMillis()) : null;

        String sql = "UPDATE enrollments SET progress = ?, status = ?, completed_at = ? WHERE learner_id = ? AND course_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bounded);
            ps.setString(2, status);
            ps.setTimestamp(3, completedTime);
            ps.setInt(4, learnerId);
            ps.setInt(5, courseId);
            int rows = ps.executeUpdate();

            // If progress reached 100%, automatically grant a certificate if not already issued
            if (bounded >= 100 && rows > 0) {
                new CertificateDAO().issueCertificate(learnerId, courseId);
            }
            return rows > 0;
        } catch (SQLException e) {
            // Legacy fallback if status/completed_at columns are absent
            String legacySql = "UPDATE enrollments SET progress = ? WHERE learner_id = ? AND course_id = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(legacySql)) {
                ps.setInt(1, bounded);
                ps.setInt(2, learnerId);
                ps.setInt(3, courseId);
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                throw new DatabaseException("Failed to update progress: " + ex.getMessage(), ex);
            }
        }
    }

    @Override
    public int getEnrollmentCount(int learnerId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE learner_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, learnerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting enrollments: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean createCourse(String title, String category, String description, String content, int instructorId) throws DatabaseException {
        String sql = "INSERT INTO courses(title, category, description, content, instructor_id, status) VALUES(?, ?, ?, ?, ?, 'PENDING')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title != null ? title.trim() : "");
            ps.setString(2, category != null && !category.trim().isEmpty() ? category.trim() : "Renewable Energy");
            ps.setString(3, description != null ? description.trim() : "");
            ps.setString(4, content != null ? content.trim() : "");
            ps.setInt(5, instructorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Legacy schema without category
            String legacySql = "INSERT INTO courses(title, description, content, instructor_id, status) VALUES(?, ?, ?, ?, 'PENDING')";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(legacySql)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, content);
                ps.setInt(4, instructorId);
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                throw new DatabaseException("Failed to create course: " + ex.getMessage(), ex);
            }
        }
    }

    // Overload for old servlet signatures
    public boolean createCourse(String title, String description, String content, int instructorId) throws SQLException {
        try {
            return createCourse(title, "Renewable Energy", description, content, instructorId);
        } catch (DatabaseException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    @Override
    public boolean updateCourse(int id, String title, String category, String description, String content) throws DatabaseException {
        String sql = "UPDATE courses SET title = ?, category = ?, description = ?, content = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, category);
            ps.setString(3, description);
            ps.setString(4, content);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update course: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(int courseId, String status) throws DatabaseException {
        String sql = "UPDATE courses SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update course status: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPendingCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM courses WHERE status = 'PENDING'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count pending courses: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<EnrollmentStats> getCourseEnrollmentStats() throws DatabaseException {
        List<EnrollmentStats> stats = new ArrayList<>();
        String sql = "SELECT c.id, c.title, " +
                     "COUNT(e.id) AS total_enrolled, " +
                     "SUM(CASE WHEN e.progress >= 100 OR e.status = 'COMPLETED' THEN 1 ELSE 0 END) AS total_completed " +
                     "FROM courses c " +
                     "LEFT JOIN enrollments e ON c.id = e.course_id " +
                     "GROUP BY c.id, c.title " +
                     "ORDER BY total_enrolled DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stats.add(new EnrollmentStats(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("total_enrolled"),
                        rs.getInt("total_completed")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate course enrollment statistics: " + e.getMessage(), e);
        }
        return stats;
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String category = "Renewable Energy";
        try {
            category = rs.getString("category");
        } catch (SQLException ignored) {}
        String description = rs.getString("description");
        String content = rs.getString("content");
        int instructorId = rs.getInt("instructor_id");
        String instructor = rs.getString("instructor");
        String status = rs.getString("status");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new Course(id, title, category, description, content, instructorId, instructor, status, createdAt, updatedAt);
    }
}
