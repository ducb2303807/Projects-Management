package com.group4.projects_management.repository; /***********************************************************************
 * Module:  TaskStatusRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface TaskStatusRepository
 ***********************************************************************/

import com.group4.projects_management.entity.ProjectStatus;
import com.group4.projects_management.entity.TaskStatus;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.Optional;

/** @pdOid 45b32b7f-c638-46bd-84d3-caef3dc5d3eb */
public interface TaskStatusRepository extends BaseRepository<TaskStatus, Long> {

    Optional<ProjectStatus> findBySystemCode(String systemCode);

}