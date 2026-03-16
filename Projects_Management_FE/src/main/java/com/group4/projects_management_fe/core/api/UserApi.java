package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.*;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserApi extends AbstractAuthenticatedApi {
    private static final String END_POINT = "/users";

    public UserApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    public CompletableFuture<List<UserDTO>> getAllUsers() {
        return this.sendGetRequest(
                END_POINT,
                UserDTO[].class,
                null
        ).thenApply(List::of);
    }

    // 1. GET /{userId}/projects
    public CompletableFuture<List<ProjectResponseDTO>> getProjectsByUserId(Long userId) {
        return this.sendGetRequest(
                END_POINT + "/" + userId + "/projects",
                ProjectResponseDTO[].class,
                null
        ).thenApply(List::of);
    }

    // 2. POST /exists
    public CompletableFuture<UserExistsResponseDTO> existsByUsernameOrEmail(UserExistsRequestDTO request) {
        return this.sendPostRequest(
                END_POINT + "/exists",
                request,
                UserExistsResponseDTO.class,
                null
        );
    }

    // 3. GET /search?keyword={keyword}
    public CompletableFuture<List<UserDTO>> searchUsers(String keyword) {
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return this.sendGetRequest(
                END_POINT + "/search?keyword=" + encodedKeyword,
                UserDTO[].class,
                null
        ).thenApply(List::of);
    }

    // 4. PATCH /{userId}
    public CompletableFuture<UserDTO> updateProfile(Long userId, UserUpdateDTO request) {
        return this.sendPatchRequest(
                END_POINT + "/" + userId,
                request,
                UserDTO.class,
                null
        );
    }

    // 5. PATCH /{userId}/password
    public CompletableFuture<Void> changePassword(Long userId, ChangePasswordRequestDTO request) {
        return this.sendPatchRequest(
                END_POINT + "/" + userId + "/password",
                request,
                Void.class,
                null
        );
    }

}
