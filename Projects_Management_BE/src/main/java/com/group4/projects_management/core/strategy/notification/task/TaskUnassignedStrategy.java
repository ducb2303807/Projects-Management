package com.group4.projects_management.core.strategy.notification.task;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskUnassignedStrategy implements NotificationStrategy<TaskUnassignContext> {
    @Override
    public boolean supports(Class<?> clazz) {
        return TaskUnassignContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.TASK_UNASSIGNED;
    }

    @Override
    public String buildTitle(TaskUnassignContext ctx) {
        return String.format("%s has removed you from task: '%s'",
                ctx.getActor().getUsername(),
                ctx.getTask().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(TaskUnassignContext ctx) {
        return Map.of("taskId", ctx.getTask().getId(),
                "projectId", ctx.getTask().getProject().getId());
    }
}
