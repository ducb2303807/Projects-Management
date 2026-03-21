package com.group4.projects_management.core.strategy.notification.comment;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NewCommentStrategy implements NotificationStrategy<CommentNotificationContext> {
    @Override
    public boolean supports(Class<?> clazz) {
        return CommentNotificationContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.NEW_COMMENT;
    }

    @Override
    public String buildTitle(CommentNotificationContext ctx) {
        // Nếu có parentAuthor và người đó không phải là chính người đang comment
        if (ctx.getParentAuthor() != null) {
            return String.format("%s replied to your comment on task: '%s'",
                    ctx.getAuthor().getUsername(),
                    ctx.getTask().getName());
        }

        // Ngược lại là bình luận mới cho cả task
        return String.format("%s has commented on task: '%s'",
                ctx.getAuthor().getUsername(),
                ctx.getTask().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(CommentNotificationContext ctx) {
        return Map.of(
                "taskId", ctx.getTask().getId(),
                "projectId", ctx.getTask().getProject().getId(),
                "isReply", ctx.getParentAuthor() != null
        );
    }
}
