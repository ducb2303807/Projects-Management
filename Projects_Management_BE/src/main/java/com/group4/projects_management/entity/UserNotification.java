package com.group4.projects_management.entity; /***********************************************************************
 * Module:  UserNotification.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserNotification
 ***********************************************************************/

import java.time.LocalDateTime;

/** @pdOid a9899dc6-a56b-4a63-a061-00a9467b3e53 */
public class UserNotification {
   /** @pdOid 74b7a3f3-83cf-46d7-ad8e-6ebb9ed52f65 */
   private boolean isRead;
   /** @pdOid a74babdb-b594-4736-8c20-be06e62146e8 */
   private LocalDateTime readAt;
   
   /** @pdRoleInfo migr=no name=Notification assc=association18 mult=1..1 side=A */
   public Notification notification;
   /** @pdRoleInfo migr=no name=User assc=association19 mult=1..1 side=A */
   public User user;
   
   /** @pdOid 0be8d923-8d3f-4a04-895b-da880708a5cd */
   public void markAsRead() {
      // TODO: implement
   }
}