package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.UserDTO;
import com.group4.projects_management_fe.core.api.base.AbstractAuthenticatedApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserApi extends AbstractAuthenticatedApi {
    private static final String END_POINT = "/users";

    public UserApi(AuthSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    public CompletableFuture<List<UserDTO>> getAllUsers() {
        return this.sendGetRequest(
                END_POINT,
                UserDTO[].class,
                null
        ).thenApply(List::of);
    }

}
