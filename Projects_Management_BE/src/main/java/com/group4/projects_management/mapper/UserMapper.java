package com.group4.projects_management.mapper;

import com.group4.common.dto.AuthResponse;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.common.dto.UserUpdateDTO;
import com.group4.projects_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Mapping(source = "appRole.name", target = "systemRoleName")
    public abstract UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashedPassword", expression = "java(passwordEncoder.encode(userDTO.getPassword()))")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "appRole", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "userNotification", ignore = true)
    public abstract User toEntity(UserRegistrationDTO userDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashedPassword", ignore = true)
    @Mapping(target = "appRole", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "userNotification", ignore = true)
    public abstract void updateEntityFromDto(UserUpdateDTO dto, @MappingTarget User user);

    public AuthResponse toAuthResponse(String token, User user) {
        return new AuthResponse(token, toDto(user));
    }
}
