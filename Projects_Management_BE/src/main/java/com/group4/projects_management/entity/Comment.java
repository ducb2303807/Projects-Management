package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Comment.java
 * Author:  Lenovo
 * Purpose: Defines the Class Comment
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/** @pdOid 0e883aa9-0aed-4d40-b934-fa3a37b93713 */
@Entity
@Table(name = "TASK_COMMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "TASK_COMMENT_ID")
   private Long id;

   @Column(name = "TASK_COMMENT_TEXT")
   private java.lang.String content;

   @Column(name = "TASK_COMMENT_CREATED_AT")
   private LocalDateTime createAt;
   
   @ManyToOne
   @JoinColumn(name = "PARENT_ID")
   @ToString.Exclude
   private Comment parent;
   /** @pdRoleInfo migr=no name=ProjectMember assc=association15 mult=1..1 side=A */
   @ManyToOne
   @JoinColumn(name = "PROJECT_MEMBER_ID")
   @ToString.Exclude
   public ProjectMember member;
   /** @pdRoleInfo migr=no name=Task assc=association21 mult=1..1 side=A */
   @ManyToOne
   @JoinColumn(name = "TASK_ID")
   @ToString.Exclude
   public Task task;
   
   /** @pdOid 56e051eb-9d50-4eff-a19d-b65586bb8caa */
   public boolean isReply() {
     return this.parent != null;
   }
}