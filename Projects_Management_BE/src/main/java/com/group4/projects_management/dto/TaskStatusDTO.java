package com.group4.projects_management.dto;

public class TaskStatusDTO {
    private Long taskStatusId;
    private String taskStatusName;
    private String taskStatusDescription;

    public TaskStatusDTO() {}

    public TaskStatusDTO(Long taskStatusId, String taskStatusName, String taskStatusDescription) {
        this.taskStatusId = taskStatusId;
        this.taskStatusName = taskStatusName;
        this.taskStatusDescription = taskStatusDescription;
    }

    public Long getTaskStatusId() { return taskStatusId; }
    public void setTaskStatusId(Long taskStatusId) { this.taskStatusId = taskStatusId; }

    public String getTaskStatusName() { return taskStatusName; }
    public void setTaskStatusName(String taskStatusName) { this.taskStatusName = taskStatusName; }

    public String getTaskStatusDescription() { return taskStatusDescription; }
    public void setTaskStatusDescription(String taskStatusDescription) { this.taskStatusDescription = taskStatusDescription; }
}
