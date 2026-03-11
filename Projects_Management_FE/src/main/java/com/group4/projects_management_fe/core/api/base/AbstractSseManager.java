package com.group4.projects_management_fe.core.api.base;

import com.group4.common.dto.ErrorResponse;
import com.group4.common.enums.BusinessErrorCode;
import com.group4.projects_management_fe.core.config.DotEnvManager;
import com.group4.projects_management_fe.core.exception.ApiException;
import com.group4.projects_management_fe.core.exception.UnauthorizedException;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSseManager<T> implements SseClientManager<T> {
    private final String endpoint = DotEnvManager.getEnv("API_BASE_URL", "http://localhost:8080/api") + "/notifications/subscribe/me";

    protected final int MAX_RETRY_DELAY_MILLISECOND = Integer.parseInt(
            DotEnvManager.getEnv("APP_TIMEOUT", String.valueOf(Duration.ofSeconds(6).toMillis()))
    );
    protected final JsonMapper jsonMapper;
    protected final OkHttpClient client;
    protected final Class<T> responseType;


    protected AbstractSseManager(Class<T> responseType, AuthSessionProvider sessionProvider) {
        this.responseType = responseType;

        this.jsonMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        var clientBuilder = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS);

        if (sessionProvider != null) {
            clientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                String token = sessionProvider.getValidToken();

                if (token != null && !token.isEmpty()) {
                    Request authorized = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(authorized);
                } else return chain.proceed(original);
            });
        }
        this.client = clientBuilder.build();
    }

    protected String getUrl() {
        return endpoint;
    }

    protected T parseData(String data) throws Exception {
        return jsonMapper.readValue(data, responseType);
    }

    @Override
    public void shutdown() {
        this.disconnect();
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();

        if (client.cache() != null) {
            try {
                client.cache().close();
            } catch (Exception ignored) {
                // Ignored
            }
        }
        onCustomShutdown();
    }

    protected void onCustomShutdown() {
    }

    protected Throwable parseHttpError(Response response, Throwable defaultThrowable) {
        if (response != null) {
            try {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (!responseBody.isEmpty()) {
                    ErrorResponse errorResponse = jsonMapper.readValue(responseBody, ErrorResponse.class);
                    String errorCode = errorResponse.getErrorCode();
                    if (response.code() == 401 ||
                            BusinessErrorCode.AUTH_INVALID_TOKEN.getCode().equals(errorCode) ||
                            BusinessErrorCode.AUTH_TOKEN_EXPIRED.getCode().equals(errorCode) ||
                            BusinessErrorCode.AUTH_REQUIRED.getCode().equals(errorCode)) {
                        return new UnauthorizedException(errorResponse.getMessage());
                    }
                    return new ApiException(errorResponse.getMessage(), defaultThrowable);
                }
            } catch (Exception e) {
                System.err.println("Không thể parse ErrorResponse từ BE: " + e.getMessage());
            }

            if (response.code() == 401) {
                return new UnauthorizedException("Phiên đăng nhập không hợp lệ");
            } else {
                return new ApiException("Lỗi Server - HTTP Code: " + response.code(), defaultThrowable);
            }
        }
        // response == null
        return defaultThrowable != null ? new ApiException("Mất kết nối mạng: " + defaultThrowable.getMessage(), defaultThrowable)
                : new ApiException("Mất kết nối mạng không xác định", defaultThrowable);
    }
}
