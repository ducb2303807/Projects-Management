package com.group4.projects_management.repository; /***********************************************************************
 * Module:  ProjectStatusRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface ProjectStatusRepository
 ***********************************************************************/

import com.group4.projects_management.entity.ProjectStatus;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.Optional;

/** @pdOid 31a68138-eb53-4ac1-ac05-3aac924cc45c */
public interface ProjectStatusRepository extends BaseRepository<ProjectStatus, Long> {
    Optional<ProjectStatus> findBySystemCode(String systemCode);
}