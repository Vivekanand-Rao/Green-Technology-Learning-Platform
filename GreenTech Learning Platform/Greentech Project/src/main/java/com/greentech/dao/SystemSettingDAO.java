package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.SystemSetting;
import com.greentech.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of System Settings operations.
 */
public class SystemSettingDAO implements ISystemSettingDAO {

    @Override
    public List<SystemSetting> getAllSettings() throws DatabaseException {
        List<SystemSetting> list = new ArrayList<>();
        String sql = "SELECT setting_key, setting_value, description, updated_at FROM system_settings ORDER BY setting_key ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new SystemSetting(
                        rs.getString("setting_key"),
                        rs.getString("setting_value"),
                        rs.getString("description"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            // If table does not exist, return defaults
            list.add(new SystemSetting("platform_name", "GreenLearn Academy", "Public platform title", new Timestamp(System.currentTimeMillis())));
            list.add(new SystemSetting("allow_registration", "true", "Public registration flag", new Timestamp(System.currentTimeMillis())));
            list.add(new SystemSetting("maintenance_mode", "false", "Maintenance state", new Timestamp(System.currentTimeMillis())));
        }
        return list;
    }

    @Override
    public String getSettingValue(String key, String defaultValue) throws DatabaseException {
        if (key == null) return defaultValue;
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        } catch (SQLException e) {
            return defaultValue;
        }
        return defaultValue;
    }

    @Override
    public boolean updateSetting(String key, String value) throws DatabaseException {
        if (key == null || value == null) return false;
        String sql = "INSERT INTO system_settings(setting_key, setting_value, description) VALUES(?, ?, 'Configuration') " +
                     "ON DUPLICATE KEY UPDATE setting_value = ?, updated_at = NOW()";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key.trim());
            ps.setString(2, value.trim());
            ps.setString(3, value.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update setting " + key + ": " + e.getMessage(), e);
        }
    }
}
