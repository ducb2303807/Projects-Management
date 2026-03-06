package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.AuthResponse;
import com.group4.common.dto.LoginRequest;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.projects_management_fe.core.api.base.BaseApi;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.concurrent.CompletableFuture;

public class AuthApi extends BaseApi {
    public CompletableFuture<AuthResponse> login(LoginRequest loginReq) {
        try {
            String json = jsonMapper.writeValueAsString(loginReq);

            RequestBody body = RequestBody.create(
                    json, MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/auth/login")
                    .post(body)
                    .build();

            return this.sendRequest(request, AuthResponse.class);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<UserDTO> register(UserRegistrationDTO registrationData) {
        try {
            String json = jsonMapper.writeValueAsString(registrationData);

            RequestBody body = RequestBody.create(
                    json, MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/auth/register")
                    .post(body)
                    .build();

            return this.sendRequest(request, UserDTO.class);
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}

