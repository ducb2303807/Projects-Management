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
   
   /** @pdRoleInfo migr=no name=Priority assc=association8 mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "PRIORITY_ID", nullable = false)
   @ToString.Exclude
   public Priority priority;
   /** @pdRoleInfo migr=no name=TaskHistory assc=association17 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   @OneToMany(mappedBy = "task")
   @ToString.Exclude
   public java.util.Collection<TaskHistory> historys;
   /** @pdRoleInfo migr=no name=TaskStatus assc=association20 mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "TASK_STATUS_ID", nullable = false)
   @ToString.Exclude
   public TaskStatus taskStatus;
   /** @pdRoleInfo migr=no name=Comment assc=association21 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   @OneToMany(mappedBy = "task")
   @ToString.Exclude
   public java.util.Collection<Comment> comments;
   /** @pdRoleInfo migr=no name=Project assc=association6 mult=1..1 side=A */
   @ManyToOne
   @JoinColumn(name = "PROJECT_ID", nullable = false)
   @ToString.Exclude
   public Project project;
   
   /** @pdOid 858eb2b9-cb60-4c67-8e3b-85ffe23871d0 */
   public boolean isOverdue() {
      // TODO: implement
      return false;
   }
   
   /** @pdOid 66c26773-7b21-443a-87c5-2a41db972b6f */
   public int getMemberCount() {
      // TODO: implement
      return 0;
   }
   
   /** @pdOid 8f5c84cb-6114-4c05-98c0-ef242a913a53 */
   public boolean isUrgent() {
      // TODO: implement
      return false;
   }
   
   /** @pdOid 7c19221e-df9e-48fc-bf15-7d61911428da */
   public long getRemainingDays() {
      // TODO: implement
      return 0;
   }
   
   /** @param projectMember
    * @pdOid 056f8fe5-2b76-4b09-8667-82e11f2dabf9 */
   public void addAssignee(ProjectMember projectMember) {
      // TODO: implement
   }
   
   /** @param newStatus
    * @pdOid 734b1e10-6754-46c4-b925-adb8cc118112 */
   public boolean canUpdateStatus(TaskStatus newStatus) {
      // TODO: implement
      return false;
   }
   
   /** @param comment
    * @pdOid bc0096f3-024b-4369-8db1-174e4f6c6c54 */
   public void addComment(Comment comment) {
      // TODO: implement
   }

   public boolean isEmpty() {
      return false;
   }
}