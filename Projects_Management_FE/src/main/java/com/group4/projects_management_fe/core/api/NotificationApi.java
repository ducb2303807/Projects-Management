package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.RequestBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NotificationApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/notifications";
    public NotificationApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * Tương ứng với @GetMapping("/me")
     * Lấy danh sách thông báo của user đang đăng nhập
     */
    public CompletableFuture<List<NotificationDTO>> getNotificationsForUser() {
        String endpoint = ENDPOINT + "/me";

        return this.sendGetRequest(
                endpoint,
                NotificationDTO[].class,
                null
        ).thenApply(List::of);
    }

    /**
     * Tương ứng với @PatchMapping("/{notificationId}/read")
     * Đánh dấu thông báo đã đọc
     */
    public CompletableFuture<Void> markAsRead(Long notificationId) {
        String endpoint = ENDPOINT + "/" + notificationId + "/read";

        RequestBody emptyBody = RequestBody.create(new byte[0], null);
        return this.sendPatchRequest(
                endpoint,
                emptyBody,
                Void.class,
                null
        );
    }
}
