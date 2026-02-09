package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Project.java
 * Author:  Lenovo
 * Purpose: Defines the Class Project
 ***********************************************************************/

import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

/** @pdOid ed3a2148-7b7a-4a09-a24a-3df8fa0f223c */
@Data
public class Project {
   /** @pdOid 2e5266d6-45b5-4a43-a120-7bad5afafc61 */
   private Long id;
   /** @pdOid 6a8f736a-e531-411a-85b0-fc90c9a9a200 */
   private java.lang.String name;
   /** @pdOid 47a33cc6-e5f2-4fc6-ab63-7c393e136bc2 */
   private LocalDateTime startDate;
   /** @pdOid bb4ff680-204d-4fc8-96e7-328c7c480196 */
   private LocalDateTime endDate;
   /** @pdOid caa9d682-4fe2-4b7e-bfdc-8623ccb44a23 */
   private java.lang.String description;
   /** @pdOid 95b2195b-2b45-480d-b99f-8d63211367b4 */
   private LocalDateTime createAt;
   
   /** @pdRoleInfo migr=no name=Task assc=association6 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   public java.util.Collection<Task> tasks;
   /** @pdRoleInfo migr=no name=ProjectMember assc=association9 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   public java.util.Collection<ProjectMember> members;
   /** @pdRoleInfo migr=no name=ProjectStatus assc=hasStatus mult=1..1 */
   public ProjectStatus projectStatus;
   /** @pdRoleInfo migr=no name=User assc=association7 mult=1..1 side=A */
   public User createdBy;
   
   /** @pdOid 10e72771-2811-47b2-819e-f417901cb177 */
   public void removeMember() {
      // TODO: implement
   }
   
   /** @pdOid 2d4c02bc-cfb1-4c94-b105-182986cb7917 */
   public double calculateProgress() {
      // TODO: implement
      return 0;
   }
   
   /** @pdOid 7082dcab-7aa6-4835-a3d3-c5cd032f1204 */
   public boolean isOverdue() {
      // TODO: implement
      return false;
   }
   
   /** @pdOid c7a0a817-13e6-4aa8-aec5-ce6fbc2b8324 */
   public int getMemberCount() {
      // TODO: implement
      return 0;
   }
   
   /** @param userId
    * @pdOid 5a758d3d-68ae-4fa1-ab98-16222a790e83 */
   public boolean hasMember(Long userId) {
      // TODO: implement
      return false;
   }
   
   /** @pdOid 655e0174-bd56-427e-9cf9-42dc87cddaf8 */
   public List<ProjectMember> getActiveMembers() {
      // TODO: implement
      return null;
   }
   
   /** @pdOid 6043c10b-7fc8-4ffe-9204-c6664cda21d3 */
   public boolean isCompleted() {
      // TODO: implement
      return false;
   }
}