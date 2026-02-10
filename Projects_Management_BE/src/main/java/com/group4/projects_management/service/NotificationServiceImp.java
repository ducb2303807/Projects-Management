package com.group4.projects_management.service; /***********************************************************************
 * Module:  NotificationServiceImp.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationServiceImp
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.entity.Notification;
import com.group4.projects_management.repository.NotificationRepository;
import com.group4.projects_management.repository.UserNotificationRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/** @pdOid e0710e36-f81b-4af0-9747-40513bf2c38e */
@Service
public class NotificationServiceImp extends BaseServiceImpl<Notification,Long> implements NotificationService {
   /** @pdRoleInfo migr=no name=NotificationRepository assc=association29 mult=1..1 */
   private final NotificationRepository notificationRepository;
   /** @pdRoleInfo migr=no name=UserNotificationRepository assc=association30 mult=1..1 */
   private final UserNotificationRepository userNotificationRepository;

   public NotificationServiceImp(NotificationRepository notificationRepository, UserNotificationRepository userNotificationRepository) {
      super(notificationRepository);
      this.notificationRepository = notificationRepository;
      this.userNotificationRepository = userNotificationRepository;
   }

   @Override
   public List<NotificationDTO> getNotificationsForUser(Long userId) {
      return List.of();
   }

   @Override
   public void markAsRead(Long notificationId, Long userId) {

   }

   @Override
   public void sendNotification(Long userId, String text, String type, Long referenceId) {

   }

   @Override
   public void createNotification(List<Long> usersId, String text, String type, Long referenceId) {

   }
}