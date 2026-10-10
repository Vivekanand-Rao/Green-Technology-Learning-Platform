package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing a system-wide configuration setting.
 */
public class SystemSetting {
    private String settingKey;
    private String settingValue;
    private String description;
    private Timestamp updatedAt;

    public SystemSetting() {}

    public SystemSetting(String settingKey, String settingValue, String description, Timestamp updatedAt) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
