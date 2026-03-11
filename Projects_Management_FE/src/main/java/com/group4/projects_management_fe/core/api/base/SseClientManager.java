package com.group4.projects_management_fe.core.api.base;

import java.util.function.Consumer;

public interface SseClientManager<T> {
    void startListening(Consumer<T> onReceive, Consumer<Throwable> onError, Runnable onUnauthorized);
    void stopListening();
    void shutdown();
}
