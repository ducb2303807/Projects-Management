package com.group4.projects_management.core.strategy.notification;

import com.group4.projects_management.enums.NotificationType;

import java.util.Map;

public interface NotificationStrategy<T> {
    boolean supports(Class<?> contextClass);

    NotificationType getType();

    String buildTitle(T context);

    Map<String, Object> buildMetadata(T context);
}
