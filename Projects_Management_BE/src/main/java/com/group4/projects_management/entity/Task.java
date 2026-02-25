package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Task.java
 * Author:  Lenovo
 * Purpose: Defines the Class Task
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/** @pdOid f521481c-a646-4bde-bb82-baf28d561b0f */
@Entity
@Table(name = "TASK")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
   /** @pdOid c19f764e-b01e-4035-b8e4-fc859bd0c93b */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "TASK_ID")
   private Long id;
   /** @pdOid 36d9b294-70bd-4088-85c9-9387cdfe5c18 */
   @Column(name = "TASK_NAME", nullable = false, length = 50)
   private java.lang.String name;
   /** @pdOid 25c2d70b-15dc-44c0-b482-ae4fe639c31a */
   @Column(name = "TASK_DESCRIPTION", columnDefinition = "TEXT")
   private java.lang.String description;
   /** @pdOid 5ed148b6-5071-447e-8a13-e7f6c42e5bfd */
   @Column(name = "TASK_DEADLINE")
   private LocalDateTime deadline;

   @Column(name = "TASK_CREATED_AT", nullable = false)
   private LocalDateTime createdAt;

   @OneToMany(mappedBy = "task")
   @ToString.Exclude
   public java.util.Collection<TaskAssignment> assignments;

   @ManyToOne
   @JoinColumn(name = "PRIORITY_ID", nullable = false)
   @ToString.Exclude
   public Priority priority;

   @OneToMany(mappedBy = "task")
   @ToString.Exclude
   public java.util.Collection<TaskHistory> historys;

   @ManyToOne
   @JoinColumn(name = "TASK_STATUS_ID", nullable = false)
   @ToString.Exclude
   public TaskStatus taskStatus;

   @OneToMany(mappedBy = "task")
   @ToString.Exclude
   public java.util.Collection<Comment> comments;

   @ManyToOne
   @JoinColumn(name = "PROJECT_ID", nullable = false)
   @ToString.Exclude
   public Project project;

   public boolean isOverdue() {
      if (this.taskStatus != null
              && "COMPLETED".equalsIgnoreCase(this.taskStatus.getSystemCode()))
         return false;

      if (this.deadline == null) return false;
      return this.deadline.isBefore(LocalDateTime.now());
   }

   public int getMemberCount() {
      if (assignments == null) return 0;
      int count = 0;
      for (var assignment : assignments) {
         if (assignment.getAssignee().isActive()) count++;
      }
      return count;
   }

   public boolean isUrgent() {
     return this.priority.getSystemCode().equals("Urgent");
   }

   public long getRemainingDays() {
      return deadline.until(LocalDateTime.now(), java.time.temporal.ChronoUnit.DAYS);
   }


   public void addAssignment(ProjectMember assignee, ProjectMember assigner) {
      if (assignee == null || assigner == null) return;

      TaskAssignment assignment = new TaskAssignment();
      assignment.setAssignee(assignee);
      assignment.setAssigner(assigner);

      this.addAssignment(assignment);
   }

   public void addAssignment(TaskAssignment assignment) {
      if (assignment == null) return;

      assignment.setTask(this);

      if (assignment.getAssignAt() == null) {
         assignment.setAssignAt(LocalDateTime.now());
      }

      this.assignments.add(assignment);
   }

   public boolean canUpdateStatus(TaskStatus newStatus) {
      // TODO: implement
      return false;
   }

   public void addComment(Comment comment) {
      this.comments.add(comment);
   }

   public boolean isEmptyAssignment() {
      return this.assignments.isEmpty();
   }
}