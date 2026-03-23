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

   @ManyToOne
   @JoinColumn(name = "PROJECT_MEMBER_ID")
   @ToString.Exclude
   public ProjectMember member;

   @ManyToOne
   @JoinColumn(name = "TASK_ID")
   @ToString.Exclude
   public Task task;

   public boolean isReply() {
     return this.parent != null;
   }
}