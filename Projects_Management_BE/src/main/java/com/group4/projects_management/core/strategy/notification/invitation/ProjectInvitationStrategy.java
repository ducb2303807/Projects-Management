package com.group4.projects_management.core.strategy.notification.invitation;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProjectInvitationStrategy implements NotificationStrategy<ProjectInvitationContext> {
    @Override
    public boolean supports(Class<?> clazz) {
        return ProjectInvitationContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.PROJECT_INVITATION;
    }

    @Override
    public String buildTitle(ProjectInvitationContext ctx) {
        return String.format("%s has invited you to project '%s'",
                ctx.getInviter().getUsername(),
                ctx.getProject().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(ProjectInvitationContext ctx) {
        return Map.of(
                "projectId", ctx.getProject().getId(),
                "projectName", ctx.getProject().getName(),
                "roleName", ctx.getRole().getName(),
                "inviterName", ctx.getInviter().getUsername()
        );
    }
}
