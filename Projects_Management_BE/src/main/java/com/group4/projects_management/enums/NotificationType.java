package com.group4.projects_management.enums;

public enum NotificationType {
    //NHÓM DỰ ÁN (Project Level)
    PROJECT_UPDATED,
    PROJECT_DELETED,

    // NHÓM THÀNH VIÊN (Member Level)
    PROJECT_INVITATION,
    MEMBER_JOINED,
    MEMBER_LEFT,
    MEMBER_REMOVED,


    // NHÓM CÔNG VIỆC (Task Level)
    TASK_ASSIGNED,
    TASK_UNASSIGNED,
    TASK_UPDATED,
    TASK_STATUS_CHANGED,

    // NHÓM TƯƠNG TÁC (Comment)
    NEW_COMMENT,
    MENTIONED_IN_COMMENT,


    // NHÓM HỆ THỐNG & NHẮC NHỞ
    TASK_DEADLINE_APPROACHING,
    TASK_OVERDUE
}
