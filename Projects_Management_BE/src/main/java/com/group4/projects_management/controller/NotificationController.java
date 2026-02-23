package com.group4.projects_management.controller; /***********************************************************************
 * Module:  NotificationController.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationController
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** @pdOid 0524bd5c-e73c-4795-8831-2f4775fd28ea */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
   /** @pdRoleInfo migr=no name=NotificationService assc=association34 mult=1..1 */

   @Autowired
   private NotificationService notificationService;

   @GetMapping("/user/{userId}")
   public ResponseEntity<List<NotificationDTO>> getNotificationsForUser(@PathVariable Long userId) {
      return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
   }

   @PatchMapping("/{notificationId}/read")
   public ResponseEntity<Void> makeAsRead(@PathVariable Long notificationId) {
      var userId = SecurityUtils.getCurrentUserId();
      notificationService.markAsRead(notificationId, userId);
      return ResponseEntity.ok().build();
   }

}