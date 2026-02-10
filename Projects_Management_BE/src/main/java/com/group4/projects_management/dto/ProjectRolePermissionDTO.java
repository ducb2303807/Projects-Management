package com.group4.projects_management.dto;

public class ProjectRolePermissionDTO {
    private Long projectRoleId;
    private Long permissionId;

    public ProjectRolePermissionDTO() {}

    public ProjectRolePermissionDTO(Long projectRoleId, Long permissionId) {
        this.projectRoleId = projectRoleId;
        this.permissionId = permissionId;
    }

    public Long getProjectRoleId() { return projectRoleId; }
    public void setProjectRoleId(Long projectRoleId) { this.projectRoleId = projectRoleId; }

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }
}
