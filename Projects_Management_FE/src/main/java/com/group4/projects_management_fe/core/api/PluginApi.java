package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.PluginDTO;
import com.group4.common.dto.UserWidgetConfigDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Class này chưa ổn định, chưa test được?
 */
public class PluginApi extends AbstractAuthenticatedApi {
    private static final String ENDPOINT = "/plugins";

    public PluginApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    /**
     * GET /api/plugins
     * Lấy danh sách tất cả các plugin khả dụng trong hệ thống
     */
    public CompletableFuture<List<PluginDTO>> getAvailablePlugins() {
        return this.sendGetRequest(
                ENDPOINT,
                PluginDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * GET /api/plugins/dashboard/{userId}
     * Lấy cấu hình Dashboard (danh sách widget) của một người dùng
     */
    public CompletableFuture<List<UserWidgetConfigDTO>> getUserDashboardConfig(Long userId) {
        String url = ENDPOINT + "/dashboard/" + userId;
        return this.sendGetRequest(
                url,
                UserWidgetConfigDTO[].class,
                null
        ).thenApply(array -> array != null ? Arrays.asList(array) : List.of());
    }

    /**
     * POST /api/plugins/dashboard/{userId}
     * Lưu bố cục Dashboard mới cho người dùng
     * @param configs Danh sách cấu hình widget
     */
    public CompletableFuture<Void> saveDashboardLayout(Long userId, List<UserWidgetConfigDTO> configs) {
        String url = ENDPOINT + "/dashboard/" + userId;
        return this.sendPostRequest(
                url,
                configs, // Gửi trực tiếp List, Jackson sẽ tự convert thành JSON array
                Void.class,
                null
        );
    }
}
