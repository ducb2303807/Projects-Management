package com.group4.projects_management.service; /***********************************************************************
 * Module:  NotificationService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface NotificationService
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;

import java.util.List;


public interface NotificationService {
    List<NotificationDTO> getNotificationsForUser(Long userId);

    int countUnreadNotifications(Long userId);

    void markAllAsRead(Long userId);

    void markAsRead(Long notificationId, Long userId);

    <T> void send(Long receiverId, T contextData, Long referenceId);

    <T> void send(List<Long> receiverIds, T contextData, Long referenceId);
}