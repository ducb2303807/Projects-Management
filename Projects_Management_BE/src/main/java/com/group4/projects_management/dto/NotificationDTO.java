package com.group4.projects_management.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long notificationId;
    private String notificationContent;
    private LocalDateTime notificationCreatedAt;

    public NotificationDTO() {}

    public NotificationDTO(Long notificationId, String notificationContent, LocalDateTime notificationCreatedAt) {
        this.notificationId = notificationId;
        this.notificationContent = notificationContent;
        this.notificationCreatedAt = notificationCreatedAt;
    }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public String getNotificationContent() { return notificationContent; }
    public void setNotificationContent(String notificationContent) { this.notificationContent = notificationContent; }

    public LocalDateTime getNotificationCreatedAt() { return notificationCreatedAt; }
    public void setNotificationCreatedAt(LocalDateTime notificationCreatedAt) { this.notificationCreatedAt = notificationCreatedAt; }
}
