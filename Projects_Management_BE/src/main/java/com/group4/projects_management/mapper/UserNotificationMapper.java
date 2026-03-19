package com.group4.projects_management.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserNotificationMapper {
    @Mapping(source = "notification.id", target = "id")
    @Mapping(source = "notification.title",target = "title")
    @Mapping(source = "notification.type", target = "type")
    @Mapping(source = "notification.referenceId", target = "referenceId")
    @Mapping(source = "notification.createdAt", target = "createdDate")
    @Mapping(source = "read", target = "isRead")
    @Mapping(source = "notification.metadata", target = "metadata")
    public abstract NotificationDTO toDto(UserNotification notification);

    protected NotificationDTO.Metadata map(String metadataJson) {
        if (metadataJson == null) return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(metadataJson, NotificationDTO.Metadata.class);
        } catch (Exception e) {
            return null;
        }
    }
}
