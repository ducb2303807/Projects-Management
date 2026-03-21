package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.*;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.HttpUrl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/users";

    public UserApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * GET /api/users
     * Lấy tất cả thông tin của người dùng
     */
    public CompletableFuture<List<UserDTO>> getAllUsers() {
        return this.sendGetRequest(
                ENDPOINT,
                UserDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * POST /api/users
     * Tạo người dùng mới trong hệ thống
     */
    public CompletableFuture<UserDTO> register(UserRegistrationDTO request) {
        return this.sendPostRequest(
                ENDPOINT,
                request,
                UserDTO.class,
                null
        );
    }

    /**
     * GET /api/users/{userId}/projects
     * Lấy tất cả project của người dùng
     */
    public CompletableFuture<List<ProjectResponseDTO>> getProjectsByUserId(Long userId, boolean includeCancelled) {
        String url = ENDPOINT + "/" + userId + "/projects";

        return this.sendGetRequest(
                url,
                ProjectResponseDTO[].class,
                builder -> {
                    // Thêm query parameter thông qua Request.Builder
                    HttpUrl currentUrl = builder.build().url();
                    HttpUrl newUrl = currentUrl.newBuilder()
                            .addQueryParameter("includeCancelled", String.valueOf(includeCancelled))
                            .build();
                    builder.url(newUrl);
                }
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * POST /api/users/exists
     * Kiểm tra tồn tại của username/email trong hệ thống
     */
    public CompletableFuture<UserExistsResponseDTO> existsByUsernameOrEmail(UserExistsRequestDTO request) {
        String url = ENDPOINT + "/exists";
        return this.sendPostRequest(
                url,
                request,
                UserExistsResponseDTO.class,
                null
        );
    }

    /**
     * GET /api/users/search
     * Tìm kiếm người dùng dựa trên keyword (username/email)
     */
    public CompletableFuture<List<UserDTO>> searchUsers(String keyword) {
        String url = ENDPOINT + "/search";

        return this.sendGetRequest(
                url,
                UserDTO[].class,
                builder -> {
                    HttpUrl currentUrl = builder.build().url();
                    HttpUrl newUrl = currentUrl.newBuilder()
                            .addQueryParameter("keyword", keyword)
                            .build();
                    builder.url(newUrl);
                }
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * PATCH /api/users/{userId}
     * Thay đổi thông tin cá nhân của người dùng
     */
    public CompletableFuture<UserDTO> updateProfile(Long userId, UserUpdateDTO request) {
        String url = ENDPOINT + "/" + userId;
        return this.sendPatchRequest(
                url,
                request,
                UserDTO.class,
                null
        );
    }

    /**
     * PATCH /api/users/{userId}/password
     * Thay đổi mật khẩu của người dùng
     */
    public CompletableFuture<Void> changePassword(Long userId, ChangePasswordRequestDTO dto) {
        String url = ENDPOINT + "/" + userId + "/password";
        return this.sendPatchRequest(
                url,
                dto,
                Void.class,
                null
        );
    }
}
