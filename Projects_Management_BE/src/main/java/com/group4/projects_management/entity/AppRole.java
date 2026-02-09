package com.group4.projects_management.entity; /***********************************************************************
 * Module:  AppRole.java
 * Author:  Lenovo
 * Purpose: Defines the Class AppRole
 ***********************************************************************/

import lombok.Data;

/** @pdOid 5413a20b-ef1b-40b4-806e-52d827877aea */
@Data
public class AppRole extends BaseLookup {
   /** @pdRoleInfo migr=no name=Permission assc=association5 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Aggregation */
   public java.util.Collection<Permission> permissions;
}