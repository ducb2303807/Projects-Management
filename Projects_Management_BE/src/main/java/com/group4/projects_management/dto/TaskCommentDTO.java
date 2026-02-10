package com.group4.projects_management.dto;

import java.time.LocalDateTime;

public class TaskCommentDTO {
    private Long taskCommentId;
    private Long taskId;
    private Long projectMemberId;
    private Long parentId;
    private String taskCommentText;
    private LocalDateTime taskCommentCreatedAt;

    public TaskCommentDTO() {}

    public TaskCommentDTO(Long taskCommentId, Long taskId, Long projectMemberId,
                          Long parentId, String taskCommentText, LocalDateTime taskCommentCreatedAt) {
        this.taskCommentId = taskCommentId;
        this.taskId = taskId;
        this.projectMemberId = projectMemberId;
        this.parentId = parentId;
        this.taskCommentText = taskCommentText;
        this.taskCommentCreatedAt = taskCommentCreatedAt;
    }

    public Long getTaskCommentId() { return taskCommentId; }
    public void setTaskCommentId(Long taskCommentId) { this.taskCommentId = taskCommentId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getProjectMemberId() { return projectMemberId; }
    public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getTaskCommentText() { return taskCommentText; }
    public void setTaskCommentText(String taskCommentText) { this.taskCommentText = taskCommentText; }

    public LocalDateTime getTaskCommentCreatedAt() { return taskCommentCreatedAt; }
    public void setTaskCommentCreatedAt(LocalDateTime taskCommentCreatedAt) { this.taskCommentCreatedAt = taskCommentCreatedAt; }
}
