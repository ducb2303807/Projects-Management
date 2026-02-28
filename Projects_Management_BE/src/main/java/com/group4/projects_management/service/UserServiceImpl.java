package com.group4.projects_management.service; /***********************************************************************
 * Module:  UserServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserServiceImpl
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.common.enums.BusinessErrorCode;
import com.group4.projects_management.core.exception.BusinessException;
import com.group4.projects_management.core.security.JwtUtils;
import com.group4.projects_management.entity.User;
import com.group4.projects_management.mapper.UserMapper;
import com.group4.projects_management.repository.AppRoleRepository;
import com.group4.projects_management.repository.UserRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @pdOid 5c7fa502-42c5-4869-8a02-2c98dd204c57
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final AppRoleRepository appRoleRepository;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils, UserMapper userMapper, AppRoleRepository appRoleRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.appRoleRepository = appRoleRepository;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại", BusinessErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException("Tài khoản của bạn đã bị khóa!",BusinessErrorCode.ACCOUNT_LOCKED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getHashedPassword())) {
            throw new BusinessException("Sai mật khẩu", BusinessErrorCode.INVALID_PASSWORD);
        }

        var token = jwtUtils.generateToken(user.getUsername());

        return userMapper.toAuthResponse(token, user);
    }

    @Override
    @Transactional
    public UserDTO register(UserRegistrationDTO dto)
    {
       if (existsByUsername(dto.getUsername())) {
           throw new BusinessException("Username đã tồn tại!",BusinessErrorCode.USERNAME_ALREADY_EXISTS);
       }
       if (existsByEmail(dto.getEmail())) {
           throw new BusinessException("Email đã tồn tại", BusinessErrorCode.EMAIL_ALREADY_EXISTS);
       }

        var role = appRoleRepository.getAppRoleBySystemCode("user");

        var user = userMapper.toEntity(dto);
        user.setAppRole(role);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long userId, UserUpdateDTO dto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", BusinessErrorCode.USER_NOT_FOUND));

        if (dto.getEmail() != null && existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email đã tồn tại", BusinessErrorCode.EMAIL_ALREADY_EXISTS);
        }

        userMapper.updateEntityFromDto(dto, user);

        userRepository.save(user);
        return userMapper.toDto(user);
    }

//    @Override
//    @Transactional
//    public void changePassword(Long userId, String oldPassword, String newPassword) {
//        var user = userRepository.findById(userId)
//                .orElseThrow(() -> new BusinessException("User not found", BusinessErrorCode.USER_NOT_FOUND));
//
//        if (!passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
//            throw new BusinessException("old password is incorrect", BusinessErrorCode.INVALID_PASSWORD);
//        }
//        user.setHashedPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//    }

    @Override
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", BusinessErrorCode.USER_NOT_FOUND));

        user.setHashedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        return List.of();
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }
}