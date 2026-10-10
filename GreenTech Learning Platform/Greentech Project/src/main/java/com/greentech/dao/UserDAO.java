package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.User;
import com.greentech.util.DBConnection;
import com.greentech.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of User Data Access Object.
 * Employs PreparedStatement, try-with-resources, and cryptographic password handling.
 */
public class UserDAO implements IUserDAO {

    @Override
    public User findById(Integer id) throws DatabaseException {
        if (id == null) return null;
        String sql = "SELECT id, name, email, role, status, created_at FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<User> getAll() throws DatabaseException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, email, role, status, created_at FROM users ORDER BY id ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve users: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        if (id == null) return false;
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public User login(String email, String password) throws DatabaseException {
        if (email == null || password == null) return null;
        String cleanEmail = email.trim().toLowerCase();

        // Query supporting both schema variants (password_hash/salt or legacy password column)
        String sql = "SELECT id, name, email, role, status, created_at, " +
                     "COALESCE(password_hash, '') AS pass_hash, " +
                     "COALESCE(salt, '') AS pass_salt " +
                     "FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cleanEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("pass_hash");
                    String storedSalt = rs.getString("pass_salt");

                    if (PasswordUtil.verifyPassword(password, storedHash, storedSalt)) {
                        return mapUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            // Check for legacy schema fallback where column was named 'password'
            return loginLegacyFallback(cleanEmail, password);
        }
        return null;
    }

    private User loginLegacyFallback(String email, String password) throws DatabaseException {
        String sql = "SELECT id, name, email, role, status, created_at, password FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    if (password.equals(stored)) {
                        return mapUser(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Login database query failed: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public boolean emailExists(String email) throws DatabaseException {
        if (email == null) return false;
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking email existence: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean register(String name, String email, String password, String role) throws DatabaseException {
        // Enforce safety: Public registration only allows LEARNER or INSTRUCTOR
        String safeRole = "LEARNER";
        if ("INSTRUCTOR".equalsIgnoreCase(role)) {
            safeRole = "INSTRUCTOR";
        }
        return createUser(name, email, password, safeRole, "ACTIVE");
    }

    @Override
    public boolean createUser(String name, String email, String password, String role, String status) throws DatabaseException {
        if (name == null || email == null || password == null) return false;

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        String finalRole = (role != null) ? role.toUpperCase() : "LEARNER";
        String finalStatus = (status != null) ? status.toUpperCase() : "ACTIVE";

        String sql = "INSERT INTO users(name, email, password_hash, salt, role, status) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, hash);
            ps.setString(4, salt);
            ps.setString(5, finalRole);
            ps.setString(6, finalStatus);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Attempt legacy fallback column insertion if table hasn't been migrated yet
            return createUserLegacyFallback(name, email, password, finalRole);
        }
    }

    private boolean createUserLegacyFallback(String name, String email, String password, String role) throws DatabaseException {
        String sql = "INSERT INTO users(name, email, password, role) VALUES(?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, password);
            ps.setString(4, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to insert user: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean updateUser(int id, String name, String email, String role, String status) throws DatabaseException {
        String sql = "UPDATE users SET name = ?, email = ?, role = ?, status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, role != null ? role.toUpperCase() : "LEARNER");
            ps.setString(4, status != null ? status.toUpperCase() : "ACTIVE");
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Check if status column doesn't exist in legacy schema
            String legacySql = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(legacySql)) {
                ps.setString(1, name.trim());
                ps.setString(2, email.trim().toLowerCase());
                ps.setString(3, role != null ? role.toUpperCase() : "LEARNER");
                ps.setInt(4, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                throw new DatabaseException("Failed to update user: " + ex.getMessage(), ex);
            }
        }
    }

    @Override
    public int getUserCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count users: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<User> getUsersByRole(String role) throws DatabaseException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, email, role, status, created_at FROM users WHERE role = ? ORDER BY name ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role != null ? role.toUpperCase() : "LEARNER");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users by role: " + e.getMessage(), e);
        }
        return list;
    }

    public List<User> getAllUsers() throws SQLException {
        try {
            return getAll();
        } catch (DatabaseException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String role = rs.getString("role");
        String status = "ACTIVE";
        try {
            status = rs.getString("status");
        } catch (SQLException ignored) {}
        Timestamp createdAt = null;
        try {
            createdAt = rs.getTimestamp("created_at");
        } catch (SQLException ignored) {}

        User u = User.createSpecialized(id, name, email, role);
        u.setStatus(status);
        u.setCreatedAt(createdAt);
        return u;
    }
}
