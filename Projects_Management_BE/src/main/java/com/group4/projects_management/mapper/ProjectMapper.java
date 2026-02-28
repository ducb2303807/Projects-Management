package com.group4.projects_management.mapper;

import com.group4.common.dto.ProjectCreateRequestDTO;
import com.group4.common.dto.ProjectResponseDTO;
import com.group4.projects_management.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper
{
    @Mapping(source = "name", target = "projectName")
    @Mapping(source = "projectStatus.name", target = "statusName")
    @Mapping(target = "memberCount", expression = "java(project.getMemberCount())")
    @Mapping(target = "userCreatedUsername", expression = "java(project.getCreatedBy().getUsername())")
    @Mapping(target = "userCreatedFullName", expression = "java(project.getCreatedBy().getFullName())")
    public abstract ProjectResponseDTO toDto(Project project);


    @Mapping(source = "projectName", target = "name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "projectStatus", ignore = true)
    public abstract Project toCreateEntity(ProjectCreateRequestDTO dto);
}
