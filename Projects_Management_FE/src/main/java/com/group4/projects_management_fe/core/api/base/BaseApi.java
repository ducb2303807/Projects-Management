package com.group4.projects_management_fe.core.api.base;

import com.group4.common.dto.ErrorResponse;
import com.group4.projects_management_fe.core.api.config.ApiConfig;
import com.group4.projects_management_fe.core.exception.ApiException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class BaseApi {
    protected static final String BASE_URL = ApiConfig.BASE_URL;
    protected static final OkHttpClient client = ApiConfig.SHARED_HTTP_CLIENT;
    protected static final JsonMapper jsonMapper = ApiConfig.JSON_MAPPER;

    protected <T> CompletableFuture<T> sendRequest(Request request, Class<T> responseClass) {
        CompletableFuture<T> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(new ApiException("Lỗi kết nối: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String data = responseBody != null ? responseBody.string() : "";

                    if (response.isSuccessful()) {
                        if (responseClass == Void.class) {
                            future.complete(null);
                        } else {
                            T Result = jsonMapper.readValue(data, responseClass);
                            future.complete(Result);
                        }
                    } else {
                        handleError(data, response.code(), future);
                    }
                }
            }
        });
        return future;
    }

    private <T> void handleError(String jsonData, int statusCode, CompletableFuture<T> future) {
        if (jsonData == null || jsonData.isBlank()) {
            future.completeExceptionally(new ApiException("Lỗi hệ thống (Status: " + statusCode + ")"));
            return;
        }
        try {
            ErrorResponse errorResponse = jsonMapper.readValue(jsonData, ErrorResponse.class);
            future.completeExceptionally(new ApiException(errorResponse.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            future.completeExceptionally(new ApiException("Lỗi không xác định (HTTP " + statusCode + ")"));
        }
    }
}
