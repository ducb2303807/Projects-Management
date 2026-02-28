package com.group4.projects_management.service; /***********************************************************************
 * Module:  NotificationServiceImp.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationServiceImp
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.entity.Notification;
import com.group4.projects_management.mapper.UserNotificationMapper;
import com.group4.projects_management.repository.NotificationRepository;
import com.group4.projects_management.repository.UserNotificationRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/** @pdOid e0710e36-f81b-4af0-9747-40513bf2c38e */
@Service
public class NotificationServiceImp extends BaseServiceImpl<Notification,Long> implements NotificationService {
   private final NotificationRepository notificationRepository;
   private final UserNotificationRepository userNotificationRepository;
   private final UserNotificationMapper userNotificationMapper;

   public NotificationServiceImp(NotificationRepository notificationRepository, UserNotificationRepository userNotificationRepository, UserNotificationMapper userNotificationMapper) {
      super(notificationRepository);
      this.notificationRepository = notificationRepository;
      this.userNotificationRepository = userNotificationRepository;
       this.userNotificationMapper = userNotificationMapper;
   }

   @Override
   public List<NotificationDTO> getNotificationsForUser(Long userId) {
      var notifications = this.userNotificationRepository
              .findAllByUserIdWithNotification(userId);

      return notifications
              .stream()
              .map(this.userNotificationMapper::toDto)
              .toList();
   }

   @Override
   public void markAsRead(Long notificationId, Long userId) {
      var notification = this.userNotificationRepository.findByUser_IdAndNotification_Id(userId, notificationId)
              .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

      notification.markAsRead();

      this.userNotificationRepository.save(notification);
   }

   @Override
   public void sendNotification(Long userId, String text, String type, Long referenceId) {

   }

   @Override
   public void createNotification(List<Long> usersId, String text, String type, Long referenceId) {

   }
}