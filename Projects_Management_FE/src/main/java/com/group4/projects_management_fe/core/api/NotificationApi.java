package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.RequestBody;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NotificationApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/notifications";

    public NotificationApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * GET /api/notifications/me
     * Lấy danh sách thông báo của user đang đăng nhập
     */
    public CompletableFuture<List<NotificationDTO>> getNotificationsForUser() {
        String url = ENDPOINT + "/me";

        return this.sendGetRequest(
                url,
                NotificationDTO[].class, // Sử dụng mảng để tránh lỗi Type Erasure của Generic List
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * PATCH /api/notifications/{notificationId}/read
     * Đánh dấu một thông báo cụ thể là đã đọc
     */
    public CompletableFuture<Void> markAsRead(Long notificationId) {
        String url = ENDPOINT + "/" + notificationId + "/read";

        RequestBody emptyBody = RequestBody.create("", JSON_MEDIA_TYPE);

        return this.sendPatchRequest(
                url,
                emptyBody,
                Void.class,
                null
        );
    }

    /**
     * PATCH /api/notifications/read-all
     * Đánh dấu tất cả thông báo là đã đọc
     */
    public CompletableFuture<Void> markAllAsRead() {
        String url = ENDPOINT + "/me/read-all";

        // Gửi body rỗng tương tự như markAsRead
        RequestBody emptyBody = RequestBody.create("", JSON_MEDIA_TYPE);

        return this.sendPatchRequest(
                url,
                emptyBody,
                Void.class,
                null
        );
    }

    /**
     * GET /api/notifications/unread-count
     * Lấy số lượng thông báo chưa đọc của user
     */
    public CompletableFuture<Integer> getUnreadCount() {
        String url = ENDPOINT + "/me/unread-count";

        return this.sendGetRequest(
                url,
                Integer.class,
                null
        ).thenApply(count -> count != null ? count : 0);
    }

}
