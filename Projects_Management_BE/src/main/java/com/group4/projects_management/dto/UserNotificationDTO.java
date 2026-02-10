package com.group4.projects_management.dto;

public class UserNotificationDTO {
    private Long userNotificationId;
    private Long userId;
    private Long notificationId;
    private boolean isRead;

    public UserNotificationDTO() {}

    public UserNotificationDTO(Long userNotificationId, Long userId, Long notificationId, boolean isRead) {
        this.userNotificationId = userNotificationId;
        this.userId = userId;
        this.notificationId = notificationId;
        this.isRead = isRead;
    }

    public Long getUserNotificationId() { return userNotificationId; }
    public void setUserNotificationId(Long userNotificationId) { this.userNotificationId = userNotificationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
