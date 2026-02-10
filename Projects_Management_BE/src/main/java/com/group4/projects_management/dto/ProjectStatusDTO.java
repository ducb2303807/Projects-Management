package com.group4.projects_management.dto;

public class ProjectStatusDTO {
    private Long projectStatusId;
    private String projectStatusName;
    private String projectStatusDescription;

    public ProjectStatusDTO() {}

    public ProjectStatusDTO(Long projectStatusId, String projectStatusName, String projectStatusDescription) {
        this.projectStatusId = projectStatusId;
        this.projectStatusName = projectStatusName;
        this.projectStatusDescription = projectStatusDescription;
    }

    public Long getProjectStatusId() { return projectStatusId; }
    public void setProjectStatusId(Long projectStatusId) { this.projectStatusId = projectStatusId; }

    public String getProjectStatusName() { return projectStatusName; }
    public void setProjectStatusName(String projectStatusName) { this.projectStatusName = projectStatusName; }

    public String getProjectStatusDescription() { return projectStatusDescription; }
    public void setProjectStatusDescription(String projectStatusDescription) { this.projectStatusDescription = projectStatusDescription; }
}
