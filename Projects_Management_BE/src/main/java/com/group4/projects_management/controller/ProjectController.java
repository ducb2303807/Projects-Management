package com.group4.projects_management.controller; /***********************************************************************
 * Module:  ProjectController.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** @pdOid 77c82126-6d2b-449a-b8d4-32b47d4148fb */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
   /** @pdRoleInfo migr=no name=ProjectService assc=association26 mult=1..1 */
   @Autowired
   private ProjectService projectService;

   @GetMapping
   public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
      return ResponseEntity.ok(projectService.getAllProjects());
   }
   @PostMapping
   public ResponseEntity<ProjectResponseDTO> createProject(ProjectCreateRequestDTO request) {
      return ResponseEntity.ok(projectService.createProject(request));
   }

   @GetMapping("/user/{userId}")
   public ResponseEntity<List<ProjectResponseDTO>> getProjectsByUserId(@PathVariable Long userId) {
      return ResponseEntity.ok(projectService.getProjectsByUserId(userId));
   }

   @GetMapping("/{projectId}")
   public ResponseEntity<ProjectResponseDTO> getProjectDetail(@PathVariable Long projectId) {
      return ResponseEntity.ok(projectService.getProjectDetail(projectId));
   }

   @PutMapping("/{projectId}")
   public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long projectId, @RequestBody ProjectUpdateRequestDTO request) {
      return ResponseEntity.ok(projectService.updateProject(projectId, request));
   }

   @GetMapping("/{projectId}/members")
   public ResponseEntity<List<ProjectMemberDTO>> getMembersOfProject(@PathVariable Long projectId) {
      return ResponseEntity.ok(projectService.getMembersOfProject(projectId));
   }

   @PostMapping("/{projectId}/invite")
   public ResponseEntity<Void> inviteMember(
           @PathVariable Long projectId,
           @RequestParam Long inviteeId,
           @RequestParam Long inviterId,
           @RequestParam Long roleId) {
      projectService.inviteMember(projectId, inviteeId, inviterId, roleId);
      return ResponseEntity.ok().build();
   }

   @PatchMapping("/members/{projectMemberId}/accept")
   public ResponseEntity<Void> acceptInvitation(@PathVariable Long projectMemberId) {
      projectService.acceptInvitation(projectMemberId);
      return ResponseEntity.ok().build();
   }

   @PatchMapping("/members/{projectMemberId}/decline")
   public ResponseEntity<Void> declineInvitation(@PathVariable Long projectMemberId) {
      projectService.declineInvitation(projectMemberId);
      return ResponseEntity.ok().build();
   }

   @GetMapping("/{projectId}/statistics")
   public ResponseEntity<ProjectStatsDTO> getProjectStatistics(@PathVariable Long projectId) {
      return ResponseEntity.ok(projectService.getProjectStatistics(projectId));
   }

   @DeleteMapping("/members/{projectMemberId}/leave")
   public ResponseEntity<Void> leaveProject(@PathVariable Long projectMemberId) {
      projectService.leaveProject(projectMemberId);
      return ResponseEntity.ok().build();
   }

   @DeleteMapping("/members/{projectMemberId}")
   public ResponseEntity<Void> removeMemberFormProject(@PathVariable Long projectMemberId) {
      projectService.removeMemberFromProject(projectMemberId);
      return ResponseEntity.ok().build();
   }

}