package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.InvitationDTO;
import com.group4.common.dto.InvitationRequestDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class InvitationApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/invitations";

    public InvitationApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * GET /api/invitations/{userId}
     * Lấy danh sách lời mời của một userId cụ thể
     */
    public CompletableFuture<List<InvitationDTO>> getInvitationsByUserId(Long userId) {
        String url = ENDPOINT + "/" + userId;
        return this.sendGetRequest(
                url,
                InvitationDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * GET /api/invitations/me
     * Lấy danh sách lời mời của chính người dùng đang đăng nhập
     */
    public CompletableFuture<List<InvitationDTO>> getMyInvitations() {
        String url = ENDPOINT + "/me";
        return this.sendGetRequest(
                url,
                InvitationDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * PATCH /api/invitations/{projectMemberId}
     * Chấp nhận hoặc từ chối lời mời (Dựa vào InvitationRequestDTO với action ACCEPT/DECLINE)
     */
    public CompletableFuture<Void> handleInvitation(Long projectMemberId, InvitationRequestDTO dto) {
        String url = ENDPOINT + "/" + projectMemberId;
        return this.sendPatchRequest(
                url,
                dto,
                Void.class,
                null
        );
    }
}
