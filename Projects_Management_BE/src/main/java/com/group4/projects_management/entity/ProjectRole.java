package com.group4.projects_management.entity; /***********************************************************************
 * Module:  ProjectRole.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectRole
 ***********************************************************************/

import lombok.Data;

/** @pdOid 235eeec6-021f-4ed1-bca5-d62e8563ef45 */
@Data
public class ProjectRole extends BaseLookup {
   /** @pdRoleInfo migr=no name=Permission assc=rolePermission coll=java.util.Collection mult=0..* type=Aggregation */
   public java.util.Collection<Permission> permissions;

}