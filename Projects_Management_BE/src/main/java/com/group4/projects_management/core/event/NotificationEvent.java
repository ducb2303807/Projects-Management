package com.group4.projects_management.core.event;

import com.group4.common.dto.NotificationDTO;

public record NotificationEvent(Long userId, NotificationDTO notificationDTO) {
}
