package com.group4.projects_management.service; /***********************************************************************
 * Module:  ProjectService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface ProjectService
 ***********************************************************************/

import com.group4.common.dto.*;

import java.util.List;

/** @pdOid 8bff7f6e-4d91-40f5-85c1-c0b870149bb3 */
public interface ProjectService {
   /** @param projectId 
    * @param inviteeId 
    * @param inviterId 
    * @param roleId
    * @pdOid 8144cdbe-f21f-4011-a675-731c7a0723d1 */
   void inviteMember(Long projectId, Long inviteeId, Long inviterId, Long roleId);
   /** @param projectMemberId
    * @pdOid 7f0b6932-4c34-4548-b826-2f84291c5b29 */
   void acceptInvitation(Long projectMemberId);
   /** @param projectMemberId
    * @pdOid a651eee0-e9d3-4dc1-905c-b7e083f859f9 */
   void declineInvitation(Long projectMemberId);
   /** @param userId
    * @pdOid bbc00595-81d2-4f50-bc1a-22428df0a5dc */
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
   List<ProjectResponseDTO> getProjectsByUserId();
   /** @param projectId 
    * @param dto
    * @pdOid 1b03a732-3ad2-4fbd-8402-eebae6e90b5a */
   ProjectResponseDTO updateProject(Long projectId, ProjectCreateRequestDTO dto);
   /** @param projectId
    * @pdOid 2cf5ccf3-3f2b-4551-8bc1-bee95aaeb64e */
   List<ProjectMemberDTO> getMembersOfProject(Long projectId);
   /** @param projectMemberId
    * @pdOid f8856ff1-4747-43ea-9860-f339b189a203 */
   void leaveProject(Long projectMemberId);
   /** @param dto
    * @pdOid ee12b686-f872-4abd-9cf7-9d4c19372716 */
   ProjectResponseDTO createProject(ProjectCreateRequestDTO dto);
   /** @pdOid 5b3db6cf-258b-47e2-a668-c04294cf5ac2 */
   ProjectResponseDTO getProjectDetail();

}