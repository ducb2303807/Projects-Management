package com.group4.projects_management_fe.core.api.base;

import com.group4.projects_management_fe.core.api.config.ApiConfig;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSseManager<T> extends BaseNetworkCore implements SseClientManager<T> {
    protected final String endpoint =  "/notifications/subscribe/me";
    protected final int MAX_RETRY_DELAY_MILLISECOND = (int) ApiConfig.APP_TIMEOUT_MS;
    protected final OkHttpClient client;
    protected final Class<T> responseType;


    protected AbstractSseManager(Class<T> responseType, AuthSessionProvider sessionProvider) {
        this.responseType = responseType;

        var clientBuilder =  super.getHttpClient().newBuilder()
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
}
