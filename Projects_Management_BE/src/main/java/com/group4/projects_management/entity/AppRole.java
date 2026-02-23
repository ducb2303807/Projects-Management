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
@AttributeOverrides(value = {
        @AttributeOverride(name = "id", column = @Column(name = "APP_ROLE_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "SYSTEM_NAME", length = 50, nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "SYSTEM_DESCRIPTION")),
        @AttributeOverride(name = "systemCode", column = @Column(name = "SYSTEM_CODE"))
})
public class AppRole extends BaseLookup<Long> {

   @ManyToMany
   @JoinTable(
           name = "APP_ROLE_PERMISSION",
           joinColumns = @JoinColumn(name = "SYSTEM_ROLE_ID"),
           inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID")
   )
   @ToString.Exclude
   private java.util.Collection<Permission> permissions;
}