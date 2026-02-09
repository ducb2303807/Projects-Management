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

/** @pdOid a9899dc6-a56b-4a63-a061-00a9467b3e53 */
@Entity
@Table(name = "USER_NOTIFICATION")
@IdClass(UserNotificationId.class) //khóa chính tổng hợp
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotification {
   /** @pdOid 74b7a3f3-83cf-46d7-ad8e-6ebb9ed52f65 */
   @Column(name = "USER_NOTIFICATION_IS_READ")
   private boolean isRead;
   /** @pdOid a74babdb-b594-4736-8c20-be06e62146e8 */
   @Column(name = "USER_NOTIFICATION_READ_AT")
   private LocalDateTime readAt;
   
   /** @pdRoleInfo migr=no name=Notification assc=association18 mult=1..1 side=A */
   @Id
   @ManyToOne
   @JoinColumn(name = "NOTIFICATION_ID")
   @ToString.Exclude
   public Notification notification;
   /** @pdRoleInfo migr=no name=User assc=association19 mult=1..1 side=A */
   @Id
   @ManyToOne
   @JoinColumn(name = "USER_ID")
   @ToString.Exclude
   public User user;
   
   /** @pdOid 0be8d923-8d3f-4a04-895b-da880708a5cd */
   public void markAsRead() {
      // TODO: implement
   }
}