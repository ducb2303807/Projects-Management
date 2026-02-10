package com.group4.projects_management.dto;

public class AppRolePermissionDTO {
    private Long appRoleId;
    private Long permissionId;

    public AppRolePermissionDTO() {}

    public AppRolePermissionDTO(Long appRoleId, Long permissionId) {
        this.appRoleId = appRoleId;
        this.permissionId = permissionId;
    }

    public Long getAppRoleId() { return appRoleId; }
    public void setAppRoleId(Long appRoleId) { this.appRoleId = appRoleId; }

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }
}
