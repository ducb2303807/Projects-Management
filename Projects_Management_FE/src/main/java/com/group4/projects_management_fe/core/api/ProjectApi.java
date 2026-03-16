package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.ProjectResponseDTO;
import com.group4.common.dto.ProjectCreateRequestDTO;
import com.group4.common.dto.ProjectUpdateRequestDTO;
import com.group4.common.dto.ProjectMemberDTO;
import com.group4.common.dto.ProjectStatsDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.ProjectMemberUpdateDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProjectApi extends AbstractAuthenticatedApi {
    private static final String BASE_ENDPOINT = "/api/projects";

    public ProjectApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    // GET /api/projects
    public CompletableFuture<List<ProjectResponseDTO>> getAllProjects() {
        return this.sendGetRequest(BASE_ENDPOINT, ProjectResponseDTO[].class, null)
                .thenApply(Arrays::asList);
    }

    // GET /api/projects/me
    public CompletableFuture<List<ProjectResponseDTO>> getMyProjects() {
        return this.sendGetRequest(BASE_ENDPOINT + "/me", ProjectResponseDTO[].class, null)
                .thenApply(Arrays::asList);
    }

    // POST /api/projects
    public CompletableFuture<ProjectResponseDTO> createProject(ProjectCreateRequestDTO dto) {
        return this.sendPostRequest(BASE_ENDPOINT, dto, ProjectResponseDTO.class, null);
    }

    // GET /api/projects/{projectId}
    public CompletableFuture<ProjectResponseDTO> getProjectDetail(Long projectId) {
        return this.sendGetRequest(BASE_ENDPOINT + "/" + projectId, ProjectResponseDTO.class, null);
    }

    // GET /api/projects/{projectId}/tasks
    public CompletableFuture<List<TaskResponseDTO>> getTasksByProjectId(Long projectId) {
        return this.sendGetRequest(BASE_ENDPOINT + "/" + projectId + "/tasks", TaskResponseDTO[].class, null)
                .thenApply(Arrays::asList);
    }

    // PUT /api/projects/{projectId}
    public CompletableFuture<ProjectResponseDTO> updateProject(Long projectId, ProjectUpdateRequestDTO dto) {
        return this.sendPutRequest(BASE_ENDPOINT + "/" + projectId, dto, ProjectResponseDTO.class, null);
    }

    // GET /api/projects/{projectId}/members
    public CompletableFuture<List<ProjectMemberDTO>> getMembersOfProject(Long projectId) {
        return this.sendGetRequest(BASE_ENDPOINT + "/" + projectId + "/members", ProjectMemberDTO[].class, null)
                .thenApply(Arrays::asList);
    }

    // POST /api/projects/{projectId}/invitations
    public CompletableFuture<Void> inviteMember(Long projectId, Long inviteeId, Long inviterId, Long roleId) {
        String endpoint = BASE_ENDPOINT + "/" + projectId + "/invitations"
                + "?inviteeId=" + inviteeId
                + "&inviterId=" + inviterId
                + "&roleId=" + roleId;
        return this.sendPostRequest(endpoint, null, Void.class, null);
    }

    // PATCH /api/projects/members/{projectMemberId}
    public CompletableFuture<Void> updateMemberStatus(Long projectMemberId, ProjectMemberUpdateDTO dto) {
        return this.sendPatchRequest(BASE_ENDPOINT + "/members/" + projectMemberId, dto, Void.class, null);
    }

    // DELETE /api/projects/members/{projectMemberId}
    public CompletableFuture<Void> removeMemberFromProject(Long projectMemberId) {
        return this.sendDeleteRequest(BASE_ENDPOINT + "/members/" + projectMemberId, Void.class, null);
    }

    // GET /api/projects/{projectId}/statistics
    public CompletableFuture<ProjectStatsDTO> getProjectStatistics(Long projectId) {
        return this.sendGetRequest(BASE_ENDPOINT + "/" + projectId + "/statistics", ProjectStatsDTO.class, null);
    }
}
