package com.group4.projects_management.service;

import com.group4.common.dto.*;
import com.group4.common.enums.BusinessErrorCode;
import com.group4.projects_management.core.exception.BusinessException;
import com.group4.projects_management.core.security.JwtUtils;
import com.group4.projects_management.entity.AppRole;
import com.group4.projects_management.entity.User;
import com.group4.projects_management.mapper.UserMapper;
import com.group4.projects_management.repository.AppRoleRepository;
import com.group4.projects_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserMapper userMapper;
    @Mock private AppRoleRepository appRoleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setHashedPassword("encodedPassword");
        sampleUser.setActive(true);
        sampleUser.setEmail("test@example.com");
    }

    @Nested
    @DisplayName("Tests for login function")
    class LoginTests {

        @Test
        @DisplayName("Login thành công - Trả về AuthResponse")
        void login_Success() {
            // Given
            LoginRequest request = new LoginRequest("testuser", "password123");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(jwtUtils.generateToken("testuser")).thenReturn("mock-token");
            when(userMapper.toAuthResponse(anyString(), any(User.class))).thenReturn(new AuthResponse());

            // When
            AuthResponse response = userService.login(request);

            // Then
            assertThat(response).isNotNull();
            verify(jwtUtils).generateToken("testuser");
        }

        @Test
        @DisplayName("Login thất bại - User không tồn tại")
        void login_UserNotFound() {
            LoginRequest request = new LoginRequest("unknown", "pass");
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.AUTH_USER_NOT_FOUND);
        }

        @Test
        @DisplayName("Login thất bại - Tài khoản bị khóa")
        void login_AccountLocked() {
            sampleUser.setActive(false);
            LoginRequest request = new LoginRequest("testuser", "pass");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.AUTH_ACCOUNT_LOCKED);
        }

        @Test
        @DisplayName("Login thất bại - Sai mật khẩu")
        void login_InvalidPassword() {
            LoginRequest request = new LoginRequest("testuser", "wrongpass");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
            when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

            assertThatThrownBy(() -> userService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.AUTH_INVALID_PASSWORD);
        }
    }

    @Nested
    @DisplayName("Tests for register function")
    class RegisterTests {

        @Test
        @DisplayName("Đăng ký thành công")
        void register_Success() {
            UserRegistrationDTO dto = new UserRegistrationDTO();
            dto.setUsername("newuser");
            dto.setEmail("new@example.com");

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(appRoleRepository.getAppRoleBySystemCode("user")).thenReturn(new AppRole());
            when(userMapper.toEntity(dto)).thenReturn(sampleUser);
            when(userMapper.toDto(any())).thenReturn(new UserDTO());

            UserDTO result = userService.register(dto);

            assertThat(result).isNotNull();
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Đăng ký thất bại - Trùng Username")
        void register_UsernameExists() {
            UserRegistrationDTO dto = new UserRegistrationDTO();
            dto.setUsername("existingUser");
            when(userRepository.existsByUsername("existingUser")).thenReturn(true);

            assertThatThrownBy(() -> userService.register(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.AUTH_USERNAME_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("Tests for updateProfile function")
    class UpdateProfileTests {

        @Test
        @DisplayName("Cập nhật profile thành công")
        void updateProfile_Success() {
            UserUpdateDTO dto = new UserUpdateDTO();
            dto.setEmail("newemail@example.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
            when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
            when(userMapper.toDto(any())).thenReturn(new UserDTO());

            UserDTO result = userService.updateProfile(1L, dto);

            assertThat(result).isNotNull();
            verify(userMapper).updateEntityFromDto(dto, sampleUser);
            verify(userRepository).save(sampleUser);
        }
    }
}
