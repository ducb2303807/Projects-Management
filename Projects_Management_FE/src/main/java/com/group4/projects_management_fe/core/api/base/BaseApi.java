package com.group4.projects_management_fe.core.api.base;

import com.group4.common.dto.ErrorResponse;
import com.group4.projects_management_fe.core.config.DotEnvManager;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public abstract class BaseApi {
    protected static final String BASE_URL = DotEnvManager.getEnv("API_BASE_URL", "http://localhost:8080/api");
    protected static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofMillis(
                    Long.parseLong(DotEnvManager.getEnv("APP_TIMEOUT", "1000"))
            ))
            .dispatcher(new Dispatcher(Executors.newSingleThreadExecutor(runable -> {
                Thread thread = new Thread(runable);
                thread.setDaemon(true);
                return thread;
            })))
            .build();
    protected static final JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    protected <T> CompletableFuture<T> sendRequest(Request request, Class<T> responseClass) {
        CompletableFuture<T> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(new Exception("Lỗi kết nối: " + e.getMessage()));
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
            future.completeExceptionally(new Exception("Lỗi hệ thống (Status: " + statusCode + ")"));
            return;
        }
        try {
            ErrorResponse errorResponse = jsonMapper.readValue(jsonData, ErrorResponse.class);
            future.completeExceptionally(new Exception(errorResponse.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            future.completeExceptionally(new Exception("Lỗi không xác định (HTTP " + statusCode + ")"));
        }
    }

    public static void shutdown() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        if (client.cache() != null) {
            try { client.cache().close(); } catch (Exception ignored) {}
        }
    }
}
