package com.group4.projects_management.dto;

import java.time.LocalDateTime;

public class TaskAssignmentDTO {
    private Long taskAssignmentId;
    private Long taskId;
    private Long taskAssignee;
    private Long taskAssigner;
    private LocalDateTime taskAssignedAt;

    public TaskAssignmentDTO() {}

    public TaskAssignmentDTO(Long taskAssignmentId, Long taskId, Long taskAssignee,
                             Long taskAssigner, LocalDateTime taskAssignedAt) {
        this.taskAssignmentId = taskAssignmentId;
        this.taskId = taskId;
        this.taskAssignee = taskAssignee;
        this.taskAssigner = taskAssigner;
        this.taskAssignedAt = taskAssignedAt;
    }

    public Long getTaskAssignmentId() { return taskAssignmentId; }
    public void setTaskAssignmentId(Long taskAssignmentId) { this.taskAssignmentId = taskAssignmentId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getTaskAssignee() { return taskAssignee; }
    public void setTaskAssignee(Long taskAssignee) { this.taskAssignee = taskAssignee; }

    public Long getTaskAssigner() { return taskAssigner; }
    public void setTaskAssigner(Long taskAssigner) { this.taskAssigner = taskAssigner; }

    public LocalDateTime getTaskAssignedAt() { return taskAssignedAt; }
    public void setTaskAssignedAt(LocalDateTime taskAssignedAt) { this.taskAssignedAt = taskAssignedAt; }
}
