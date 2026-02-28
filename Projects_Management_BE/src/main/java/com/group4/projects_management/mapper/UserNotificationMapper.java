package com.group4.projects_management.mapper;

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserNotificationMapper {
    @Mapping(source = "notification.id", target = "id")
    @Mapping(source = "notification.text",target = "text")
    @Mapping(source = "notification.type", target = "type")
    @Mapping(source = "notification.referenceId", target = "referenceId")
    @Mapping(source = "notification.createdAt", target = "createdDate")
    public abstract NotificationDTO toDto(UserNotification notification);
}
