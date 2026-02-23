package com.group4.projects_management.service; /***********************************************************************
 * Module:  ProjectServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectServiceImpl
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.entity.Project;
import com.group4.projects_management.mapper.ProjectMapper;
import com.group4.projects_management.repository.ProjectMemberRepository;
import com.group4.projects_management.repository.ProjectRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/** @pdOid 8ed7a716-6d3c-4f74-ae7a-3d4f7734e7a8 */
@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project,Long> implements ProjectService {
   /** @pdRoleInfo migr=no name=ProjectRepository assc=association25 mult=1..1 */
   private final ProjectRepository projectRepository;

   /** @pdRoleInfo migr=no name=ProjectMemberRepository assc=association45 mult=1..1 */
   public ProjectMemberRepository projectMemberRepository;
   private final ProjectMapper projectMapper;

   public ProjectServiceImpl(ProjectRepository repository, ProjectMapper projectMapper) {
      super(repository);
      this.projectRepository = repository;
       this.projectMapper = projectMapper;
   }

   @Override
   public List<ProjectResponseDTO> getAllProjects() {
      return projectRepository.findAll()
              .stream()
              .map(projectMapper::toDto)
              .toList();
   }

   @Override
   public void inviteMember(Long projectId, Long inviteeId, Long inviterId, Long roleId) {

   }

   @Override
   public void acceptInvitation(Long projectMemberId) {

   }

   @Override
   public void declineInvitation(Long projectMemberId) {

   }

   @Override
   public List<InvitationDTO> getPendingInvitations(Long userId) {
      return List.of();
   }

   @Override
   public void removeMemberFromProject(Long projectMemberId) {

   }

   @Override
   public void updateMemberRole(Long projectMemberId, Long roleId) {

   }

   @Override
   public ProjectStatsDTO getProjectStatistics(Long projectId) {
      return null;
   }

   @Override
   public List<ProjectResponseDTO> getProjectsByUserId(Long userId) {
      return List.of();
   }

   @Override
   public ProjectResponseDTO updateProject(Long projectId, ProjectUpdateRequestDTO dto) {
      return null;
   }

   @Override
   public List<ProjectMemberDTO> getMembersOfProject(Long projectId) {
      return List.of();
   }

   @Override
   public void leaveProject(Long projectMemberId) {

   }

   @Override
   public ProjectResponseDTO createProject(ProjectCreateRequestDTO dto) {
      return null;
   }

   @Override
   public ProjectResponseDTO getProjectDetail(Long projectId) {
      return null;
   }
}