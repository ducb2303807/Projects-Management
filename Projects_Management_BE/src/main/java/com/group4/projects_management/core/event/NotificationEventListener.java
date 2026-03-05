package com.group4.projects_management.core.event;

import com.group4.projects_management.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("DB đã commit xong. Bắt đầu đẩy SSE cho user {}", event.userId());

        // Gửi qua SSE với tên sự kiện là "NOTIFICATION_EVENT"
        sseService.send(event.userId(), "NOTIFICATION_EVENT", event.notificationDTO());
    }
}
