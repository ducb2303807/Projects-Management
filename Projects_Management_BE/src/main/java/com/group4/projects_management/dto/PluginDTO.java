package com.group4.projects_management.dto;

public class PluginDTO {
    private Long pluginId;
    private String pluginName;
    private String pluginDescription;

    public PluginDTO() {}

    public PluginDTO(Long pluginId, String pluginName, String pluginDescription) {
        this.pluginId = pluginId;
        this.pluginName = pluginName;
        this.pluginDescription = pluginDescription;
    }

    public Long getPluginId() { return pluginId; }
    public void setPluginId(Long pluginId) { this.pluginId = pluginId; }

    public String getPluginName() { return pluginName; }
    public void setPluginName(String pluginName) { this.pluginName = pluginName; }

    public String getPluginDescription() { return pluginDescription; }
    public void setPluginDescription(String pluginDescription) { this.pluginDescription = pluginDescription; }
}
