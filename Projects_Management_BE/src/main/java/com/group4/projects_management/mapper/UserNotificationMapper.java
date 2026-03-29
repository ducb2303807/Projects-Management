package com.group4.projects_management.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class UserNotificationMapper {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(source = "notification.id", target = "id")
    @Mapping(source = "notification.title", target = "title")
    @Mapping(source = "notification.type", target = "type")
    @Mapping(source = "notification.referenceId", target = "referenceId", qualifiedByName = "stringToLong")
    @Mapping(source = "notification.createdAt", target = "createdDate")
    @Mapping(source = "read", target = "isRead")
    @Mapping(source = "notification.metadata", target = "metadata", qualifiedByName = "jsonToMap")
    public abstract NotificationDTO toDto(UserNotification userNotification);

    @Named("jsonToMap")
    protected Map<String, Object> jsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            // Log lỗi nếu cần thiết
            System.err.println("Error parsing notification metadata: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    @Named("stringToLong")
    protected Long stringToLong(String referenceId) {
        if (referenceId == null || referenceId.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(referenceId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}