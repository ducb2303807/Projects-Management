package com.group4.projects_management_fe.core.api.config;

import com.group4.projects_management_fe.core.config.DotEnvManager;
import okhttp3.OkHttpClient;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;

public class ApiConfig {
    public static final String BASE_URL = DotEnvManager.getEnv("API_BASE_URL", "http://localhost:8080/api");
    public static final long APP_TIMEOUT_MS = Long.parseLong(DotEnvManager.getEnv("APP_TIMEOUT", "6000"));

    public static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    public static final OkHttpClient SHARED_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofMillis(APP_TIMEOUT_MS))
            .build();

    private ApiConfig() {}

    public static void shutdown() {
        SHARED_HTTP_CLIENT.dispatcher().executorService().shutdown();
        SHARED_HTTP_CLIENT.connectionPool().evictAll();
        if (SHARED_HTTP_CLIENT.cache() != null) {
            try { SHARED_HTTP_CLIENT.cache().close(); } catch (Exception ignored) {}
        }
    }
}
