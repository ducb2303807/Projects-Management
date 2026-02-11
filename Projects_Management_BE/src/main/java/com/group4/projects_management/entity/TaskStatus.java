package com.group4.projects_management.entity; /***********************************************************************
 * Module:  TaskStatus.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskStatus
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 2e260575-f590-4b9e-bd86-b0d2459e09d9 */
@Entity
@Table(name = "TASK_STATUS")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "TASK_STATUS_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "TASK_STATUS_NAME", nullable = false, length = 50)),
        @AttributeOverride(name = "description", column = @Column(name = "TASK_STATUS_DESCRIPTION"))
})
public class TaskStatus extends BaseLookup<Long> {
}