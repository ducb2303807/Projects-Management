package com.group4.projects_management.repository; /***********************************************************************
 * Module:  ProjectRoleRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface ProjectRoleRepository
 ***********************************************************************/

import com.group4.projects_management.entity.ProjectRole;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.Optional;

/** @pdOid afa9a1ee-c34c-4680-b1bb-ee1bebda7721 */
public interface ProjectRoleRepository extends BaseRepository<ProjectRole, Long> {
    Optional<ProjectRole> findBySystemCode(String systemCode);
}