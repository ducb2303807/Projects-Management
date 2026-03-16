package com.group4.projects_management_fe.core.api.base;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class BaseApi extends BaseNetworkCore {
    protected static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    protected <T> CompletableFuture<T> sendRequest(
            Request request,
            Class<T> responseClass) {
        CompletableFuture<T> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(parseHttpError(null, e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        // server trả về lỗi (400, 401, 500...)
                        future.completeExceptionally(parseHttpError(response, null));
                        return;
                    }

                    try (ResponseBody responseBody = response.body()) {
                        var responseBodyString = responseBody != null ? responseBody.string() : "";

                        if (responseBodyString.isEmpty()) {
                            future.complete(null);
                            return;
                        }

                        T result = parseData(responseBodyString, responseClass);
                        future.complete(result);
                    }
                } catch (Exception e) {
                    // lỗi parse json hoặc không xác định
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }

    protected <Res> CompletableFuture<Res> sendGetRequest(
            String endpoint,
            Class<Res> responseClass,
            Consumer<Request.Builder> requestCustomizer) {
        return this.sendRequestWithoutBody(
                "GET",
                endpoint,
                responseClass,
                requestCustomizer
        );
    }

    protected <Req, Res> CompletableFuture<Res> sendPostRequest(
            String endpoint,
            Req bodyObject,
            Class<Res> responseClass,
            Consumer<Request.Builder> requestCustomizer) {
        return this.sendRequestWithBody(
                "POST",
                endpoint,
                bodyObject,
                responseClass,
                requestCustomizer);
    }

    protected <Req, Res> CompletableFuture<Res> sendPutRequest(
            String endpoint,
            Req bodyObject,
            Class<Res> responseClass,
            Consumer<Request.Builder> requestCustomizer) {
        return this.sendRequestWithBody(
                "PUT",
                endpoint,
                bodyObject,
                responseClass,
                requestCustomizer);
    }

    protected <Req, Res> CompletableFuture<Res> sendPatchRequest(
            String endpoint,
            Req bodyObject,
            Class<Res> responseClass,
            Consumer<Request.Builder> requestCustomizer) {
        return this.sendRequestWithBody(
                "PATCH",
                endpoint,
                bodyObject,
                responseClass,
                requestCustomizer
        );
    }

    protected <Res> CompletableFuture<Res> sendDeleteRequest(
            String endpoint,
            Class<Res> responseClass,
            Consumer<Request.Builder> requestCustomizer) {
        return this.sendRequestWithoutBody(
                "DELETE",
                endpoint,
                responseClass,
                requestCustomizer
        );
    }

    private <Res> CompletableFuture<Res> sendRequestWithoutBody(
            String method,
            String endpoint,
            Class<Res> responseClass,
            Consumer<Request.Builder> requestCustomizer) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(buildUrl(endpoint))
                    .method(method, null);
            if (requestCustomizer != null) {
                requestCustomizer.accept(builder);
            }
            return sendRequest(builder.build(), responseClass);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    ///  (POST, PUT, PATCH) gọi chung để gắn body vào
    private <Req, Res> CompletableFuture<Res> sendRequestWithBody(
            String method,
            String endpoint,
            Req bodyObject,
            Class<Res> responseClass,
            Consumer<Request.Builder> requestCustomizer) {
        try {
            RequestBody finalBody;
            if (bodyObject instanceof RequestBody) {
                finalBody = (RequestBody) bodyObject;
            } else {
                String json = jsonMapper.writeValueAsString(bodyObject);
                finalBody = RequestBody.create(json, JSON_MEDIA_TYPE);
            }

            Request.Builder builder = new Request.Builder()
                    .url(buildUrl(endpoint))
                    .method(method, finalBody);

            if (requestCustomizer != null) {
                requestCustomizer.accept(builder);
            }

            return sendRequest(builder.build(), responseClass);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
