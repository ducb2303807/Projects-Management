package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Notification.java
 * Author:  Lenovo
 * Purpose: Defines the Class Notification
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** @pdOid 42dc7ab3-e537-47fa-95b1-2302a533ffab */
@Entity
@Table(name = "NOTIFICATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
   /** @pdOid 88036377-1337-4dad-9274-e57c4168187d */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "NOTIFICATION_ID")
   private Long id;
   /** @pdOid f0b9c1c7-73b8-4563-b5b2-1b489f616e78 */
   @Column(name = "NOTIFICATION_TEXT", nullable = false, columnDefinition = "TEXT")
   private java.lang.String text;
   /** @pdOid d823306f-4a5c-433d-b553-62ff8d791069 */
   @Column(name = "NOTIFICATION_TYPE", nullable = false, length = 50)
   private java.lang.String type;
   /** @pdOid 6cc7116a-aa34-4702-bedc-04a423dae70e */
   @Column(name = "NOTIFICATION_REFERENCE_ID")
   private java.lang.String referenceId;
   /** @pdOid 4091409f-ea95-4ab7-b37d-a36966cb28da */
   @CreationTimestamp
   @Column(name = "NOTIFICATION_CREATED_AT", nullable = false)
   private LocalDateTime createAt;

}