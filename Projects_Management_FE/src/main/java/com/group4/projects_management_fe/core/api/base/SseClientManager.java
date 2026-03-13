package com.group4.projects_management_fe.core.api.base;

import java.util.function.Consumer;

public interface SseClientManager<T> {
    void connect(Runnable onUnauthorized);
    void disconnect();
    void shutdown();

    Runnable subscribe(Consumer<T> onReceive, Consumer<Throwable> onError);
}
