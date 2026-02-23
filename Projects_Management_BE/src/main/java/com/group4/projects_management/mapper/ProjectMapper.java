package com.group4.projects_management.mapper;

import com.group4.common.dto.ProjectResponseDTO;
import com.group4.projects_management.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper
{

    @Mapping(source = "name", target = "projectName")
    @Mapping(source = "projectStatus.name", target = "statusName")
    @Mapping(source = "startDate", target = "startAt")
    @Mapping(source = "endDate", target = "endAt")
    @Mapping(target = "memberCount", expression = "java(project.getMembers().size())") // int getActiveMember() đợi thêm
    public abstract ProjectResponseDTO toDto(Project project);
}
