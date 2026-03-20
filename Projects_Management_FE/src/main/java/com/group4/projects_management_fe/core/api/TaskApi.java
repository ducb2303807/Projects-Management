package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.CommentDTO;
import com.group4.common.dto.CommentCreateRequestDTO;
import com.group4.common.dto.LookupDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TaskApi extends AbstractAuthenticatedApi {
    private static final String BASE_ENDPOINT = "/tasks";

    public TaskApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    // POST /projects/{projectId}/tasks
    public CompletableFuture<TaskResponseDTO> createTaskInProject(Long projectId, TaskCeateRequestDTO dto) {
        String endpoint = "/projects/" + projectId + "/tasks";
        return this.sendPostRequest(endpoint, dto, TaskResponseDTO.class, null);
    }

    // POST /tasks/{taskId}/assignments
    public CompletableFuture<Void> assignMember(Long taskId, Long projectMemberId) {
        String endpoint = BASE_ENDPOINT + "/" + taskId + "/assignments?projectMemberId=" + projectMemberId;
        return this.sendPostRequest(endpoint, null, Void.class, null);
    }

    // DELETE /tasks/assignments/{taskAssignmentId}
    public CompletableFuture<Void> removeMemberFromTask(Long taskAssignmentId) {
        String endpoint = BASE_ENDPOINT + "/assignments/" + taskAssignmentId;
        return this.sendDeleteRequest(endpoint, Void.class, null);
    }

    // GET /tasks/{taskId}/histories
    public CompletableFuture<List<TaskHistoryDTO>> getTaskHistory(Long taskId) {
        String endpoint = BASE_ENDPOINT + "/" + taskId + "/histories";
        return this.sendGetRequest(endpoint, TaskHistoryDTO[].class, null)
                .thenApply(Arrays::asList);
    }

    // PUT /tasks/{taskId}
    public CompletableFuture<TaskResponseDTO> updateTask(Long taskId, TaskUpdateDTO dto) {
        String endpoint = BASE_ENDPOINT + "/" + taskId;
        return this.sendPutRequest(endpoint, dto, TaskResponseDTO.class, null);
    }

    // GET /tasks/{taskId}/comments
    public CompletableFuture<List<CommentDTO>> getTaskComments(Long taskId) {
        String endpoint = BASE_ENDPOINT + "/" + taskId + "/comments";
        return this.sendGetRequest(endpoint, CommentDTO[].class, null)
                .thenApply(Arrays::asList);
    }

    // POST /api/comments -> Khớp backend CommentController.createComment
    public CompletableFuture<CommentDTO> createComment(CommentCreateRequestDTO dto) {
        String endpoint = "/comments";
        return this.sendPostRequest(endpoint, dto, CommentDTO.class, null);
    }

    public CompletableFuture<List<TaskResponseDTO>> getMyTasks() {
        String endpoint = BASE_ENDPOINT + "/me";                    // ← Đúng endpoint backend
        return this.sendGetRequest(endpoint, TaskResponseDTO[].class, null)
                .thenApply(Arrays::asList);
    }
}