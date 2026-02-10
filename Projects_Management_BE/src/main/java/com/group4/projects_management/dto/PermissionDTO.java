package com.group4.projects_management.dto;

public class PermissionDTO {
    private Long permissionId;
    private String permissionCode;
    private String permissionDescription;

    public PermissionDTO() {}

    public PermissionDTO(Long permissionId, String permissionCode, String permissionDescription) {
        this.permissionId = permissionId;
        this.permissionCode = permissionCode;
        this.permissionDescription = permissionDescription;
    }

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }

    public String getPermissionDescription() { return permissionDescription; }
    public void setPermissionDescription(String permissionDescription) { this.permissionDescription = permissionDescription; }
}
