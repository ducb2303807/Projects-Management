package com.group4.projects_management.core.strategy.notification.taskassignment;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskAssignedStrategy implements NotificationStrategy<TaskAssignContext> {
    @Override
    public boolean supports(Class<?> clazz) {
        return TaskAssignContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.TASK_ASSIGNED;
    }

    @Override
    public String buildTitle(TaskAssignContext ctx) {
        return String.format("%s đã giao cho bạn công việc: %s",
                ctx.getAssigner().getUsername(),
                ctx.getTask().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(TaskAssignContext ctx) {
        return Map.of(
                "taskId", ctx.getTask().getId(),
                "projectId", ctx.getTask().getProject().getId(),
                "assignerId", ctx.getAssigner().getId()
        );
    }
}
