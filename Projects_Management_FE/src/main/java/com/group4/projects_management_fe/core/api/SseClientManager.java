package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.NotificationDTO;
import javafx.application.Platform;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SseClientManager {
    private final JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private EventSource eventSource;
    private static final int MAX_RETRY_DELAY = 60;
    private int retryCount = 0;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();

    public void startListening(Long userId) {
        stopListening();

        Request request = new Request.Builder()
                .url("http://localhost:8080/api/notifications/subscribe/" + userId)
                // .header("Authorization", "Bearer " + token) // Nếu có JWT
                .build();

        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                System.out.println("Kết nối SSE thành công!");
                retryCount = 0;
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("INIT".equals(type)) {
                    System.out.println("Server xác nhận: " + data);
                    return;
                }

                try {
                    NotificationDTO notificationDTO = jsonMapper.readValue(data, NotificationDTO.class);
                    Platform.runLater(() -> {
                        System.out.println("Thông báo mới: " + notificationDTO.getText());

                        // update UI
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                System.err.println("Lỗi SSE: " + t.getMessage());
                eventSource.cancel();

                int delay = (int) Math.min(Math.pow(2, retryCount), MAX_RETRY_DELAY);
                retryCount++;

                System.err.println("Thử lại sau " + delay);

                scheduler.schedule(() -> startListening(userId), delay, TimeUnit.SECONDS);
            }
        };

        this.eventSource = EventSources.createFactory(this.client).newEventSource(request, listener);
    }

    public void stopListening() {
        if (this.eventSource != null) {
            this.eventSource.cancel();
            this.eventSource = null;
        }
    }

    public void shutdown() {
        stopListening();
        scheduler.shutdown();
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();

        if (client.cache() != null) {
            try { client.cache().close(); } catch (Exception ignored) {}
        }
    }
}
