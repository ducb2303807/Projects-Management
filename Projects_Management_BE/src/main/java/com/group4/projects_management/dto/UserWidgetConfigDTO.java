package com.group4.projects_management.dto;

public class UserWidgetConfigDTO {
    private Long userWidgetConfigId;
    private Long userId;
    private Long pluginId;
    private String configData;

    public UserWidgetConfigDTO() {}

    public UserWidgetConfigDTO(Long userWidgetConfigId, Long userId, Long pluginId, String configData) {
        this.userWidgetConfigId = userWidgetConfigId;
        this.userId = userId;
        this.pluginId = pluginId;
        this.configData = configData;
    }

    public Long getUserWidgetConfigId() { return userWidgetConfigId; }
    public void setUserWidgetConfigId(Long userWidgetConfigId) { this.userWidgetConfigId = userWidgetConfigId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPluginId() { return pluginId; }
    public void setPluginId(Long pluginId) { this.pluginId = pluginId; }

    public String getConfigData() { return configData; }
    public void setConfigData(String configData) { this.configData = configData; }
}
