package com.group4.projects_management.mapper;

import com.group4.common.dto.ProjectCreateRequestDTO;
import com.group4.common.dto.ProjectResponseDTO;
import com.group4.common.dto.ProjectUpdateRequestDTO;
import com.group4.projects_management.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper
{
    @Mapping(source = "name", target = "projectName")
    @Mapping(source = "projectStatus.name", target = "statusName")
    @Mapping(target = "memberCount", expression = "java(project.getActiveMemberCount())")
    @Mapping(target = "userCreatedUsername", expression = "java(project.getCreatedBy().getUsername())")
    @Mapping(target = "userCreatedFullName", expression = "java(project.getCreatedBy().getFullName())")
    public abstract ProjectResponseDTO toDto(Project project);


    @Mapping(source = "projectName", target = "name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "projectStatus", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    public abstract Project toCreateEntity(ProjectCreateRequestDTO dto);

    @Mapping(source = "projectName", target = "name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "projectStatus", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateProjectFromDto(ProjectUpdateRequestDTO dto, @MappingTarget Project project);
}
