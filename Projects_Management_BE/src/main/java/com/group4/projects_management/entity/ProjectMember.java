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
@Table(name = "PROJECT_MEMBER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
   /** @pdOid 9aaaddb9-1a6f-4acc-8946-b43e089e6573 */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "PROJECT_MEMBER_ID")
   private Long id;
   /** @pdOid b74414a0-0f73-4748-8495-71b9ac5c46e7 */
   @Column(name = "PROJECT_MEMBER_JOIN_AT", nullable = false)
   private LocalDateTime joinAt;
   /** @pdOid 504436bf-88e0-4370-ac97-2784889f884a */
   @Column(name = "PROJECT_MEMBER_LEFT_AT")
   private LocalDateTime leftAt;
   
   /** @pdRoleInfo migr=no name=ProjectMember assc=invitedBy mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "PROJECT_MEMBER_INVITE_ID") // Tự tham chiếu để biết ai mời
   @ToString.Exclude
   public ProjectMember invitedBy;
   /** @pdRoleInfo migr=no name=ProjectMemberStatus assc=association10 mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "PROJECT_MEMBER_STATUS_ID", nullable = false)
   @ToString.Exclude
   public ProjectMemberStatus projectMemberStatus;
   /** @pdRoleInfo migr=no name=User assc=association11 mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "USER_ID", nullable = false)
   @ToString.Exclude
   public User user;
   /** @pdRoleInfo migr=no name=ProjectRole assc=association12 mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "PROJECT_ROLE_ID", nullable = false)
   @ToString.Exclude
   public ProjectRole projectRole;
   /** @pdRoleInfo migr=no name=Project assc=association9 mult=1..1 side=A */
   @ManyToOne
   @JoinColumn(name = "PROJECT_ID", nullable = false)
   @ToString.Exclude
   public Project project;
}