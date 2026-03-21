package com.group4.projects_management.controller; /***********************************************************************
 * Module:  TaskController.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.CommentService;
import com.group4.projects_management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Lấy thông tin tất cả tasks trong hệ thống")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(summary = "Lấy thông tin tasks mà tôi tham gia, không lấy task bị CANCELED")
    @GetMapping("/me")
    public ResponseEntity<List<TaskResponseDTO>> getMyTasks() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskService.getTasksByUserId(currentUserId));
    }

    @Operation(summary = "Đưa các member đã chọn vào task",
            description = "List ở đây chứa danh sách projectmemberId")
    @PostMapping("/{taskId}/members")
    public ResponseEntity<Void> assignMember(
            @PathVariable Long taskId,
            @RequestBody List<Long> projectMemberId) {
        var userId = SecurityUtils.getCurrentUserId();
        taskService.assignMembers(taskId, projectMemberId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xóa các member đã chọn ra khỏi task",
            description = "List ở đây chứa danh sách projectmemberId")
    @DeleteMapping("/{taskId}/members/{projectMemberIds}")
    public ResponseEntity<Void> removeMemberFromTask(
            @PathVariable Long taskId,
            @RequestParam List<Long> projectMemberIds) {
        var userId = SecurityUtils.getCurrentUserId();
        taskService.removeMembersFromTask(taskId, projectMemberIds, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lấy lịch sử thay đổi của task")
    @GetMapping("/{taskId}/histories")
    public ResponseEntity<List<TaskHistoryDTO>> getTaskHistory(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskHistory(taskId));
    }

    @Operation(summary = "Cập nhật task")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId,
                                                      @Valid @RequestBody TaskUpdateDTO request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @Operation(summary = "Lấy comments của task")
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentDTO>> getTaskComment(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }
}