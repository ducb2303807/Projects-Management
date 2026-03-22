package com.group4.projects_management.core.strategy.notification.task;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskUpdatedStrategy implements NotificationStrategy<TaskUpdateContext> {

    @Override
    public boolean supports(Class<?> clazz) {
        return TaskUpdateContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.TASK_UPDATED;
    }

    @Override
    public String buildTitle(TaskUpdateContext ctx) {
        return ctx.getActor() != null ?
                String.format("%s has updated task: '%s'", ctx.getActor().getUsername(), ctx.getTask().getName()) :
                String.format("Task '%s' has been updated", ctx.getTask().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(TaskUpdateContext ctx) {
        return Map.of(
                "taskId", ctx.getTask().getId(),
                "projectId", ctx.getTask().getProject().getId(),
                "updatedBy", ctx.getActor() != null ? ctx.getActor().getUsername() : "System"
        );
    }
}
