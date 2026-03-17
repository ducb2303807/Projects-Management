package com.group4.projects_management.controller;

import com.group4.common.dto.InvitationDTO;
import com.group4.projects_management.service.ProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
