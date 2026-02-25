package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectMember.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectMember
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/** @pdOid b611216c-16aa-4354-9881-f75a46baba4f */
@Entity
@Table(
        name = "PROJECT_MEMBER",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"PROJECT_ID", "USER_ID"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "PROJECT_MEMBER_ID")
   private Long id;

   @Column(name = "PROJECT_MEMBER_JOIN_AT", nullable = false)
   private LocalDateTime joinAt;

   @Column(name = "PROJECT_MEMBER_LEFT_AT")
   private LocalDateTime leftAt;
   
   @ManyToOne
   @JoinColumn(name = "PROJECT_MEMBER_INVITE_ID") // Tự tham chiếu để biết ai mời
   @ToString.Exclude
   private ProjectMember invitedBy;

   @ManyToOne
   @JoinColumn(name = "PROJECT_MEMBER_STATUS_ID", nullable = false)
   @ToString.Exclude
   private ProjectMemberStatus projectMemberStatus;

   @ManyToOne
   @JoinColumn(name = "USER_ID", nullable = false)
   @ToString.Exclude
   private User user;

   @ManyToOne
   @JoinColumn(name = "PROJECT_ROLE_ID", nullable = false)
   @ToString.Exclude
   private ProjectRole projectRole;

   @ManyToOne
   @JoinColumn(name = "PROJECT_ID", nullable = false)
   @ToString.Exclude
   private Project project;

   @PrePersist
   protected void onJoin() {
      this.joinAt = LocalDateTime.now();
      this.leftAt = null;
   }

   public void leave() {
      if (this.leftAt == null) {
         this.leftAt = LocalDateTime.now();
      }
   }

   public boolean isActive() {
      return this.leftAt == null;
   }

   public boolean hasRoleId(Long roleId) {
      return this.projectRole != null
              && roleId != null
              && roleId.equals(this.projectRole.getId());
   }

   public boolean hasRoleCode(String roleName) {
      return this.projectRole != null
              && roleName != null
              && roleName.equalsIgnoreCase(this.projectRole.getName());
   }
}