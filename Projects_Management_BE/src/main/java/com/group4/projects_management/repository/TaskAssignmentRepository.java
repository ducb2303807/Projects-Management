package com.group4.projects_management.repository; /***********************************************************************
 * Module:  TaskAssignmentRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface TaskAssignmentRepository
 ***********************************************************************/

import com.group4.projects_management.entity.TaskAssignment;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.List;

/** @pdOid 30841a9b-6d08-428c-9b72-37a731252170 */
public interface TaskAssignmentRepository extends BaseRepository<TaskAssignment, Long> {
    // đếm số task user được assign
    int countByAssignee_User_Id(Long userId);

    // lấy toàn bộ task assignment của user
    List<TaskAssignment> findByAssignee_User_Id(Long userId);
}