package com.group4.projects_management.mapper;

import com.group4.common.dto.TaskHistoryDTO;
import com.group4.projects_management.entity.TaskHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class TaskHistoryMapper {
    @Mapping(source = "changedBy.user.username", target = "changedBy")
    public abstract TaskHistoryDTO toDto(TaskHistory taskHistory);
}
