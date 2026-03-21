package com.group4.projects_management.core.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.common.dto.NotificationDTO;
import com.group4.common.dto.SseNotificationDTO;
import com.group4.projects_management.enums.NotificationType;
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
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("DB đã commit xong. Bắt đầu đẩy SSE cho user {}", event.userId());
        NotificationDTO dbDto = event.notificationDTO();

        String friendlyTitle = "Thông báo";
        try {
            NotificationType typeEnum = NotificationType.valueOf(dbDto.getType());
            friendlyTitle = typeEnum.getDisplayTitle();
        } catch (Exception e) {
            log.warn("Không tìm thấy Enum cho type: {}", dbDto.getType());
        }

        Object metadataJson;
        try {
            metadataJson = objectMapper.readTree(dbDto.getMetadata());
        } catch (Exception e) {
            metadataJson = null;
        }

        SseNotificationDTO ssePayload = SseNotificationDTO.builder()
                .notificationId(dbDto.getId())
                .type(dbDto.getType())
                .title(friendlyTitle)
                .message(dbDto.getTitle())
                .referenceId(dbDto.getReferenceId() != null ? dbDto.getReferenceId().toString() : null)
                .metadata(metadataJson)
                .timestamp(dbDto.getCreatedDate())
                .build();

        // Gửi qua SSE với tên sự kiện là "NOTIFICATION_EVENT"
        sseService.send(event.userId(), "NOTIFICATION_EVENT", ssePayload);
    }
}
