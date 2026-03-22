package com.group4.projects_management.repository; /***********************************************************************
 * Module:  TaskRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface TaskRepository
 ***********************************************************************/

import com.group4.projects_management.entity.Task;
import com.group4.projects_management.repository.Base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/** @pdOid 9b851944-caed-4a10-a0ed-e8cb4396c90f */
public interface TaskRepository extends BaseRepository<Task, Long> {

    int countByProject_Id(Long projectId);

    int countByProject_IdAndTaskStatus_SystemCode(Long projectId, String statusCode);

    List<Task> findByProject_Id(Long projectId);

    List<Task> findByProject_IdAndTaskStatus_Id(Long projectId, Long statusId);

    @Query("SELECT t FROM Task t WHERE t.deadline BETWEEN :start AND :end " +
            "AND t.taskStatus.systemCode NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findTasksExpiringBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Task t WHERE t.deadline < :now " +
            "AND t.taskStatus.systemCode NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findOverdueTasks(LocalDateTime now);

    // Đếm số lượng task của project mà trạng thái KHÔNG PHẢI là 'CANCELLED'
    @Query("SELECT COUNT(t) FROM Task t " +
            "WHERE t.project.id = :projectId " +
            "AND t.taskStatus.systemCode <> :status")
    int countByProjectIdAndStatusNot(
            @Param("projectId") Long projectId,
            @Param("status") String status);

    // Tìm danh sách task của project mà trạng thái KHÔNG PHẢI là 'CANCELLED'
    @Query("SELECT t FROM Task t " +
            "WHERE t.project.id = :projectId " +
            "AND t.taskStatus.systemCode <> :status")
    List<Task> findByProjectIdAndStatusNot(
            @Param("projectId") Long projectId,
            @Param("status") String status);

    @Query("SELECT t FROM Task t " +
            "WHERE t.project.id = :projectId " +
            "AND t.taskStatus.systemCode <> :tStatus " +
            "AND t.project.projectStatus.systemCode <> :pStatus")
    List<Task> findActiveTasksByProjectId(
            @Param("projectId") Long projectId,
            @Param("tStatus") String tStatus,
            @Param("pStatus") String pStatus);

}
