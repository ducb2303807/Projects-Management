package com.group4.projects_management.mapper;

import com.group4.common.dto.InvitationDTO;
import com.group4.common.dto.ProjectMemberDTO;
import com.group4.projects_management.entity.ProjectMember;
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
    public abstract ProjectMemberDTO toDto(ProjectMember projectMember);

    // TODO
    public abstract InvitationDTO toInvitationDto(ProjectMember projectMember);

}
