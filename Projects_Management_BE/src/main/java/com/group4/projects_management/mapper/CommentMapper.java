package com.group4.projects_management.mapper;

import com.group4.common.dto.CommentDTO;
import com.group4.projects_management.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {
    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "userName", source = "member.user.username")
    @Mapping(target = "fullName", source = "member.user.fullName")
    public abstract CommentDTO toDto(Comment comment);
}
