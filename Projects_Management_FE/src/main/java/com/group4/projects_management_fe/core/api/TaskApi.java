package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.CommentDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.HttpUrl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TaskApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/tasks";

    public TaskApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * GET /api/tasks
     * Lấy tất cả tasks trong hệ thống
     */
    public CompletableFuture<List<TaskResponseDTO>> getAllTasks() {
        return this.sendGetRequest(
                ENDPOINT,
                TaskResponseDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * GET /api/tasks/me?includeCancelled=...
     * Lấy tasks của người dùng hiện tại
     */
    public CompletableFuture<List<TaskResponseDTO>> getMyTasks(boolean includeCancelled) {
        String url = ENDPOINT + "/me";
        return this.sendGetRequest(
                url,
                TaskResponseDTO[].class,
                builder -> addQueryParam(builder, "includeCancelled", includeCancelled)
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * POST /api/tasks/{taskId}/members
     * Body: List<Long> projectMemberIds
     */
    public CompletableFuture<Void> assignMember(Long taskId, List<Long> projectMemberIds) {
        String url = ENDPOINT + "/" + taskId + "/members";
        return this.sendPostRequest(
                url,
                projectMemberIds, // Gửi thẳng List Long, Jackson sẽ tự convert sang JSON array
                Void.class,
                null
        );
    }

    /**
     * DELETE /api/tasks/{taskId}/members/{projectMemberIds}
     * Lưu ý: Controller dùng @RequestParam List<Long> projectMemberIds
     */
    public CompletableFuture<Void> removeMemberFromTask(Long taskId, List<Long> projectMemberIds) {
        // Build path: /tasks/{taskId}/members/all (placeholder cho path variable)
        // Spring sẽ map các ID từ Query Param vào List projectMemberIds
        String idsPath = projectMemberIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("0");
        String url = ENDPOINT + "/" + taskId + "/members/" + idsPath;

        return this.sendDeleteRequest(
                url,
                Void.class,
                builder -> {
                    HttpUrl.Builder urlBuilder = builder.build().url().newBuilder();
                    for (Long id : projectMemberIds) {
                        urlBuilder.addQueryParameter("projectMemberIds", String.valueOf(id));
                    }
                    builder.url(urlBuilder.build());
                }
        );
    }

    /**
     * DELETE /api/tasks/{taskId}
     */
    public CompletableFuture<Void> deleteTask(Long taskId) {
        return this.sendDeleteRequest(ENDPOINT + "/" + taskId, Void.class, null);
    }

    /**
     * GET /api/tasks/{taskId}/histories
     */
    public CompletableFuture<List<TaskHistoryDTO>> getTaskHistory(Long taskId) {
        String url = ENDPOINT + "/" + taskId + "/histories";
        return this.sendGetRequest(
                url,
                TaskHistoryDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * PUT /api/tasks/{taskId}
     */
    public CompletableFuture<TaskResponseDTO> updateTask(Long taskId, TaskUpdateDTO request) {
        String url = ENDPOINT + "/" + taskId;
        return this.sendPutRequest(url, request, TaskResponseDTO.class, null);
    }

    /**
     * GET /api/tasks/{taskId}/comments
     */
    public CompletableFuture<List<CommentDTO>> getTaskComments(Long taskId) {
        String url = ENDPOINT + "/" + taskId + "/comments";
        return this.sendGetRequest(
                url,
                CommentDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * Helper hỗ trợ thêm Query Parameter
     */
    private void addQueryParam(okhttp3.Request.Builder builder, String key, Object value) {
        HttpUrl currentUrl = builder.build().url();
        HttpUrl newUrl = currentUrl.newBuilder()
                .addQueryParameter(key, String.valueOf(value))
                .build();
        builder.url(newUrl);
    }
}