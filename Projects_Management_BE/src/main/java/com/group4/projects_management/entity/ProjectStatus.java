package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectStatus.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectStatus
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 68b1a1a6-3731-4e7e-8e7b-4f7ad3331ca7 */
@Entity
@Table(name = "PROJECT_STATUS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectStatus extends BaseLookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_STATUS_ID")
    private Long id;

    @Column(name = "PROJECT_STATUS_NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "PROJECT_STATUS_DESCRIPTION")
    private String description;
}