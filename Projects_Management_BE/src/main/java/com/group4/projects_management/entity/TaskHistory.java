package com.group4.projects_management.entity; /***********************************************************************
 * Module:  TaskHistory.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskHistory
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/** @pdOid 67fc81ff-c267-46d7-9ed9-80bf169619ae */
@Entity
@Table(name = "TASK_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistory {
   /** @pdOid 39671e43-3521-4948-9f6d-68e1512b6258 */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "TASK_HISTORY_ID")
   private int id;
   /** @pdOid d7ec35ae-e900-40bb-9ef5-5a217e7d552d */
   @Column(name = "TASK_HISTORY_COLUMN_NAME", nullable = false, length = 50)
   private java.lang.String columnName;
   /** @pdOid 17fdd804-15a4-47a7-90a8-d641b881b02c */
   @Column(name = "TASK_HISTORY_OLD_VALUE", columnDefinition = "TEXT")
   private java.lang.String oldValue;
   /** @pdOid 6d4682f9-266e-4c75-bf9e-776e7246d7ab */
   @Column(name = "TASK_HISTORY_NEW_VALUE", nullable = false, columnDefinition = "TEXT")   private java.lang.String newValue;
   /** @pdOid 543f4f41-1b2e-428d-b132-57c71e75245e */
   @Column(name = "TASK_HISTORY_CHANGED_AT", nullable = false)
   private LocalDateTime changedAt;
   
   /** @pdRoleInfo migr=no name=ProjectMember assc=association16 mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "PROJECT_MEMBER_ID", nullable = false)
   @ToString.Exclude
   public ProjectMember changedBy;
   /** @pdRoleInfo migr=no name=Task assc=association17 mult=1..1 side=A */
   @ManyToOne
   @JoinColumn(name = "TASK_ID", nullable = false)
   @ToString.Exclude
   public Task task;
   

}