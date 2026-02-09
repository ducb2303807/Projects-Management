package com.group4.projects_management.entity; /***********************************************************************
 * Module:  AppRole.java
 * Author:  Lenovo
 * Purpose: Defines the Class AppRole
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.*;

/** @pdOid 5413a20b-ef1b-40b4-806e-52d827877aea */
@Entity
@Table(name = "APP_ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppRole extends BaseLookup {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "SYSTEM_ROLE_ID") // Bắt buộc phải có dòng này
   private Long id;

   @Column(name = "SYSTEM_ROLE_NAME")
   private String name;

   @Column(name = "SYSTEM_ROLE_DESCRIPTION")
   private String description;

   @ManyToMany
   @JoinTable(
           name = "APP_ROLE_PERMISSION",
           joinColumns = @JoinColumn(name = "SYSTEM_ROLE_ID"),
           inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID")
   )
   @ToString.Exclude
   private java.util.Collection<Permission> permissions;
}