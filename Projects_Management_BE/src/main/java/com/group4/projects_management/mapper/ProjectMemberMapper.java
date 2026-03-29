package com.group4.projects_management.mapper;

import com.group4.common.dto.InvitationDTO;
import com.group4.common.dto.ProjectMemberDTO;
import com.group4.projects_management.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProjectMemberMapper {

    @Mapping(source = "id", target = "projectMemberId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "joinAt", target = "joinDate")
    @Mapping(source = "projectRole.name", target = "roleName")
    @Mapping(source = "projectMemberStatus.name", target = "statusName")
    @Mapping(source = "projectRole.systemCode", target = "roleCode")
    @Mapping(source = "projectMemberStatus.systemCode", target = "statusCode")
    public abstract ProjectMemberDTO toDto(ProjectMember projectMember);

    @Mapping(source = "id", target = "projectMemberId")
    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "invitedBy.user.fullName", target = "inviterName")
    @Mapping(source = "invitedAt", target = "sentAt")
    @Mapping(source = "projectRole.name", target = "roleName")
    @Mapping(source = "projectMemberStatus.name", target = "statusName")
    @Mapping(source = "projectRole.systemCode", target = "roleCode")
    @Mapping(source = "projectMemberStatus.systemCode", target = "statusCode")
    public abstract InvitationDTO toInvitationDto(ProjectMember projectMember);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "project")
    @Mapping(target = "user", source = "creator")
    @Mapping(target = "projectRole", source = "ownerRole")
    @Mapping(target = "projectMemberStatus", source = "activeMemberStatus")
    @Mapping(target = "invitedBy", ignore = true)
    @Mapping(target = "invitedAt", ignore = true)
    @Mapping(target = "joinAt", expression = "java(java.time.LocalDateTime.now())")
    public abstract ProjectMember createInitialMember(Project project, User creator, ProjectRole ownerRole, ProjectMemberStatus activeMemberStatus);
}
