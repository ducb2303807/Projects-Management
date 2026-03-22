package com.group4.projects_management.repository; /***********************************************************************
 * Module:  ProjectMemberRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface ProjectMemberRepository
 ***********************************************************************/

import com.group4.projects_management.entity.ProjectMember;
import com.group4.projects_management.repository.Base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** @pdOid 2c6d799b-fc02-4a09-a1f3-21ac987720f0 */
public interface ProjectMemberRepository extends BaseRepository<ProjectMember, Long> {

    List<ProjectMember> findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(Long userId, String status);

    boolean existsByProject_IdAndUser_Id(Long projectId, Long userId);

    Optional<ProjectMember> findByUser_IdAndProject_Id(Long userId, Long projectId);

    List<ProjectMember> findAllByUser_IdAndProjectMemberStatus_SystemCode(Long userId, String projectMemberStatusSystemCode);

    Optional<ProjectMember> findByProject_IdAndUser_Id(Long projectId, Long userId);

    List<ProjectMember> findAllByProject_IdAndUser_IdIn(Long projectId, List<Long> userIds);

    @Query("SELECT COUNT(m) FROM ProjectMember m WHERE m.project.id = :projectId AND m.projectMemberStatus.systemCode = 'ACCEPTED'")
    int countActiveMembersByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMember pm " +
            "WHERE pm.user.id = :userId " +
            "AND pm.leftAt IS NULL " +
            "AND pm.projectMemberStatus.systemCode = :memberStatus " +
            "AND pm.project.projectStatus.systemCode <> :cancelledStatus")
    List<ProjectMember> findActiveProjectsForUser(
            @Param("userId") Long userId,
            @Param("memberStatus") String memberStatus,
            @Param("cancelledStatus") String cancelledStatus);
}