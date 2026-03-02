package com.group4.projects_management.entity; /***********************************************************************
 * Module:  UserNotification.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserNotification
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
class UserNotificationId implements Serializable {
   private Long user;
   private Long notification;
}

@Entity
@Table(name = "USER_NOTIFICATION")
@IdClass(UserNotificationId.class) //khóa chính tổng hợp
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotification {
   @Column(name = "USER_NOTIFICATION_IS_READ")
   private boolean isRead;
   @Column(name = "USER_NOTIFICATION_READ_AT")
   private LocalDateTime readAt;

   @Id
   @ManyToOne
   @JoinColumn(name = "NOTIFICATION_ID")
   @ToString.Exclude
   private Notification notification;

   @Id
   @ManyToOne
   @JoinColumn(name = "USER_ID")
   @ToString.Exclude
   private User user;
   
   public void markAsRead() {
      if (!this.isRead) {
         this.isRead = true;
         this.readAt = LocalDateTime.now();
      }
   }

   public void markAsUnread() {
      if (this.isRead) {
         this.isRead = false;
         this.readAt = null;
      }
   }
}