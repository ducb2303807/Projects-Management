package com.group4.projects_management.repository; /***********************************************************************
 * Module:  AppRoleRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface AppRoleRepository
 ***********************************************************************/

import com.group4.projects_management.entity.AppRole;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.List;

/** @pdOid 1e7cb5ff-6f9f-4708-9b71-5ced7f07e71d */
public interface AppRoleRepository extends BaseRepository<AppRole, Long> {
    List<AppRole> getAppRoleByName(String name);

    AppRole getAppRoleBySystemCode(String systemCode);
}