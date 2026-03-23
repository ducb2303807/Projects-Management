package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.*;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.HttpUrl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProjectApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/projects";

    public ProjectApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * GET /api/projects
     */
    public CompletableFuture<List<ProjectResponseDTO>> getAllProjects() {
        return this.sendGetRequest(
                ENDPOINT,
                ProjectResponseDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * GET /api/projects/me
     */
    public CompletableFuture<List<ProjectResponseDTO>> getMyProjects(boolean includeCancelled) {
        String url = ENDPOINT + "/me";
        return this.sendGetRequest(
                url,
                ProjectResponseDTO[].class,
                builder -> addQueryParam(builder, "includeCancelled", includeCancelled)
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * POST /api/projects
     */
    public CompletableFuture<ProjectResponseDTO> createProject(ProjectCreateRequestDTO request) {
        return this.sendPostRequest(ENDPOINT, request, ProjectResponseDTO.class, null);
    }

    /**
     * GET /api/projects/{projectId}
     */
    public CompletableFuture<ProjectResponseDTO> getProjectDetail(Long projectId) {
        return this.sendGetRequest(ENDPOINT + "/" + projectId, ProjectResponseDTO.class, null);
    }

    /**
     * GET /api/projects/{projectId}/tasks
     */
    public CompletableFuture<List<TaskResponseDTO>> getTasksByProjectId(Long projectId, boolean includeCancelled) {
        String url = ENDPOINT + "/" + projectId + "/tasks";
        return this.sendGetRequest(
                url,
                TaskResponseDTO[].class,
                builder -> addQueryParam(builder, "includeCancelled", includeCancelled)
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * POST /api/projects/{projectId}/tasks
     */
    public CompletableFuture<TaskResponseDTO> createTaskInProject(Long projectId, TaskCreateRequestDTO request) {
        String url = ENDPOINT + "/" + projectId + "/tasks";
        return this.sendPostRequest(url, request, TaskResponseDTO.class, null);
    }

    /**
     * PUT /api/projects/{projectId}
     */
    public CompletableFuture<ProjectResponseDTO> updateProject(Long projectId, ProjectUpdateRequestDTO request) {
        String url = ENDPOINT + "/" + projectId;
        return this.sendPutRequest(url, request, ProjectResponseDTO.class, null);
    }

    /**
     * DELETE /api/projects/{projectId}
     */
    public CompletableFuture<Void> deleteProject(Long projectId) {
        return this.sendDeleteRequest(ENDPOINT + "/" + projectId, Void.class, null);
    }

    /**
     * GET /api/projects/{projectId}/members
     */
    public CompletableFuture<List<ProjectMemberDTO>> getMembersOfProject(Long projectId) {
        String url = ENDPOINT + "/" + projectId + "/members";
        return this.sendGetRequest(
                url,
                ProjectMemberDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * POST /api/projects/{projectId}/invitations
     */
    public CompletableFuture<Void> inviteMembers(Long projectId, List<MemberInviteRequest> request) {
        String url = ENDPOINT + "/" + projectId + "/invitations";
        return this.sendPostRequest(url, request, Void.class, null);
    }

    /**
     * PATCH /api/projects/members/{projectMemberId}
     */
    public CompletableFuture<Void> updateMemberStatus(Long projectMemberId, ProjectMemberUpdateDTO request) {
        String url = ENDPOINT + "/members/" + projectMemberId;
        return this.sendPatchRequest(url, request, Void.class, null);
    }

    /**
     * DELETE /api/projects/members/{projectMemberId}
     */
    public CompletableFuture<Void> removeMemberFromProject(Long projectMemberId) {
        String url = ENDPOINT + "/members/" + projectMemberId;
        return this.sendDeleteRequest(url, Void.class, null);
    }

    /**
     * GET /api/projects/{projectId}/statistics
     */
    public CompletableFuture<ProjectStatsDTO> getProjectStatistics(Long projectId) {
        String url = ENDPOINT + "/" + projectId + "/statistics";
        return this.sendGetRequest(url, ProjectStatsDTO.class, null);
    }

    /**
     * Helper để thêm query parameter vào OkHttp Request Builder
     */
    private void addQueryParam(okhttp3.Request.Builder builder, String key, Object value) {
        HttpUrl currentUrl = builder.build().url();
        HttpUrl newUrl = currentUrl.newBuilder()
                .addQueryParameter(key, String.valueOf(value))
                .build();
        builder.url(newUrl);
    }
}
