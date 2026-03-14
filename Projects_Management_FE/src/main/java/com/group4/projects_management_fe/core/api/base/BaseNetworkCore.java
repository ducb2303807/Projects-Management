package com.group4.projects_management_fe.core.api.base;

import com.group4.common.dto.ErrorResponse;
import com.group4.common.enums.BusinessErrorCode;
import com.group4.projects_management_fe.core.api.config.ApiConfig;
import com.group4.projects_management_fe.core.exception.ApiException;
import com.group4.projects_management_fe.core.exception.UnauthorizedException;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tools.jackson.databind.json.JsonMapper;

public abstract class BaseNetworkCore {
    protected static final String BASE_URL = ApiConfig.BASE_URL;
    protected static final OkHttpClient client = ApiConfig.SHARED_HTTP_CLIENT;
    protected static final JsonMapper jsonMapper = ApiConfig.JSON_MAPPER;

    protected String buildUrl(String endpoint) {
        String safeBaseUrl = BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/";
        String safeEndpoint = endpoint.startsWith("/") ? endpoint.substring(1) : endpoint;
        return safeBaseUrl + safeEndpoint;
    }

    protected OkHttpClient getHttpClient() {
        return client;
    }

    protected <T> T parseData(String json, Class<T> clazz) throws Exception {
        if (json == null || json.isBlank()) {
            return null;
        }
        return jsonMapper.readValue(json, clazz);
    }

    protected Throwable parseHttpError(Response response, Throwable defaultThrowable) {
        if (response == null) {
            return defaultThrowable != null ? new ApiException("Mất kết nối mạng: " + defaultThrowable.getMessage(), defaultThrowable)
                    : new ApiException("Mất kết nối mạng không xác định");
        }

        try (ResponseBody body = response.body()) {
            String responseBodyString = body != null ? body.string() : "";

            if (!responseBodyString.isEmpty()) {
                ErrorResponse errorResponse = jsonMapper.readValue(responseBodyString, ErrorResponse.class);
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

        if (response.code() == 401)
            return new UnauthorizedException("Phiên đăng nhập không hợp lệ");

        return new ApiException("Lỗi Server - HTTP Code: " + response.code(), defaultThrowable);
    }
}
