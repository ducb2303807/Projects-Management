package com.group4.projects_management.dto;

public class AppRoleDTO {
    private Long appRoleId;
    private String appRoleName;
    private String appRoleDescription;

    public AppRoleDTO() {}

    public AppRoleDTO(Long appRoleId, String appRoleName, String appRoleDescription) {
        this.appRoleId = appRoleId;
        this.appRoleName = appRoleName;
        this.appRoleDescription = appRoleDescription;
    }

    public Long getAppRoleId() { return appRoleId; }
    public void setAppRoleId(Long appRoleId) { this.appRoleId = appRoleId; }

    public String getAppRoleName() { return appRoleName; }
    public void setAppRoleName(String appRoleName) { this.appRoleName = appRoleName; }

    public String getAppRoleDescription() { return appRoleDescription; }
    public void setAppRoleDescription(String appRoleDescription) { this.appRoleDescription = appRoleDescription; }
}
