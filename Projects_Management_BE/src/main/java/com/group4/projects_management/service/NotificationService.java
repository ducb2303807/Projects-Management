package com.group4.projects_management.service; /***********************************************************************
 * Module:  NotificationService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface NotificationService
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;

import java.util.List;

/** @pdOid 349c8d00-d347-436d-bb9d-3c3d2472ba37 */
public interface NotificationService {
   /** @param userId
    * @pdOid 790acd64-c554-4b0d-bf0c-12f6051d7f75 */
   List<NotificationDTO> getNotificationsForUser(Long userId);
   /** @param notificationId 
    * @param userId
    * @pdOid 0a174c82-18a2-4360-b707-a09ae8f600b3 */
   void markAsRead(Long notificationId, Long userId);
   /** @param userId 
    * @param text 
    * @param type 
    * @param referenceId
    * @pdOid 891ba294-67d5-4673-9bfa-f2d473a10a3a */
   void sendNotification(Long userId, java.lang.String text, java.lang.String type, Long referenceId);
   /** @param usersId 
    * @param text 
    * @param type 
    * @param referenceId
    * @pdOid 919e0bc1-f2b2-465b-b299-f78408014f5b */
   void createNotification(List<Long> usersId, java.lang.String text, java.lang.String type, Long referenceId);

}