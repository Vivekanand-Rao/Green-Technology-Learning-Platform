package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Certificate;
import com.greentech.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Concrete implementation of Certificate Data Access Object.
 */
public class CertificateDAO implements ICertificateDAO {

    @Override
    public Certificate findById(Integer id) throws DatabaseException {
        if (id == null) return null;
        String sql = "SELECT cert.id, cert.learner_id, u.name AS learner_name, cert.course_id, c.title AS course_title, " +
                     "cert.certificate_code, cert.issue_date " +
                     "FROM certificates cert " +
                     "JOIN users u ON cert.learner_id = u.id " +
                     "JOIN courses c ON cert.course_id = c.id " +
                     "WHERE cert.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCertificate(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find certificate: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Certificate> getAll() throws DatabaseException {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT cert.id, cert.learner_id, u.name AS learner_name, cert.course_id, c.title AS course_title, " +
                     "cert.certificate_code, cert.issue_date " +
                     "FROM certificates cert " +
                     "JOIN users u ON cert.learner_id = u.id " +
                     "JOIN courses c ON cert.course_id = c.id " +
                     "ORDER BY cert.issue_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCertificate(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load certificates: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        if (id == null) return false;
        String sql = "DELETE FROM certificates WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete certificate: " + e.getMessage(), e);
        }
    }

    @Override
    public Certificate issueCertificate(int learnerId, int courseId) throws DatabaseException {
        // Check if certificate already exists
        String checkSql = "SELECT cert.id, cert.learner_id, u.name AS learner_name, cert.course_id, c.title AS course_title, " +
                          "cert.certificate_code, cert.issue_date " +
                          "FROM certificates cert " +
                          "JOIN users u ON cert.learner_id = u.id " +
                          "JOIN courses c ON cert.course_id = c.id " +
                          "WHERE cert.learner_id = ? AND cert.course_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setInt(1, learnerId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCertificate(rs);
                }
            }
        } catch (SQLException ignored) {}

        String code = "GL-" + System.currentTimeMillis() % 100000 + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String insertSql = "INSERT INTO certificates(learner_id, course_id, certificate_code, issue_date) VALUES(?, ?, ?, NOW())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, learnerId);
            ps.setInt(2, courseId);
            ps.setString(3, code);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to issue certificate: " + e.getMessage(), e);
        }
        return getByCode(code);
    }

    @Override
    public List<Certificate> getCertificatesByLearner(int learnerId) throws DatabaseException {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT cert.id, cert.learner_id, u.name AS learner_name, cert.course_id, c.title AS course_title, " +
                     "cert.certificate_code, cert.issue_date " +
                     "FROM certificates cert " +
                     "JOIN users u ON cert.learner_id = u.id " +
                     "JOIN courses c ON cert.course_id = c.id " +
                     "WHERE cert.learner_id = ? " +
                     "ORDER BY cert.issue_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, learnerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCertificate(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load learner certificates: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Certificate getByCode(String code) throws DatabaseException {
        if (code == null) return null;
        String sql = "SELECT cert.id, cert.learner_id, u.name AS learner_name, cert.course_id, c.title AS course_title, " +
                     "cert.certificate_code, cert.issue_date " +
                     "FROM certificates cert " +
                     "JOIN users u ON cert.learner_id = u.id " +
                     "JOIN courses c ON cert.course_id = c.id " +
                     "WHERE cert.certificate_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCertificate(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query certificate by code: " + e.getMessage(), e);
        }
        return null;
    }

    private Certificate mapCertificate(ResultSet rs) throws SQLException {
        return new Certificate(
                rs.getInt("id"),
                rs.getInt("learner_id"),
                rs.getString("learner_name"),
                rs.getInt("course_id"),
                rs.getString("course_title"),
                rs.getString("certificate_code"),
                rs.getTimestamp("issue_date")
        );
    }
}
