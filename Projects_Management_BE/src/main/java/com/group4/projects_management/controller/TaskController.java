package com.group4.projects_management.controller; /***********************************************************************
 * Module:  TaskController.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.CommentService;
import com.group4.projects_management.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @pdOid ce9b19d3-3ddd-449d-8d79-b270923a5f2d
 */
@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/{taskId}/members")
    public ResponseEntity<Void> assignMember(
            @PathVariable Long taskId,
            @RequestBody List<Long> projectMemberId) {
        var userId = SecurityUtils.getCurrentUserId();
        taskService.assignMembers(taskId, projectMemberId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/members/{projectMemberIds}")
    public ResponseEntity<Void> removeMemberFromTask(
            @PathVariable Long taskId,
            @RequestParam List<Long> projectMemberIds) {
        var userId = SecurityUtils.getCurrentUserId();
        taskService.removeMembersFromTask(taskId, projectMemberIds, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{taskId}/histories")
    public ResponseEntity<List<TaskHistoryDTO>> getTaskHistory(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskHistory(taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId,
                                                      @Valid @RequestBody TaskUpdateDTO request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentDTO>> getTaskComment(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }
}