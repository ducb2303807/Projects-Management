package com.group4.projects_management.core.strategy.notification.deadline;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskDeadlineApproachingStrategy implements NotificationStrategy<TaskDeadlineApproachingContext> {
    @Override
    public boolean supports(Class<?> clazz) { return TaskDeadlineApproachingContext.class.equals(clazz); }

    @Override
    public NotificationType getType() { return NotificationType.TASK_DEADLINE_APPROACHING; }

    @Override
    public String buildTitle(TaskDeadlineApproachingContext ctx) {
        return String.format("Task '%s' is due tomorrow!", ctx.getTask().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(TaskDeadlineApproachingContext ctx) {
        return Map.of("taskId", ctx.getTask().getId(), "deadline", ctx.getTask().getDeadline());
    }
}