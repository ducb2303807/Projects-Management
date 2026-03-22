package com.group4.projects_management.controller; /***********************************************************************
 * Module:  ProjectController.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.ProjectService;
import com.group4.projects_management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskService taskService;

    @Operation(summary = "Lấy thông tin tất cả projects")
    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(summary = "Lấy thông tin projects mà tôi tham gia")
    @GetMapping("/me")
    public ResponseEntity<List<ProjectResponseDTO>> getMyProjects(
            @RequestParam(defaultValue = "false") boolean includeCancelled) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(projectService.getProjectsByUserId(currentUserId, includeCancelled));
    }

    @Operation(summary = "Tạo project")
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectCreateRequestDTO request) {
        var userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(projectService.createProject(userId, request));
    }

    @Operation(summary = "lấy thông tin project")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProjectDetail(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectDetail(projectId));
    }

    @Operation(summary = "lấy thông tin tasks của project")
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByProjectId(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "false") boolean includeCancelled) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, includeCancelled));
    }

    @Operation(summary = "Tạo task vào project")
    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<TaskResponseDTO> createTaskInProject(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskCeateRequestDTO request) {
        request.setProjectId(projectId);
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @Operation(summary = "Cập nhật thông tin project")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long projectId,
                                                            @Valid @RequestBody ProjectUpdateRequestDTO request) {
        Long requester = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(projectService.updateProject(projectId, request, requester));
    }

    @Operation(summary = "Xóa project",
            description = "Đặt project thành trạng thái CANCELED")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        Long requesterId = SecurityUtils.getCurrentUserId();
        projectService.deleteProject(projectId, requesterId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "lấy thông tin các member của project")
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberDTO>> getMembersOfProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getMembersOfProject(projectId));
    }

    @Operation(summary = "Tạo lời mời thêm members vào project")
    @PostMapping("/{projectId}/invitations")
    public ResponseEntity<Void> inviteMember(
            @PathVariable Long projectId,
            @Valid @RequestBody List<MemberInviteRequest> request) {
        var inviterId = SecurityUtils.getCurrentUserId();
        projectService.inviteMembers(projectId, request, inviterId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Cập nhật trạng thái member trong project",
            description = "Như cập nhật trạng thái member từ ACTIVE sang LEFT hoặc REMOVE"
    )
    @PatchMapping("/members/{projectMemberId}")
    public ResponseEntity<Void> updateMemberStatus(
            @PathVariable Long projectMemberId,
            @Valid @RequestBody ProjectMemberUpdateDTO request) {

        projectService.updateMemberStatus(projectMemberId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xóa members khỏi project")
    @DeleteMapping("/members/{projectMemberId}")
    public ResponseEntity<Void> removeMemberFormProject(@PathVariable Long projectMemberId) {
        Long requester = SecurityUtils.getCurrentUserId();
        projectService.removeMemberFromProject(projectMemberId, requester);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Các thông số cơ bản của project")
    @GetMapping("/{projectId}/statistics")
    public ResponseEntity<ProjectStatsDTO> getProjectStatistics(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectStatistics(projectId));
    }
}