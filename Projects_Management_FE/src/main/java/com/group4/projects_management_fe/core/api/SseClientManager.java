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

import java.util.concurrent.TimeUnit;

public class SseClientManager {
    private final JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private EventSource eventSource;

    public void startListening(Long userId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:8080/api/notifications/subscribe/" + userId)
                // .header("Authorization", "Bearer " + token) // Nếu có JWT
                .build();

        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
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
                System.err.println("Mất kết nối SSE. Thử lại sau 5 giây...");
                // Logic reconnect có thể đặt ở đây
            }
        };

        this.eventSource = EventSources.createFactory(client).newEventSource(request, listener);
    }

    public void stopListening() {
        if (eventSource != null) eventSource.cancel();
    }
}
