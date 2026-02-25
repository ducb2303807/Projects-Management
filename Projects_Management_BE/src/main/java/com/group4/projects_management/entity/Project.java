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
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "PROJECT_ID")
   private Long id;

   @Column(name = "PROJECT_NAME", nullable = false, length = 100)
   private java.lang.String name;

   @Column(name = "PROJECT_START_AT", nullable = false)
   private LocalDateTime startDate;

   @Column(name = "PROJECT_END_AT", nullable = false)
   private LocalDateTime endDate;

   @Column(name = "PROJECT_DESCRIPTION", columnDefinition = "TEXT")
   private java.lang.String description;

   @Column(name = "PROJECT_CREATED_AT", nullable = false)
   private LocalDateTime createdAt;
   
   @OneToMany(mappedBy = "project")
   @ToString.Exclude
   private java.util.Collection<Task> tasks = new HashSet<>();

   @OneToMany(mappedBy = "project")
   @ToString.Exclude
   private java.util.Collection<ProjectMember> members = new HashSet<>();

   @ManyToOne
   @JoinColumn(name = "PROJECT_STATUS_ID", nullable = false)
   @ToString.Exclude
   private ProjectStatus projectStatus;


   @ManyToOne
   @JoinColumn(name = "createdBy", nullable = false)
   @ToString.Exclude
   private User createdBy;

   @PrePersist
   public void createAt() {
      this.createdAt = LocalDateTime.now();
   }

   public void removeMember(ProjectMember member) {
      if (member == null) return;
      if (!members.contains(member)) {
         throw new IllegalArgumentException("Member not in this project");
      }
      member.leave();
   }

   public double calculateProgress() {
      if (tasks == null || tasks.isEmpty()) {
         return 0;
      }
      long completed = tasks.stream()
              // TODO
              .filter(x -> true)
              .count();
      return (completed * 100.0) / tasks.size();
   }

   public boolean isOverdue() {
      if (isCompleted()) {
         return false;
      }
      return LocalDateTime.now().isAfter(endDate);
   }
   
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
   

   public boolean hasMember(Long userId) {
      if (members == null) return false;
      return members.stream()
              .filter(ProjectMember::isActive)
              .anyMatch(m -> m.getUser().getId().equals(userId));
   }

   public List<ProjectMember> getActiveMembers() {
      List<ProjectMember> result = new ArrayList<>();
      for (ProjectMember m : members) {
         if (m.isActive()) {
            result.add(m);
         }
      }
      return result;
   }

   public boolean isCompleted() {
       return this.projectStatus != null && "COMPLETED".equalsIgnoreCase(this.projectStatus.getSystemCode());
   }
}