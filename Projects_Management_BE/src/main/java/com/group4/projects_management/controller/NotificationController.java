package com.group4.projects_management.controller; /***********************************************************************
 * Module:  NotificationController.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationController
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** @pdOid 0524bd5c-e73c-4795-8831-2f4775fd28ea */
@RestController
public class NotificationController {
   /** @pdRoleInfo migr=no name=NotificationService assc=association34 mult=1..1 */

   @Autowired
   private NotificationService notificationService;
   
   /** @param userId
    * @pdOid 622f58fb-2042-4293-9e17-a52dd8c64622 */
   public ResponseEntity<List<NotificationDTO>> getNotificationsForUser(Long userId) {
      // TODO: implement
      return null;
   }
   
   /** @param notificationId
    * @pdOid cb03db1a-e4e6-4741-9111-4278a0c1955f */
   public ResponseEntity<Void> makeAsRead(Long notificationId) {
      // TODO: implement
      return null;
   }

}