package com.group4.projects_management.controller; /***********************************************************************
 * Module:  NotificationController.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationController
 ***********************************************************************/

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
   @Autowired
   private NotificationService notificationService;

   @Operation(summary = "Lấy danh sách thông báo của user đang đăng nhập")
   @GetMapping("/me")
   public ResponseEntity<List<NotificationDTO>> getNotificationsForUser() {
      var userId = SecurityUtils.getCurrentUserId();
      return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
   }

   @PatchMapping("/{notificationId}/read")
   public ResponseEntity<Void> makeAsRead(@PathVariable Long notificationId) {
      var userId = SecurityUtils.getCurrentUserId();
      notificationService.markAsRead(notificationId, userId);
      return ResponseEntity.ok().build();
   }
}