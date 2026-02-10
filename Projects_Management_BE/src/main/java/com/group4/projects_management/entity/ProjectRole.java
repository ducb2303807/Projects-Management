package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectRole.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectRole
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

/** @pdOid 235eeec6-021f-4ed1-bca5-d62e8563ef45 */
@Entity
@Table(name = "PROJECT_ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectRole extends BaseLookup {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "PROJECT_ROLE_ID")
   private Long id;

   @Column(name = "PROJECT_ROLE_NAME", nullable = false, length = 50)
   private String name;

   @Column(name = "PROJECT_ROLE_DESCRIPTION")
   private String description;

   @ManyToMany
   @JoinTable(
           name = "PROJECT_ROLE_PERMISSION",
           joinColumns = @JoinColumn(name = "PROJECT_ROLE_ID"),
           inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID")
   )
   @ToString.Exclude
   private Collection<Permission> permissions;
}