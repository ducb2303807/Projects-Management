package com.group4.projects_management.entity; /**********************************************************************
 * Module:  Permission.java
 * Author:  Lenovo
 * Purpose: Defines the Class Permission
 ***********************************************************************/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** @pdOid 8106c937-218e-4d41-9ac6-573c167c15c2 */
@Entity
@Table(name = "PERMISSION")
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PERMISSION_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "SYSTEM_NAME", length = 50, nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "SYSTEM_DESCRIPTION")),
        @AttributeOverride(name = "systemCode", column = @Column(name = "SYSTEM_CODE"))

})
public class Permission extends BaseLookup<Long> {
}