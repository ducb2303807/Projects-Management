package com.group4.projects_management.service;

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.entity.Notification;
import com.group4.projects_management.entity.User;
import com.group4.projects_management.entity.UserNotification;
import com.group4.projects_management.mapper.UserNotificationMapper;
import com.group4.projects_management.repository.NotificationRepository;
import com.group4.projects_management.repository.UserNotificationRepository;
import com.group4.projects_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserNotificationRepository userNotificationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImp notificationService;

    @Mock
    private UserNotificationMapper userNotificationMapper;

    private User mockUser;
    private Notification mockNotification;
    private UserNotification mockUserNotification;
    private NotificationDTO mockDto;



    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);


        mockNotification = new Notification();
        mockNotification.setId(100L);


        mockUserNotification = new UserNotification();
        mockUserNotification.setUser(mockUser);
        mockUserNotification.setNotification(mockNotification);


        mockDto = new NotificationDTO();
    }

    @Nested
    @DisplayName("Tests getNotificationsForUser")
    class GetNotificationsTests {
        @Test
        @DisplayName("getNotificationsForUser - Success")
        void getNotificationsForUser_Success() {
            when(userNotificationRepository.findAllByUserIdWithNotification(1L))
                    .thenReturn(List.of(mockUserNotification));
            when(userNotificationMapper.toDto(mockUserNotification)).thenReturn(mockDto);

            List<NotificationDTO> result = notificationService.getNotificationsForUser(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(userNotificationRepository).findAllByUserIdWithNotification(1L);
        }

        @Test
        @DisplayName("getNotificationsForUser - User has zero notifications")
        void getNotificationsForUser_EmptyList_ShouldReturnEmptyList() {
            when(userNotificationRepository.findAllByUserIdWithNotification(1L))
                    .thenReturn(Collections.emptyList());

            List<NotificationDTO> result = notificationService.getNotificationsForUser(1L);

            assertTrue(result.isEmpty());
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Tests markAsRead")
    class MarkAsReadTests {
        @Test
        @DisplayName("Mark as read - Success")
        void markAsRead_Success() {
            when(userNotificationRepository.findByUser_IdAndNotification_Id(1L, 100L))
                    .thenReturn(Optional.of(mockUserNotification));

            notificationService.markAsRead(100L, 1L);

            verify(userNotificationRepository).save(mockUserNotification);

            assertTrue(mockUserNotification.isRead());
        }

        @Test
        @DisplayName("Mark as read - Notification not found")
        void markAsRead_NotFound_ThrowsException() {
            when(userNotificationRepository.findByUser_IdAndNotification_Id(1L, 100L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    notificationService.markAsRead(100L, 1L)
            );
        }
    }

    @Nested
    @DisplayName("Tests markAllAsRead")
    class MarkAllAsReadTests {
        @Test
        @DisplayName("Mark all as read - Success")
        void markAllAsRead_Success() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userNotificationRepository.findAllByUser_Id(1L))
                    .thenReturn(List.of(mockUserNotification));

            notificationService.markAllAsRead(1L);

            verify(userNotificationRepository).saveAll(anyList());

            assertTrue(mockUserNotification.isRead());
        }

        @Test
        @DisplayName("Mark all as read - User not found")
        void markAllAsRead_UserNotFound_ThrowsException() {
            when(userRepository.existsById(1L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () ->
                    notificationService.markAllAsRead(1L)
            );
        }

        @Test
        @DisplayName("markAsRead - Should not save if already read")
        void markAsRead_AlreadyRead_ShouldNotSave() {
            mockUserNotification.setRead(true);
            when(userNotificationRepository.findByUser_IdAndNotification_Id(1L, 100L))
                    .thenReturn(Optional.of(mockUserNotification));

            notificationService.markAsRead(100L, 1L);

            verify(userNotificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("markAllAsRead - Should not save if all are already read")
        void markAllAsRead_AllAlreadyRead_ShouldNotSave() {
            mockUserNotification.setRead(true);
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userNotificationRepository.findAllByUser_Id(1L)).thenReturn(List.of(mockUserNotification));

            notificationService.markAllAsRead(1L);

            verify(userNotificationRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("sendNotification - Partial match - Should continue for existing users")
        void sendNotification_PartialMatch_ShouldContinue() {
            List<Long> requestIds = List.of(1L, 999L);
            when(userRepository.findAllById(requestIds)).thenReturn(List.of(mockUser));
            when(userNotificationMapper.toDto(any())).thenReturn(new NotificationDTO());

            notificationService.sendNotification(requestIds, "Partial Match", "TYPE", 1L);

            verify(notificationRepository).save(any());

            ArgumentCaptor<List<UserNotification>> captor = ArgumentCaptor.forClass(List.class);
            verify(userNotificationRepository).saveAll(captor.capture());
            assertEquals(1, captor.getValue().size());
        }

        @Test
        @DisplayName("sendNotification - Null or Empty content - Should throw exception")
        void sendNotification_InvalidInput_ShouldThrowException() {
            List<Long> userIds = List.of(1L);

            assertThrows(IllegalArgumentException.class, () ->
                    notificationService.sendNotification(userIds, "", "TYPE", 1L)
            );

            assertThrows(IllegalArgumentException.class, () ->
                    notificationService.sendNotification(userIds, null, "TYPE", 1L)
            );
        }
    }

    @Nested
    @DisplayName("Tests for sendNotification")
    class SendNotificationTests {

        @Test
        @DisplayName("Send to single user - Success")
        void sendNotification_Single_Success() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(userNotificationMapper.toDto(any())).thenReturn(mockDto);

            notificationService.sendNotification(1L, "Content", "TYPE", 500L);

            verify(notificationRepository).save(any(Notification.class));
            verify(userNotificationRepository).save(any(UserNotification.class));
            verify(eventPublisher).publishEvent(mockDto);
        }

        @Test
        @DisplayName("Send to single user - User Not Found")
        void sendNotification_Single_UserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    notificationService.sendNotification(1L, "Content", "TYPE", 500L)
            );
        }

        @Test
        @DisplayName("Send to multiple users - Success")
        void sendNotification_Multiple_Success() {
            List<Long> userIds = List.of(1L, 2L);
            User user2 = new User(); user2.setId(2L);

            when(userRepository.findAllById(userIds)).thenReturn(List.of(mockUser, user2));
            when(userNotificationMapper.toDto(any())).thenReturn(mockDto);

            notificationService.sendNotification(userIds, "Bulk Content", "BULK", 600L);

            verify(notificationRepository, times(1)).save(any(Notification.class));
            verify(userNotificationRepository).saveAll(anyList());
            // Event phải được bắn 2 lần (cho 2 user)
            verify(eventPublisher, times(2)).publishEvent(any(NotificationDTO.class));
        }

        @Test
        @DisplayName("Send to multiple users - Empty list (Edge Case)")
        void sendNotification_Multiple_EmptyList() {
            List<Long> emptyIds = Collections.emptyList();

            notificationService.sendNotification(emptyIds, "Content", "TYPE", 700L);

            // Vẫn save Notification nhưng saveAll list rỗng
            verifyNoInteractions(notificationRepository);
            verifyNoInteractions(userRepository);
            verifyNoInteractions(eventPublisher);
            verifyNoInteractions(userNotificationRepository);
        }
    }

    @Nested
    @DisplayName("Tests for countUnreadNotifications")
    class CountUnreadTests {
        @Test
        @DisplayName("Count unread - Success")
        void countUnread_Success() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userNotificationRepository.countByUser_IdAndIsReadIsFalse(1L)).thenReturn(5);

            int count = notificationService.countUnreadNotifications(1L);

            assertEquals(5, count);
        }

        @Test
        @DisplayName("Count unread - User not found")
        void countUnread_UserNotFound() {
            when(userRepository.existsById(1L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () ->
                    notificationService.countUnreadNotifications(1L)
            );
        }
    }
}
