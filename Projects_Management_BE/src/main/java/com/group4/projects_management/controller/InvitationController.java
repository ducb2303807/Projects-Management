package com.group4.projects_management.controller;

import com.group4.common.dto.InvitationDTO;
import com.group4.common.dto.InvitationRequestDTO;
import com.group4.projects_management.core.security.SecurityUtils;
import com.group4.projects_management.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@SecurityRequirement(name = "bearerAuth")
public class InvitationController {
    @Autowired
    private ProjectService projectService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<InvitationDTO>> getInvitation(@PathVariable Long userId) {
        return ResponseEntity.ok(projectService.getPendingInvitations(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<InvitationDTO>> getInvitation() {
        var userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(projectService.getPendingInvitations(userId));
    }


    @Operation(
            summary = "Chấp nhận hoặc tự chối lời mời vào project",
            description = ""
    )
    @PatchMapping("/{projectMemberId}")
    public ResponseEntity<Void> handleInvitation(@PathVariable Long projectMemberId,
                                                 @RequestBody InvitationRequestDTO dto)
    {
        projectService.updateMemberStatus(projectMemberId, dto);
        return ResponseEntity.ok().build();
    }
}
