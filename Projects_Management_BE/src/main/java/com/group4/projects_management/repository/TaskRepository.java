package com.group4.projects_management.repository; /***********************************************************************
 * Module:  TaskRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface TaskRepository
 ***********************************************************************/

import com.group4.projects_management.entity.Task;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.List;

/** @pdOid 9b851944-caed-4a10-a0ed-e8cb4396c90f */
public interface TaskRepository extends BaseRepository<Task, Long> {

    int countByProject_Id(Long projectId);

    int countByProject_IdAndTaskStatus_SystemCode(Long projectId, String statusCode);

    List<Task> findByProject_Id(Long projectId);

    List<Task> findByProject_IdAndTaskStatus_Id(Long projectId, Long statusId);
}
