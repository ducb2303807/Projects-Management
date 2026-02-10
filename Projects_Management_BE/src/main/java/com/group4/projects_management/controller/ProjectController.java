package com.group4.projects_management.controller; /***********************************************************************
 * Module:  ProjectController.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** @pdOid 77c82126-6d2b-449a-b8d4-32b47d4148fb */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
   /** @pdRoleInfo migr=no name=ProjectService assc=association26 mult=1..1 */
   @Autowired
   private ProjectService projectService;
   
   /** @param request
    * @pdOid a25a207d-c8f5-473f-ac33-b82960f1beee */
   @PostMapping("")
   public ResponseEntity<ProjectResponseDTO> createProject(ProjectCreateRequestDTO request) {
      // TODO: implement
      return null;
   }
   
   /** @param userId
    * @pdOid 89fa0401-f837-4cee-937e-263110cba329 */
   public ResponseEntity<List<ProjectResponseDTO>> getProjectsByUserId(Long userId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectId
    * @pdOid 6fa9475b-8ab5-4289-a545-d9f8c494c8ed */
   public ResponseEntity<ProjectResponseDTO> getProjectDetail(Long projectId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectId 
    * @param request
    * @pdOid 9c906992-7656-4c21-83d2-0394766b40e8 */
   public ResponseEntity<ProjectResponseDTO> updateProject(Long projectId, ProjectUpdateRequestDTO request) {
      // TODO: implement
      return null;
   }
   
   /** @param projectId
    * @pdOid fa8309b1-edbf-4c7f-aa40-c16b267df4e9 */
   public ResponseEntity<List<ProjectMemberDTO>> getMembersOfProject(Long projectId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectId 
    * @param inviteeId 
    * @param inviterId 
    * @param roleId
    * @pdOid a6ec21f9-57ac-48bc-91e6-fabdb49fab00 */
   public ResponseEntity<Void> inviteMember(Long projectId, Long inviteeId, Long inviterId, Long roleId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectMemberId
    * @pdOid 9714b47e-e717-41ad-b115-e687fef46bed */
   public ResponseEntity<Void> acceptInvitation(Long projectMemberId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectMemerId
    * @pdOid a14de7d9-64bd-44d0-8354-3166b220a889 */
   public ResponseEntity<Void> declineInvitation(Long projectMemerId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectId
    * @pdOid d3100255-5021-495b-b43e-7048512ed62a */
   public ResponseEntity<ProjectStatsDTO> getProjectStatistics(Long projectId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectMemberId
    * @pdOid eda92cad-65d1-4fa5-8d95-0f312691d814 */
   public ResponseEntity<Void> leaveProject(Long projectMemberId) {
      // TODO: implement
      return null;
   }
   
   /** @param projectMemberId
    * @pdOid cf4c4eb3-cd57-44eb-9f4e-ead01feef07f */
   public ResponseEntity<Void> removeMemberFormProject(Long projectMemberId) {
      // TODO: implement
      return null;
   }

}