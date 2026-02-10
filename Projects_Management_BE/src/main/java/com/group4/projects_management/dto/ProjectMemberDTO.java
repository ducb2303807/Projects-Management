package com.group4.projects_management.dto;

import java.time.LocalDateTime;

public class ProjectMemberDTO {
    private Long projectMemberId;
    private Long userId;
    private Long projectId;
    private Long projectRoleId;
    private Long projectMemberStatusId;
    private Long projectMemberInviteId;
    private LocalDateTime projectMemberJoinAt;
    private LocalDateTime projectMemberLeftAt;

    public ProjectMemberDTO() {}

    public ProjectMemberDTO(Long projectMemberId, Long userId, Long projectId,
                            Long projectRoleId, Long projectMemberStatusId,
                            Long projectMemberInviteId, LocalDateTime projectMemberJoinAt,
                            LocalDateTime projectMemberLeftAt) {
        this.projectMemberId = projectMemberId;
        this.userId = userId;
        this.projectId = projectId;
        this.projectRoleId = projectRoleId;
        this.projectMemberStatusId = projectMemberStatusId;
        this.projectMemberInviteId = projectMemberInviteId;
        this.projectMemberJoinAt = projectMemberJoinAt;
        this.projectMemberLeftAt = projectMemberLeftAt;
    }

    public Long getProjectMemberId() { return projectMemberId; }
    public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getProjectRoleId() { return projectRoleId; }
    public void setProjectRoleId(Long projectRoleId) { this.projectRoleId = projectRoleId; }

    public Long getProjectMemberStatusId() { return projectMemberStatusId; }
    public void setProjectMemberStatusId(Long projectMemberStatusId) { this.projectMemberStatusId = projectMemberStatusId; }

    public Long getProjectMemberInviteId() { return projectMemberInviteId; }
    public void setProjectMemberInviteId(Long projectMemberInviteId) { this.projectMemberInviteId = projectMemberInviteId; }

    public LocalDateTime getProjectMemberJoinAt() { return projectMemberJoinAt; }
    public void setProjectMemberJoinAt(LocalDateTime projectMemberJoinAt) { this.projectMemberJoinAt = projectMemberJoinAt; }

    public LocalDateTime getProjectMemberLeftAt() { return projectMemberLeftAt; }
    public void setProjectMemberLeftAt(LocalDateTime projectMemberLeftAt) { this.projectMemberLeftAt = projectMemberLeftAt; }
}
