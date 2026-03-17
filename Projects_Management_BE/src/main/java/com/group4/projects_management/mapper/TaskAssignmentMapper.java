package com.group4.projects_management.mapper;

import com.group4.common.dto.TaskAssigneeDTO;
import com.group4.projects_management.entity.TaskAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class TaskAssignmentMapper {

    @Mapping(source = "assignee.id", target = "projectMemberId")
    @Mapping(source = "assignee.user.id", target = "userId")
    @Mapping(source = "assignee.user.fullName", target = "fullName")
    public abstract TaskAssigneeDTO toDTO(TaskAssignment taskAssignment);
}
