package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Notification.java
 * Author:  Lenovo
 * Purpose: Defines the Class Notification
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid 42dc7ab3-e537-47fa-95b1-2302a533ffab */
@Entity
@Table(name = "NOTIFICATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "NOTIFICATION_ID")
   private Long id;

   @Column(name = "NOTIFICATION_TEXT", nullable = false, columnDefinition = "TEXT")
   private java.lang.String text;

   @Column(name = "NOTIFICATION_TYPE", nullable = false, length = 50)
   private java.lang.String type;

   @Column(name = "NOTIFICATION_REFERENCE_ID")
   private java.lang.String referenceId;

   @Column(name = "NOTIFICATION_CREATED_AT", nullable = false)
   private LocalDateTime createAt;

   @PrePersist
   protected void onCreate() {
      this.createAt = LocalDateTime.now();
   }
}