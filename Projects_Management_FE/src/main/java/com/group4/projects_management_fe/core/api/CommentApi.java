package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.CommentCreateRequestDTO;
import com.group4.common.dto.CommentDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.util.concurrent.CompletableFuture;

public class CommentApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/comments";

    public CommentApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * POST /api/comments
     * Thêm một comment mới vào task
     *
     * @param request Chứa taskId, projectMemberId, content và parentId (nếu là reply)
     * @return CompletableFuture chứa CommentDTO vừa tạo
     */
    public CompletableFuture<CommentDTO> createComment(CommentCreateRequestDTO request) {
        return this.sendPostRequest(
                ENDPOINT,
                request,
                CommentDTO.class,
                null
        );
    }
}
