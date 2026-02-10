package com.group4.projects_management.dto;

import java.time.LocalDateTime;

public class TaskHistoryDTO {
    private Long taskHistoryId;
    private Long taskId;
    private Long projectMemberId;
    private String taskHistoryColumnName;
    private String taskHistoryOldValue;
    private String taskHistoryNewValue;
    private LocalDateTime taskHistoryChangedAt;

    public TaskHistoryDTO() {}

    public TaskHistoryDTO(Long taskHistoryId, Long taskId, Long projectMemberId,
                          String taskHistoryColumnName, String taskHistoryOldValue,
                          String taskHistoryNewValue, LocalDateTime taskHistoryChangedAt) {
        this.taskHistoryId = taskHistoryId;
        this.taskId = taskId;
        this.projectMemberId = projectMemberId;
        this.taskHistoryColumnName = taskHistoryColumnName;
        this.taskHistoryOldValue = taskHistoryOldValue;
        this.taskHistoryNewValue = taskHistoryNewValue;
        this.taskHistoryChangedAt = taskHistoryChangedAt;
    }

    public Long getTaskHistoryId() { return taskHistoryId; }
    public void setTaskHistoryId(Long taskHistoryId) { this.taskHistoryId = taskHistoryId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getProjectMemberId() { return projectMemberId; }
    public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }

    public String getTaskHistoryColumnName() { return taskHistoryColumnName; }
    public void setTaskHistoryColumnName(String taskHistoryColumnName) { this.taskHistoryColumnName = taskHistoryColumnName; }

    public String getTaskHistoryOldValue() { return taskHistoryOldValue; }
    public void setTaskHistoryOldValue(String taskHistoryOldValue) { this.taskHistoryOldValue = taskHistoryOldValue; }

    public String getTaskHistoryNewValue() { return taskHistoryNewValue; }
    public void setTaskHistoryNewValue(String taskHistoryNewValue) { this.taskHistoryNewValue = taskHistoryNewValue; }

    public LocalDateTime getTaskHistoryChangedAt() { return taskHistoryChangedAt; }
    public void setTaskHistoryChangedAt(LocalDateTime taskHistoryChangedAt) { this.taskHistoryChangedAt = taskHistoryChangedAt; }
}
