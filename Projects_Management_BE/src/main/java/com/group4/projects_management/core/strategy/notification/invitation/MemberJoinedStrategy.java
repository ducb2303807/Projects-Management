package com.group4.projects_management.core.strategy.notification.invitation;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MemberJoinedStrategy implements NotificationStrategy<MemberJoinContext> {
    @Override
    public boolean supports(Class<?> clazz) { return MemberJoinContext.class.equals(clazz); }

    @Override
    public NotificationType getType() { return NotificationType.MEMBER_JOINED; }

    @Override
    public String buildTitle(MemberJoinContext ctx) {
        return String.format("%s has joined project '%s'",
                ctx.getNewMember().getUsername(), ctx.getProject().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(MemberJoinContext ctx) {
        return Map.of("projectId", ctx.getProject().getId(), "userId", ctx.getNewMember().getId());
    }
}
