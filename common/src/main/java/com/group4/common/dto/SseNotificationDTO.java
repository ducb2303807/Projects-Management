package com.group4.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SseNotificationDTO {
    private Long notificationId;
    private String title;
    private String type;
    private String message;
    private Object metadata;
    private String referenceId;
    private LocalDateTime timestamp;
}
