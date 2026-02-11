package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectStatus.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectStatus
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 68b1a1a6-3731-4e7e-8e7b-4f7ad3331ca7 */
@Entity
@Table(name = "PROJECT_STATUS")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PROJECT_STATUS_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "PROJECT_STATUS_NAME", length = 50, nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "PROJECT_STATUS_DESCRIPTION"))
})
public class ProjectStatus extends BaseLookup<Long> {}