package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.SystemSetting;

import java.util.List;

/**
 * Contract for managing platform system configuration settings.
 */
public interface ISystemSettingDAO {
    List<SystemSetting> getAllSettings() throws DatabaseException;
    String getSettingValue(String key, String defaultValue) throws DatabaseException;
    boolean updateSetting(String key, String value) throws DatabaseException;
}
