package com.group4.projects_management.dto;

public class ProjectRoleDTO {
    private Long projectRoleId;
    private String projectRoleName;
    private String projectRoleDescription;

    public ProjectRoleDTO() {}

    public ProjectRoleDTO(Long projectRoleId, String projectRoleName, String projectRoleDescription) {
        this.projectRoleId = projectRoleId;
        this.projectRoleName = projectRoleName;
        this.projectRoleDescription = projectRoleDescription;
    }

    public Long getProjectRoleId() { return projectRoleId; }
    public void setProjectRoleId(Long projectRoleId) { this.projectRoleId = projectRoleId; }

    public String getProjectRoleName() { return projectRoleName; }
    public void setProjectRoleName(String projectRoleName) { this.projectRoleName = projectRoleName; }

    public String getProjectRoleDescription() { return projectRoleDescription; }
    public void setProjectRoleDescription(String projectRoleDescription) { this.projectRoleDescription = projectRoleDescription; }
}
