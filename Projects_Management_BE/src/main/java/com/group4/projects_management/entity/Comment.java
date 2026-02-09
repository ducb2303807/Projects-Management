package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Comment.java
 * Author:  Lenovo
 * Purpose: Defines the Class Comment
 ***********************************************************************/

import lombok.Data;

import java.time.LocalDateTime;

/** @pdOid 0e883aa9-0aed-4d40-b934-fa3a37b93713 */
@Data
public class Comment {
   /** @pdOid 6c1a81fc-c4b5-484d-a147-9edb08299b38 */
   private Long id;
   /** @pdOid 2aced8d2-3b70-4e07-9a2c-525d62149481 */
   private java.lang.String content;
   /** @pdOid 175834a9-661f-4678-bfec-a470c2369433 */
   private LocalDateTime createAt;
   
   /** @pdRoleInfo migr=no name=Comment assc=association14 mult=0..1 */
   public Comment parent;
   /** @pdRoleInfo migr=no name=ProjectMember assc=association15 mult=1..1 side=A */
   public ProjectMember member;
   /** @pdRoleInfo migr=no name=Task assc=association21 mult=1..1 side=A */
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