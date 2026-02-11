package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectMemberStatus.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectMemberStatus
 **********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 498bc4f0-6641-425b-81bb-463f55dda0dd */

@Entity
@Table(name = "PROJECT_MEMBER_STATUS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectMemberStatus extends BaseLookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_MEMBER_STATUS_ID")
    private Long id;

    @Column(name = "PROJECT_MEMBER_STATUS_NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "PROJECT_MEMBER_STATUS_DESCRIPTION")
    private String description;
}