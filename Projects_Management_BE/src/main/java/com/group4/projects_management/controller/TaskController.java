package com.group4.projects_management.controller; /***********************************************************************
 * Module:  TaskController.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskController
 ***********************************************************************/

import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** @pdOid ce9b19d3-3ddd-449d-8d79-b270923a5f2d */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
   @Autowired
   private TaskService taskService;

   @PostMapping("/project/{projectId}")
   public ResponseEntity<TaskResponseDTO> createTaskInProject(
           @PathVariable Long projectId,
           @RequestBody TaskCeateRequestDTO request) {

      // Đảm bảo projectId từ URL được gán vào request
      request.setProjectId(projectId);
      return ResponseEntity.ok(taskService.createTask(request));
   }

   @GetMapping("/project/{projectId}")
   public ResponseEntity<List<TaskResponseDTO>> getTasksByProjectId(@PathVariable Long projectId) {
      return ResponseEntity.ok(taskService.getTasksByProject(projectId));
   }

   @PatchMapping("/{taskId}/status")
   public ResponseEntity<Void> setTaskStatus(@PathVariable Long taskId, @RequestParam Long statusId) {
      taskService.updateTaskStatus(taskId, statusId);
      return ResponseEntity.ok().build();
   }

   @PatchMapping("/{taskId}/priority")
   public ResponseEntity<Void> setTaskPriority(@PathVariable Long taskId, @RequestParam Long priorityId) {
      taskService.updateTaskPriority(taskId, priorityId);
      return ResponseEntity.ok().build();
   }

   @PostMapping("/{taskId}/assign")
   public ResponseEntity<Void> assignMember(@PathVariable Long taskId, @RequestParam Long projectMemberId) {
      var userId = SecurityUtils.getCurrentUserId();
      taskService.assignMember(taskId, projectMemberId, userId);
      return ResponseEntity.ok().build();
   }

   @DeleteMapping("/assignments/{taskAssignmentId}")
   public ResponseEntity<Void> removeMemberFromTask(@PathVariable Long taskAssignmentId) {
      taskService.removeMemberFromTask(taskAssignmentId);
      return ResponseEntity.ok().build();
   }

   @GetMapping("/{taskId}/history")
   public ResponseEntity<List<TaskHistoryDTO>> getTaskHistory(@PathVariable Long taskId) {
      return ResponseEntity.ok(taskService.getTaskHistory(taskId));
   }

   @PutMapping("/{taskId}")
   public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId, @RequestBody TaskUpdateDTO request) {
      return ResponseEntity.ok(taskService.updateTask(taskId, request));
   }
}