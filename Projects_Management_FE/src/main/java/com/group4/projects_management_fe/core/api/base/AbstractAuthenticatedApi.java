package com.group4.projects_management_fe.core.api.base;

import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.Request;

import java.util.concurrent.CompletableFuture;


///  các hàm con gọi API sẽ tự động được inject token vào, không cần custom thêm
public abstract class AbstractAuthenticatedApi extends BaseApi {
    protected final AuthSessionProvider sessionProvider;
    protected AbstractAuthenticatedApi(AuthSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    @Override
    protected <T> CompletableFuture<T> sendRequest(Request request, Class<T> responseClass) {
        var builder = request.newBuilder();
        AuthorizationInjector(builder);
        return super.sendRequest(builder.build(), responseClass);
    }

    private void AuthorizationInjector(Request.Builder builder) {
        String token = sessionProvider.getValidToken();

        if (token != null && !token.isBlank())
            builder.header("Authorization", "Bearer " + token);
    }
}
