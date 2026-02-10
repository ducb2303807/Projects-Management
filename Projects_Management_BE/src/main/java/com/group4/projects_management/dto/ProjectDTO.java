package com.group4.projects_management.dto;

import java.time.LocalDateTime;

public class ProjectDTO {
    private Long projectId;
    private Long projectCreateById;
    private Long projectStatusId;
    private String projectName;
    private String projectDescription;
    private LocalDateTime projectStartAt;
    private LocalDateTime projectEndAt;
    private LocalDateTime projectCreatedAt;
    private LocalDateTime projectUpdateAt;

    public ProjectDTO() {}

    public ProjectDTO(Long projectId, Long projectCreateById, Long projectStatusId,
                      String projectName, String projectDescription,
                      LocalDateTime projectStartAt, LocalDateTime projectEndAt,
                      LocalDateTime projectCreatedAt, LocalDateTime projectUpdateAt) {
        this.projectId = projectId;
        this.projectCreateById = projectCreateById;
        this.projectStatusId = projectStatusId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartAt = projectStartAt;
        this.projectEndAt = projectEndAt;
        this.projectCreatedAt = projectCreatedAt;
        this.projectUpdateAt = projectUpdateAt;
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getProjectCreateById() { return projectCreateById; }
    public void setProjectCreateById(Long projectCreateById) { this.projectCreateById = projectCreateById; }

    public Long getProjectStatusId() { return projectStatusId; }
    public void setProjectStatusId(Long projectStatusId) { this.projectStatusId = projectStatusId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectDescription() { return projectDescription; }
    public void setProjectDescription(String projectDescription) { this.projectDescription = projectDescription; }

    public LocalDateTime getProjectStartAt() { return projectStartAt; }
    public void setProjectStartAt(LocalDateTime projectStartAt) { this.projectStartAt = projectStartAt; }

    public LocalDateTime getProjectEndAt() { return projectEndAt; }
    public void setProjectEndAt(LocalDateTime projectEndAt) { this.projectEndAt = projectEndAt; }

    public LocalDateTime getProjectCreatedAt() { return projectCreatedAt; }
    public void setProjectCreatedAt(LocalDateTime projectCreatedAt) { this.projectCreatedAt = projectCreatedAt; }

    public LocalDateTime getProjectUpdateAt() { return projectUpdateAt; }
    public void setProjectUpdateAt(LocalDateTime projectUpdateAt) { this.projectUpdateAt = projectUpdateAt; }
}
