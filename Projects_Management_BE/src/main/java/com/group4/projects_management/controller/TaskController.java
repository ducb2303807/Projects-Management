package com.group4.projects_management.controller; /***********************************************************************
 * Module:  TaskController.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.CommentService;
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
   @Autowired
   private CommentService commentService;

   @PostMapping("/project/{projectId}")
   public ResponseEntity<TaskResponseDTO> createTaskInProject(
           @PathVariable Long projectId,
           @RequestBody TaskCeateRequestDTO request) {

      // Đảm bảo projectId từ URL được gán vào request
      request.setProjectId(projectId);
      return ResponseEntity.ok(taskService.createTask(request));
   }

   @PostMapping("/{taskId}/assignments")
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

   @GetMapping("/{taskId}/historys")
   public ResponseEntity<List<TaskHistoryDTO>> getTaskHistory(@PathVariable Long taskId) {
      return ResponseEntity.ok(taskService.getTaskHistory(taskId));
   }

   @PutMapping("/{taskId}")
   public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId, @RequestBody TaskUpdateDTO request) {
      return ResponseEntity.ok(taskService.updateTask(taskId, request));
   }

   @GetMapping("{taskId}/comments/")
   public ResponseEntity<List<CommentDTO>> getTaskComment(@PathVariable Long taskId) {
      return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
   }
}