package com.group4.projects_management.core.strategy.notification.deadline;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskOverdueStrategy implements NotificationStrategy<TaskOverdueContext> {
    @Override
    public boolean supports(Class<?> clazz) { return TaskOverdueContext.class.equals(clazz); }

    @Override
    public NotificationType getType() { return NotificationType.TASK_OVERDUE; }

    @Override
    public String buildTitle(TaskOverdueContext ctx) {
        return String.format("WARNING: Task '%s' is overdue!", ctx.getTask().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(TaskOverdueContext ctx) {
        return Map.of("taskId", ctx.getTask().getId(), "overdueSince", ctx.getTask().getDeadline());
    }
}
