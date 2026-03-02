package com.group4.projects_management.service; /***********************************************************************
 * Module:  ProjectService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface ProjectService
 ***********************************************************************/

import com.group4.common.dto.*;

import java.util.List;

/** @pdOid 8bff7f6e-4d91-40f5-85c1-c0b870149bb3 */
public interface ProjectService {
   List<ProjectResponseDTO> getAllProjects();
   void inviteMember(Long projectId, Long inviteeId, Long inviterId, Long roleId);

   void updateMemberStatus(Long memberId, ProjectMemberUpdateDTO request);
   List<InvitationDTO> getPendingInvitations(Long userId);
   /** @param projectMemberId
    * @pdOid ad492c45-0123-4365-a116-0a463d36ca71 */
   void removeMemberFromProject(Long projectMemberId);
   /** @param projectMemberId 
    * @param roleId
    * @pdOid d7beb3a7-c34a-4846-aedb-e15261f7fae1 */
   void updateMemberRole(Long projectMemberId, Long roleId);
   /** @param projectId
    * @pdOid 6ac8e9bb-3470-4722-a757-b4d681856e43 */
   ProjectStatsDTO getProjectStatistics(Long projectId);
   /** @pdOid 27d7677a-473a-40cc-8fbe-51ee89f31f8d */
   List<ProjectResponseDTO> getProjectsByUserId(Long userId);
   /** @param projectId 
    * @param dto
    * @pdOid 1b03a732-3ad2-4fbd-8402-eebae6e90b5a */
   ProjectResponseDTO updateProject(Long projectId, ProjectUpdateRequestDTO dto);

   List<ProjectMemberDTO> getMembersOfProject(Long projectId);
   /** @param projectMemberId
    * @pdOid f8856ff1-4747-43ea-9860-f339b189a203 */
   void leaveProject(Long projectMemberId);
   /** @param dto
    * @pdOid ee12b686-f872-4abd-9cf7-9d4c19372716 */
   ProjectResponseDTO createProject(ProjectCreateRequestDTO dto);
   /** @pdOid 5b3db6cf-258b-47e2-a668-c04294cf5ac2 */
   ProjectResponseDTO getProjectDetail(Long projectId);

}