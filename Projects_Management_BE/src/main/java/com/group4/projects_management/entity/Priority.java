package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Priority.java
 * Author:  Lenovo
 * Purpose: Defines the Class Priority
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid a12694ce-5d2d-4c49-b377-338505aa6f9b */
@Entity
@Table(name = "PRIORITY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Priority extends BaseLookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRIORITY_ID")
    private Long id;

    @Column(name = "PRIORITY_NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "PRIORITY_DESCRIPTION")
    private String description;
}