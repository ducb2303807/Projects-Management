package com.group4.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class SseNotificationDTO {
    private String type;      // TASK_UPDATED, COMMENT_ADDED, PROJECT_INVITATION, v.v.
    private String message;   // Nội dung hiển thị nhanh
    private Object data;      // Object chứa chi tiết (TaskDTO, CommentDTO...)
    private LocalDateTime timestamp;
}
