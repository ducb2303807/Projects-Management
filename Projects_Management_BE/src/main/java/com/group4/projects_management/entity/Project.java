package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Project.java
 * Author:  Lenovo
 * Purpose: Defines the Class Project
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** @pdOid ed3a2148-7b7a-4a09-a24a-3df8fa0f223c */
@Entity
@Table(name = "PROJECT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
   /** @pdOid 2e5266d6-45b5-4a43-a120-7bad5afafc61 */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "PROJECT_ID")
   private Long id;
   /** @pdOid 6a8f736a-e531-411a-85b0-fc90c9a9a200 */
   @Column(name = "PROJECT_NAME", nullable = false, length = 100)
   private java.lang.String name;
   /** @pdOid 47a33cc6-e5f2-4fc6-ab63-7c393e136bc2 */
   @Column(name = "PROJECT_START_AT", nullable = false)
   private LocalDateTime startDate;
   /** @pdOid bb4ff680-204d-4fc8-96e7-328c7c480196 */
   @Column(name = "PROJECT_END_AT", nullable = false)
   private LocalDateTime endDate;
   /** @pdOid caa9d682-4fe2-4b7e-bfdc-8623ccb44a23 */
   @Column(name = "PROJECT_DESCRIPTION", columnDefinition = "TEXT")
   private java.lang.String description;
   /** @pdOid 95b2195b-2b45-480d-b99f-8d63211367b4 */
   @Column(name = "PROJECT_CREATED_AT", nullable = false)
   private LocalDateTime createdAt;
   
   /** @pdRoleInfo migr=no name=Task assc=association6 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   @OneToMany(mappedBy = "project") // "project" là tên biến bạn đặt trong class ProjectMember
   @ToString.Exclude
   private java.util.Collection<Task> tasks = new HashSet<>();
   /** @pdRoleInfo migr=no name=ProjectMember assc=association9 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   @OneToMany(mappedBy = "project") // "project" là tên biến bạn đặt trong class ProjectMember
   @ToString.Exclude
   private java.util.Collection<ProjectMember> members = new HashSet<>();
   /** @pdRoleInfo migr=no name=ProjectStatus assc=hasStatus mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "PROJECT_STATUS_ID", nullable = false)
   @ToString.Exclude
   private ProjectStatus projectStatus;
   /** @pdRoleInfo migr=no name=User assc=association7 mult=1..1 side=A */
   @ManyToOne
   @JoinColumn(name = "createdBy", nullable = false)
   @ToString.Exclude
   private User createdBy;
   @PrePersist
   public void createAt() {
      this.createdAt = LocalDateTime.now();
   }
   /** @pdOid 10e72771-2811-47b2-819e-f417901cb177 */
   public void removeMember(ProjectMember member) {
      if (member == null) return;
      if (!members.contains(member)) {
         throw new IllegalArgumentException("Member not in this project");
      }
      member.leave();
   }
   /** @pdOid 2d4c02bc-cfb1-4c94-b105-182986cb7917 */
   public double calculateProgress() {
      if (tasks == null || tasks.isEmpty()) {
         return 0;
      }
      long completed = tasks.stream()
              .filter(Task::isCompleted)
              .count();
      return (completed * 100.0) / tasks.size();
   }
   /** @pdOid 7082dcab-7aa6-4835-a3d3-c5cd032f1204 */
   public boolean isOverdue() {
      if (isCompleted()) {
         return false;
      }
      return LocalDateTime.now().isAfter(endDate);
   }
   
   /** @pdOid c7a0a817-13e6-4aa8-aec5-ce6fbc2b8324 */
   public int getMemberCount() {
      if (members == null) return 0;
      int count = 0;
      for (ProjectMember member : members) {
         if (member.isActive()) {
            count++;
         }
      }
      return count;
   }
   
   /** @param userId
    * @pdOid 5a758d3d-68ae-4fa1-ab98-16222a790e83 */
   public boolean hasMember(Long userId) {
      if (members == null) return false;
      return members.stream()
              .filter(ProjectMember::isActive)
              .anyMatch(m -> m.getUser().getId().equals(userId));
   }
   /** @pdOid 655e0174-bd56-427e-9cf9-42dc87cddaf8 */
   public List<ProjectMember> getActiveMembers() {
      List<ProjectMember> result = new ArrayList<>();
      for (ProjectMember m : members) {
         if (m.isActive()) {
            result.add(m);
         }
      }
      return result;
   }
   /** @pdOid 6043c10b-7fc8-4ffe-9204-c6664cda21d3 */
   public boolean isCompleted() {
      return false;
   }
}