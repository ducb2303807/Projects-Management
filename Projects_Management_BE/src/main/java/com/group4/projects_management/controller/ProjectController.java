package com.group4.projects_management.controller; /***********************************************************************
 * Module:  ProjectController.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.ProjectService;
import com.group4.projects_management.service.TaskService;
import jakarta.validation.Valid;
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
   @Autowired
   private TaskService taskService;

   @GetMapping
   public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
      return ResponseEntity.ok(projectService.getAllProjects());
   }

   @GetMapping("/me")
   public ResponseEntity<List<ProjectResponseDTO>> getMyProjects() {
      Long currentUserId = SecurityUtils.getCurrentUserId();
      return ResponseEntity.ok(projectService.getProjectsByUserId(currentUserId));
   }

   @PostMapping
   public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectCreateRequestDTO request) {
      return ResponseEntity.ok(projectService.createProject(request));
   }

   @GetMapping("/{projectId}")
   public ResponseEntity<ProjectResponseDTO> getProjectDetail(@PathVariable Long projectId) {
      return ResponseEntity.ok(projectService.getProjectDetail(projectId));
   }

   @GetMapping("/{projectId}/tasks")
   public ResponseEntity<List<TaskResponseDTO>> getTasksByProjectId(@PathVariable Long projectId) {
      return ResponseEntity.ok(taskService.getTasksByProject(projectId));
   }

   @PutMapping("/{projectId}")
   public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long projectId, @RequestBody ProjectUpdateRequestDTO request) {
      return ResponseEntity.ok(projectService.updateProject(projectId, request));
   }

   @GetMapping("/{projectId}/members")
   public ResponseEntity<List<ProjectMemberDTO>> getMembersOfProject(@PathVariable Long projectId) {
      return ResponseEntity.ok(projectService.getMembersOfProject(projectId));
   }

   @PostMapping("/{projectId}/invitations")
   public ResponseEntity<Void> inviteMember(
           @PathVariable Long projectId,
           @RequestParam Long inviteeId,
           @RequestParam Long inviterId,
           @RequestParam Long roleId) {
      projectService.inviteMember(projectId, inviteeId, inviterId, roleId);
      return ResponseEntity.ok().build();
   }

   @PatchMapping("/members/{projectMemberId}")
   public ResponseEntity<Void> updateMemberStatus(
           @PathVariable Long projectMemberId,
           @Valid @RequestBody ProjectMemberUpdateDTO request) {

      projectService.updateMemberStatus(projectMemberId, request);
      return ResponseEntity.ok().build();
   }

   @DeleteMapping("/members/{projectMemberId}")
   public ResponseEntity<Void> removeMemberFormProject(@PathVariable Long projectMemberId) {
      projectService.removeMemberFromProject(projectMemberId);
      return ResponseEntity.ok().build();
   }

   @GetMapping("/{projectId}/statistics")
   public ResponseEntity<ProjectStatsDTO> getProjectStatistics(@PathVariable Long projectId) {
      return ResponseEntity.ok(projectService.getProjectStatistics(projectId));
   }
}