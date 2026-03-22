package com.group4.projects_management.core.strategy.notification.invitation;

import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MemberLeftStrategy implements NotificationStrategy<MemberLeftContext> {

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberLeftContext.class.equals(clazz);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.MEMBER_LEFT;
    }

    @Override
    public String buildTitle(MemberLeftContext ctx) {
        return String.format("%s has left project '%s'",
                ctx.getLeaver().getUsername(),
                ctx.getProject().getName());
    }

    @Override
    public Map<String, Object> buildMetadata(MemberLeftContext ctx) {
        return Map.of(
                "projectId", ctx.getProject().getId(),
                "leaverId", ctx.getLeaver().getId(),
                "leaverName", ctx.getLeaver().getUsername()
        );
    }
}