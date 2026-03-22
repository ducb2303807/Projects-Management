package com.group4.projects_management.core.strategy.notification.invitation;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MemberRemovedStrategy implements NotificationStrategy<MemberRemovedContext> {
    @Override
    public boolean supports(Class<?> clazz) {
        return MemberRemovedContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.MEMBER_REMOVED;
    }

    @Override
    public String buildTitle(MemberRemovedContext ctx) {
        return String.format("You have been removed from project '%s'",
                ctx.getProject().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(MemberRemovedContext ctx) {
        return Map.of("projectId", ctx.getProject().getId());
    }
}