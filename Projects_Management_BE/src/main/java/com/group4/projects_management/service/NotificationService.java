package com.group4.projects_management.service; /***********************************************************************
 * Module:  NotificationService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface NotificationService
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;

import java.util.List;


public interface NotificationService {

   List<NotificationDTO> getNotificationsForUser(Long userId);

   void markAsRead(Long notificationId, Long userId);

   void sendNotification(Long userId, java.lang.String text, java.lang.String type, Long referenceId);

   void sendNotification(List<Long> usersId, java.lang.String text, java.lang.String type, Long referenceId);

}