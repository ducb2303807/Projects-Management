package com.group4.projects_management.entity; /***********************************************************************
 * Module:  User.java
 * Author:  Lenovo
 * Purpose: Defines the Class User
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;

/** @pdOid 2ec3d48f-9f0b-4d44-91e9-a43da8d13c77 */
@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
   /** @pdOid 37b7dd80-8f41-477a-a853-42d9fb10ae40 */
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "USER_ID")
   private Long id;
   /** @pdOid 2d934aab-1e8e-4e2f-8f30-06ebca1a1c50 */
   @Column(name = "USER_USERNAME", nullable = false, length = 50)
   private java.lang.String username;
   /** @pdOid 791c9eef-6329-4983-ae73-e16790de86ba */
   @Column(name = "USER_PASSWORD_HASHED", nullable = false, columnDefinition = "TEXT")
   private java.lang.String hashedPassword;
   /** @pdOid 1a8c9487-6c10-404a-9292-0da9bf337c36 */
   @Column(name = "USER_NAME", nullable = false, length = 60)
   private java.lang.String fullName;
   /** @pdOid 3e14300c-621d-4e4d-b8cf-3b97fcc742e5 */
   @Column(name = "USER_EMAIL", nullable = false, columnDefinition = "TEXT")
   private java.lang.String email;
   /** @pdOid db79ecb1-3a84-462a-9151-aa2f93e0a25a */
   @Column(name = "USER_ADDRESS", columnDefinition = "TEXT")
   private java.lang.String address;
   /** @pdOid e6bd3c2a-d305-4d1c-9d32-c9950d4c0d02 */
   @Column(name = "USER_IS_ACTIVE")
   private boolean isActive;
   
   /** @pdRoleInfo migr=no name=AppRole assc=association3 mult=1..1 */
   @ManyToOne
   @JoinColumn(name = "SYSTEM_ROLE_ID")
   @ToString.Exclude
   private AppRole appRole;
   /** @pdRoleInfo migr=no name=Project assc=association7 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Aggregation */
   @OneToMany(mappedBy = "createdBy")
   @ToString.Exclude
   private java.util.Collection<Project> project = new HashSet<>();
   /** @pdRoleInfo migr=no name=UserNotification assc=association19 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Aggregation */
   @OneToMany(mappedBy = "user")
   @ToString.Exclude
   private java.util.Collection<UserNotification> userNotification = new HashSet<>();
   
   /** @param roleName
    * @pdOid 62fcfaba-7039-4108-a9ca-15704bd90f5c */
   public boolean hasRole(java.lang.String roleName) {
      if (this.appRole == null || roleName == null) {
         return false;
      }
      return roleName.equalsIgnoreCase(this.appRole.getName());
   }
}