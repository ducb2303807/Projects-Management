package com.group4.projects_management.core.strategy.notification.project;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProjectUpdatedStrategy implements NotificationStrategy<ProjectUpdatedContext> {
    @Override
    public boolean supports(Class<?> clazz) { return ProjectUpdatedContext.class.equals(clazz); }

    @Override
    public NotificationType getType() { return NotificationType.PROJECT_UPDATED; }

    @Override
    public String buildTitle(ProjectUpdatedContext ctx) {
        return ctx.getActor() != null ?
                String.format("%s has updated project: '%s'", ctx.getActor().getUsername(), ctx.getProject().getName()) :
                String.format("Project '%s' has been updated", ctx.getProject().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(ProjectUpdatedContext ctx) {
        return Map.of("projectId", ctx.getProject().getId());
    }
}
