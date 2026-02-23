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
   /** @pdOid 6c1a81fc-c4b5-484d-a147-9edb08299b38 */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "TASK_COMMENT_ID")
   private Long id;
   /** @pdOid 2aced8d2-3b70-4e07-9a2c-525d62149481 */
   @Column(columnDefinition = "TASK_COMMENT_TEXT")
   private java.lang.String content;
   /** @pdOid 175834a9-661f-4678-bfec-a470c2369433 */
   @Column(name = "TASK_COMMENT_CREATED_AT")
   private LocalDateTime createAt;
   
   /** @pdRoleInfo migr=no name=Comment assc=association14 mult=0..1 */
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
      // TODO: implement
      return false;
   }
   
   /** @pdOid 0f982c7d-02f0-467a-ba54-0376bedb6322 */
   public int getReplyCount() {
      // TODO: implement
      return 0;
   }
}