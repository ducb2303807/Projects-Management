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
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PERMISSION_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "PERMISSION_NAME", columnDefinition = "TEXT", nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "PERMISSION_DESCRIPTION"))
})
public class Permission extends BaseLookup<Long> {
    @Column(name = "PERMISSION_CODE", nullable = false, length = 50, unique = true)
    private String code;
}