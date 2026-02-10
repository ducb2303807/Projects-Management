package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Permission.java
 * Author:  Lenovo
 * Purpose: Defines the Class Permission
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 8106c937-218e-4d41-9ac6-573c167c15c2 */
@Entity
@Table(name = "PERMISSION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseLookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERMISSION_ID")
    private Long id;

    @Column(name = "PERMISSION_CODE")
    private String code;

    @Column(name = "PERMISSION_DESCRIPTION")
    private String description;
}