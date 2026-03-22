package com.group4.projects_management.core.strategy.notification.project;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProjectDeletedStrategy implements NotificationStrategy<ProjectDeleteContext> {

    @Override
    public boolean supports(Class<?> clazz) { return ProjectDeleteContext.class.equals(clazz); }

    @Override
    public NotificationType getType() { return NotificationType.PROJECT_DELETED; }

    @Override
    public String buildTitle(ProjectDeleteContext ctx) {
        return ctx.getActor() != null ?
                String.format("%s has deleted project: '%s'", ctx.getActor().getUsername(), ctx.getProjectName()) :
                String.format("project '%s' has been deleted", ctx.getProjectName());
    }

    @Override
    public Map<String, Object> buildMetadata(ProjectDeleteContext ctx) {
        return Map.of("projectId", ctx.getProjectName());
    }
}