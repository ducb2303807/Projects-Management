package com.group4.projects_management.entity; /***********************************************************************
 * Module:  Priority.java
 * Author:  Lenovo
 * Purpose: Defines the Class Priority
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid a12694ce-5d2d-4c49-b377-338505aa6f9b */
@Entity
@Table(name = "PRIORITY")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PRIORITY_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "SYSTEM_NAME", length = 50, nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "SYSTEM_DESCRIPTION")),
        @AttributeOverride(name = "systemCode", column = @Column(name = "SYSTEM_CODE",  nullable = false))

})
public class Priority extends BaseLookup<Long> {
}