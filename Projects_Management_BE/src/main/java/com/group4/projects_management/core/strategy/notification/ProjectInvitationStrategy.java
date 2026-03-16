package com.group4.projects_management.core.strategy.notification;

import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class ProjectInvitationStrategy implements NotificationStrategy<ProjectInviteContext> {
    @Override
    public boolean supports(Class<?> clazz) {
        return ProjectInviteContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.PROJECT_INVITATION;
    }

    @Override
    public String buildTitle(ProjectInviteContext ctx) {
        return String.format("%s đã mời bạn vào dự án %s",
                ctx.getInviter().getFullName(),
                ctx.getProject().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(ProjectInviteContext ctx) {
        return Map.of(
                "projectId", ctx.getProject().getId(),
                "projectName", ctx.getProject().getName(),
                "roleName", ctx.getRole().getName(),
                "inviterName", ctx.getInviter().getFullName()
        );
    }
}
