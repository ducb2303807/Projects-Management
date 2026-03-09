package com.group4.projects_management.service;

import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {
    private final UserRepository userRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SseEmitter createEmitter(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        SseEmitter emitter = new SseEmitter(0L);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected successfully"));
        }
        catch (IOException e) {
            log.error("Error sending INIT event to user {}: {}", userId, e.getMessage());
            emitter.complete();
            return null;
        }

        // chỉ 1 user id kết nối sse 1 lúc
        if (emitters.containsKey(userId)) {
            SseEmitter oldEmitter = emitters.get(userId);
            oldEmitter.complete();
        }

        emitters.put(userId, emitter);
        log.info("SSE connection established for user {}", userId);
        return emitter;
    }

    /**
     * @param userId ID người nhận
     * @param eventName Tên sự kiện (ví dụ: "task", "comment", "system")
     * @param data Dữ liệu bất kỳ
     */
    public void send(Long userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data)
                        .reconnectTime(5000));
            } catch (IOException e) {
                log.error("Error sending SSE to user {}: {}", userId, e.getMessage());
                emitter.complete();
                emitters.remove(userId);
            }
        } else {
            log.debug("No active SSE connection for user {}", userId);
        }
    }
}
