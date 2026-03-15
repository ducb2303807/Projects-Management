package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectMemberStatus.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectMemberStatus
 **********************************************************************/

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 498bc4f0-6641-425b-81bb-463f55dda0dd */

@Entity
@Table(name = "PROJECT_MEMBER_STATUS")
@Data
@NoArgsConstructor

@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "PROJECT_MEMBER_STATUS_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "PROJECT_MEMBER_STATUS_NAME", length = 50, nullable = false)),
        @AttributeOverride(name = "description", column = @Column(name = "PROJECT_MEMBER_STATUS_DESCRIPTION"))
})
@EqualsAndHashCode(callSuper = true)
public class ProjectMemberStatus extends BaseLookup<Long> {
}