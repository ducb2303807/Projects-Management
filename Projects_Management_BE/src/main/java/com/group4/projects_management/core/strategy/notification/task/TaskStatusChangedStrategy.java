package com.group4.projects_management.core.strategy.notification.task;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskStatusChangedStrategy implements NotificationStrategy<TaskStatusContext> {
    @Override
    public boolean supports(Class<?> clazz) {
        return TaskStatusContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.TASK_STATUS_CHANGED;
    }

    @Override
    public String buildTitle(TaskStatusContext ctx) {
        return String.format("Task '%s' status has been changed to: %s",
                ctx.getTask().getName(), ctx.getNewStatusName());
    }

    @Override
    public Map<String, Object> buildMetadata(TaskStatusContext ctx) {
        return Map.of("taskId", ctx.getTask().getId(), "newStatus", ctx.getNewStatusName());
    }
}
