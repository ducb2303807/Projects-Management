package com.group4.projects_management.dto;

import java.time.LocalDateTime;

public class TaskDTO {
    private Long taskId;
    private Long projectId;
    private Long taskStatusId;
    private Long priorityId;
    private String taskName;
    private LocalDateTime taskCreatedAt;
    private LocalDateTime taskDeadline;
    private String taskDescription;

    public TaskDTO() {}

    public TaskDTO(Long taskId, Long projectId, Long taskStatusId, Long priorityId,
                   String taskName, LocalDateTime taskCreatedAt,
                   LocalDateTime taskDeadline, String taskDescription) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.taskStatusId = taskStatusId;
        this.priorityId = priorityId;
        this.taskName = taskName;
        this.taskCreatedAt = taskCreatedAt;
        this.taskDeadline = taskDeadline;
        this.taskDescription = taskDescription;
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getTaskStatusId() { return taskStatusId; }
    public void setTaskStatusId(Long taskStatusId) { this.taskStatusId = taskStatusId; }

    public Long getPriorityId() { return priorityId; }
    public void setPriorityId(Long priorityId) { this.priorityId = priorityId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public LocalDateTime getTaskCreatedAt() { return taskCreatedAt; }
    public void setTaskCreatedAt(LocalDateTime taskCreatedAt) { this.taskCreatedAt = taskCreatedAt; }

    public LocalDateTime getTaskDeadline() { return taskDeadline; }
    public void setTaskDeadline(LocalDateTime taskDeadline) { this.taskDeadline = taskDeadline; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
}
