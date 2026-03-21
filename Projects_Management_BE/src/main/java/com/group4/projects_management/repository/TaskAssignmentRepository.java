package com.group4.projects_management.repository; /***********************************************************************
 * Module:  TaskAssignmentRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface TaskAssignmentRepository
 ***********************************************************************/

import com.group4.projects_management.entity.Task;
import com.group4.projects_management.entity.TaskAssignment;
import com.group4.projects_management.repository.Base.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @pdOid 30841a9b-6d08-428c-9b72-37a731252170
 */
public interface TaskAssignmentRepository extends BaseRepository<TaskAssignment, Long> {
    int countByAssignee_User_Id(Long userId);

    List<TaskAssignment> findByAssignee_User_Id(Long userId);

    @Modifying
    @Query("DELETE FROM TaskAssignment ta WHERE ta.task.id = :taskId AND ta.assignee.id IN :projectMemberIds")
    void deleteByTaskIdAndProjectMemberIdIn(
            @Param("taskId") Long taskId,
            @Param("projectMemberIds") List<Long> projectMemberIds
    );

    @Query("""
                SELECT DISTINCT t 
                FROM TaskAssignment ta
                JOIN ta.task t
                JOIN FETCH t.taskStatus 
                WHERE ta.assignee.user.id = :userId
                AND ta.assignee.projectMemberStatus.systemCode = :memberStatusCode
                AND t.taskStatus.systemCode != :excludedTaskStatusCode
            """)
    List<Task> findTasksByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("memberStatusCode") String memberStatusCode,
            @Param("excludedTaskStatusCode") String excludedTaskStatusCode
    );

    List<TaskAssignment>  findByTask_Id(Long taskId);
}