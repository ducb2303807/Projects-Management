package com.group4.projects_management.mapper;

import com.group4.common.dto.TaskHistoryDTO;
import com.group4.projects_management.entity.TaskHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class TaskHistoryMapper {

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "changedById", source = "changedBy.id")
    @Mapping(target = "changedByUsername", source = "changedBy.user.username")
    @Mapping(target = "changedByFullName", source = "changedBy.user.fullName")
    public abstract TaskHistoryDTO toDto(TaskHistory taskHistory);
}
