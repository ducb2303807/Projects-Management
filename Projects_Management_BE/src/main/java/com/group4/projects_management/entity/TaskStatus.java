package com.group4.projects_management.entity; /***********************************************************************
 * Module:  TaskStatus.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskStatus
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 2e260575-f590-4b9e-bd86-b0d2459e09d9 */
@Entity
@Table(name = "TASK_STATUS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskStatus extends BaseLookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_STATUS_ID")
    private Long id;

    @Column(name = "TASK_STATUS_NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "TASK_STATUS_DESCRIPTION")
    private String description;
}