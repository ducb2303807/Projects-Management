package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectRole.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectRole
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;

/** @pdOid 235eeec6-021f-4ed1-bca5-d62e8563ef45 */
@Entity
@Table(name = "PROJECT_ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PROJECT_ROLE_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "SYSTEM_NAME", length = 50, nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "SYSTEM_DESCRIPTION")),
        @AttributeOverride(name = "systemCode", column = @Column(name = "SYSTEM_CODE"))
})
public class ProjectRole extends BaseLookup<Long> {

   @ManyToMany
   @JoinTable(
           name = "PROJECT_ROLE_PERMISSION",
           joinColumns = @JoinColumn(name = "PROJECT_ROLE_ID"),
           inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID")
   )
   @ToString.Exclude
   private Collection<Permission> permissions = new HashSet<>();
}