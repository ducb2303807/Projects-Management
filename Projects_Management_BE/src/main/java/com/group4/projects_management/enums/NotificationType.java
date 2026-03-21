package com.group4.projects_management.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    // PROJECT GROUP (NHÓM DỰ ÁN)
    PROJECT_UPDATED("Project Updated"),
    PROJECT_DELETED("Project Deleted"),

    // MEMBER GROUP (NHÓM THÀNH VIÊN)
    PROJECT_INVITATION("Project Invitation"),
    MEMBER_JOINED("New Member Joined"),
    MEMBER_LEFT("Member Left Project"),
    MEMBER_REMOVED("Member Removed"),

    // TASK GROUP (NHÓM CÔNG VIỆC)
    TASK_ASSIGNED("New Task Assigned"),
    TASK_UNASSIGNED("Task Unassigned"),
    TASK_UPDATED("Task Updated"),
    TASK_STATUS_CHANGED("Task Status Changed"),

    // INTERACTION GROUP (NHÓM TƯƠNG TÁC)
    NEW_COMMENT("New Comment"),
    MENTIONED_IN_COMMENT("Mentioned in Comment"),

    // SYSTEM & REMINDER GROUP (NHÓM HỆ THỐNG)
    TASK_DEADLINE_APPROACHING("Deadline Approaching"),
    TASK_OVERDUE("Task Overdue");

    private final String displayTitle;

    NotificationType(String displayTitle) {
        this.displayTitle = displayTitle;
    }
}
