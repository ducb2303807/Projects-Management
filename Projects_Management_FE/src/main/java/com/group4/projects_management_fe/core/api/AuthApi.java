package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.AuthResponse;
import com.group4.common.dto.LoginRequest;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.projects_management_fe.core.api.base.BaseApi;

import java.util.concurrent.CompletableFuture;

public class AuthApi extends BaseApi {
    private static final String LOGIN_ENPOINT = "/auth/login";
    private static final String REGISTER_ENPOINT = "/auth/register";
    public CompletableFuture<AuthResponse> login(LoginRequest loginReq) {
       return this.sendPostRequest(
               LOGIN_ENPOINT,
               loginReq,
               AuthResponse.class,
               null
       );
    }

    public CompletableFuture<UserDTO> register(UserRegistrationDTO registrationData) {
       return this.sendPostRequest(
               REGISTER_ENPOINT,
               registrationData,
               UserDTO.class,
               null
       );
    }
}

