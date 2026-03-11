package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.SseNotificationDTO;
import com.group4.projects_management_fe.core.api.base.AbstractSseManager;
import com.group4.projects_management_fe.core.exception.UnauthorizedException;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class StandardSseManager extends AbstractSseManager<SseNotificationDTO> {
    private EventSource eventSource;
    private int retryCount = 0;
    private Runnable globalOnUnauthorized;
    // Đang kết nối
    private boolean isConnecting = false;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final List<Consumer<SseNotificationDTO>> dataSubscribers = new CopyOnWriteArrayList<>();
    private final List<Consumer<Throwable>> errorSubscribers = new CopyOnWriteArrayList<>();

    protected StandardSseManager(AuthSessionProvider sessionProvider) {
        super(SseNotificationDTO.class, sessionProvider);
    }

    @Override
    public synchronized void connect(Runnable onUnauthorized) {
        if (this.eventSource != null || this.isConnecting) return;
        this.isConnecting = true;
        this.globalOnUnauthorized = onUnauthorized;

        Request request = new Request.Builder()
                .url(this.getUrl())
                .build();

        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                System.out.println("Kết nối SSE thành công!");
                retryCount = 0;
                isConnecting = false;
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
                if ("INIT".equals(type)) {
                    System.out.println("Server xác nhận: " + data);
                    return;
                }

                try {
                    var dto = parseData(data);
                    dataSubscribers.forEach(subscriber -> subscriber.accept(dto));
                } catch (Exception e) {
                    System.err.println("Lỗi parse JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                eventSource.cancel();
                StandardSseManager.this.eventSource = null;
                StandardSseManager.this.isConnecting = false;

                Throwable parsedError = parseHttpError(response, t);
                if (parsedError instanceof UnauthorizedException &&
                        globalOnUnauthorized != null) {
                    globalOnUnauthorized.run();
                    return;
                }

                errorSubscribers.forEach(subscriber -> subscriber.accept(parsedError));

                int delay = (int) Math.min(Math.pow(2, retryCount), MAX_RETRY_DELAY_MILLISECOND);
                retryCount++;

                System.err.println("Thử lại sau " + delay);
                scheduler.schedule(() -> connect(onUnauthorized), delay, TimeUnit.MILLISECONDS);
            }
        };
        this.eventSource = EventSources.createFactory(this.client).newEventSource(request, listener);
    }

    public synchronized void disconnect() {
        if (this.eventSource != null) {
            this.eventSource.cancel();
            this.eventSource = null;
        }
        this.globalOnUnauthorized = null;
        this.isConnecting = false;
    }

    @Override
    public Runnable subscribe(Consumer<SseNotificationDTO> onReceive, Consumer<Throwable> onError) {
        if (onReceive != null) this.dataSubscribers.add(onReceive);
        if (onError != null) this.errorSubscribers.add(onError);

        return () -> {
            this.dataSubscribers.remove(onReceive);
            this.errorSubscribers.remove(onError);
        };
    }

    @Override
    protected void onCustomShutdown() {
        scheduler.shutdownNow();
        dataSubscribers.clear();
        errorSubscribers.clear();
    }
}
