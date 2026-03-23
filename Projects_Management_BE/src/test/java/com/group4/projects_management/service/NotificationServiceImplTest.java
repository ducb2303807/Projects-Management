package com.group4.projects_management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.core.event.NotificationEvent;
import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.entity.Notification;
import com.group4.projects_management.entity.User;
import com.group4.projects_management.entity.UserNotification;
import com.group4.projects_management.enums.NotificationType;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserNotificationRepository userNotificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserNotificationMapper userNotificationMapper;
    @Mock
    private ObjectMapper objectMapper;

    // Mock một Strategy tổng quát
    @Mock
    private NotificationStrategy<Object> mockStrategy;

    // Không dùng @InjectMocks, tự khởi tạo để truyền List<Strategy> chuẩn xác
    private NotificationServiceImp notificationService;

    private User mockUser;
    private Notification mockNotification;
    private UserNotification mockUserNotification;
    private NotificationDTO mockDto;

    static class DummyContext {
    }

    private DummyContext dummyContext;

    @BeforeEach
    void setUp() {
        // Tự tiêm các dependency vào Service
        notificationService = new NotificationServiceImp(
                notificationRepository,
                userNotificationRepository,
                userNotificationMapper,
                userRepository,
                eventPublisher,
                objectMapper,
                List.of(mockStrategy) // Truyền mockStrategy vào dạng List
        );

        mockUser = new User();
        mockUser.setId(1L);

        mockNotification = new Notification();
        mockNotification.setId(100L);

        mockUserNotification = new UserNotification();
        mockUserNotification.setUser(mockUser);
        mockUserNotification.setNotification(mockNotification);

        mockDto = new NotificationDTO();
        dummyContext = new DummyContext();
    }

    @Nested
    @DisplayName("Tests getNotificationsForUser")
    class GetNotificationsTests {
        @Test
        @DisplayName("Success")
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
        @DisplayName("User has zero notifications")
        void getNotificationsForUser_EmptyList() {
            when(userNotificationRepository.findAllByUserIdWithNotification(1L))
                    .thenReturn(Collections.emptyList());

            List<NotificationDTO> result = notificationService.getNotificationsForUser(1L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests markAsRead and markAllAsRead")
    class MarkAsReadTests {
        @Test
        @DisplayName("Mark single as read - Success")
        void markAsRead_Success() {
            when(userNotificationRepository.findByUser_IdAndNotification_Id(1L, 100L))
                    .thenReturn(Optional.of(mockUserNotification));

            notificationService.markAsRead(100L, 1L);

            verify(userNotificationRepository).save(mockUserNotification);
            assertTrue(mockUserNotification.isRead());
        }

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
        @DisplayName("Mark as read - Already read - Should return early and NOT save")
        void markAsRead_AlreadyRead_ShouldNotSave() {
            mockUserNotification.setRead(true);
            when(userNotificationRepository.findByUser_IdAndNotification_Id(1L, 100L))
                    .thenReturn(Optional.of(mockUserNotification));

            notificationService.markAsRead(100L, 1L);

            verify(userNotificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Mark all as read - All notifications already read - Should NOT saveAll")
        void markAllAsRead_AllAlreadyRead_ShouldNotSave() {
            when(userRepository.existsById(1L)).thenReturn(true);

            mockUserNotification.setRead(true);
            when(userNotificationRepository.findAllByUser_Id(1L))
                    .thenReturn(List.of(mockUserNotification));

            notificationService.markAllAsRead(1L);

            verify(userNotificationRepository, never()).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("Tests for send() with Strategy Pattern")
    class SendNotificationTests {

        @Test
        @DisplayName("Empty Receiver List - Should do nothing")
        void send_EmptyList_ShouldReturnEarly() {
            notificationService.send(Collections.emptyList(), dummyContext, 1L);

            verifyNoInteractions(mockStrategy);
            verifyNoInteractions(notificationRepository);
            verifyNoInteractions(userNotificationRepository);
        }

        @Test
        @DisplayName("Strategy Not Found - Should throw IllegalArgumentException")
        void send_StrategyNotFound_ShouldThrowException() {
            when(mockStrategy.supports(any())).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    notificationService.send(List.of(1L), dummyContext, 1L)
            );

            assertEquals("Không tìm thấy Strategy hỗ trợ loại dữ liệu này!", exception.getMessage());
        }

        @Test
        @DisplayName("Send to multiple users - Success")
        void send_MultipleUsers_Success() throws Exception {
            when(mockStrategy.supports(any())).thenReturn(true);
            when(mockStrategy.getType()).thenReturn(NotificationType.PROJECT_INVITATION);
            when(mockStrategy.buildTitle(any())).thenReturn("Test Title");
            when(mockStrategy.buildMetadata(any())).thenReturn(Map.of("key", "value"));

            when(objectMapper.writeValueAsString(any())).thenReturn("{\"key\":\"value\"}");

            User user2 = new User();
            user2.setId(2L);
            when(userRepository.findAllById(anyList())).thenReturn(List.of(mockUser, user2));
            when(userNotificationMapper.toDto(any())).thenReturn(mockDto);

            notificationService.send(List.of(1L, 2L), dummyContext, 500L);

            ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(1)).save(notifCaptor.capture());
            assertEquals("Test Title", notifCaptor.getValue().getTitle());
            assertEquals("{\"key\":\"value\"}", notifCaptor.getValue().getMetadata());

            ArgumentCaptor<List<UserNotification>> unCaptor = ArgumentCaptor.forClass(List.class);
            verify(userNotificationRepository).saveAll(unCaptor.capture());
            assertEquals(2, unCaptor.getValue().size()); // Lưu cho 2 users

            verify(eventPublisher, times(2)).publishEvent(any(NotificationEvent.class));
        }

        @Test
        @DisplayName("Send - JSON Parsing Exception - Should set fallback empty JSON {}")
        void send_JsonException_ShouldFallbackToEmptyBrackets() throws Exception {
            when(mockStrategy.supports(any())).thenReturn(true);
            when(mockStrategy.getType()).thenReturn(NotificationType.PROJECT_INVITATION);

            when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
            when(userNotificationMapper.toDto(any(UserNotification.class)))
                    .thenReturn(new NotificationDTO());
            when(userRepository.findAllById(anyList())).thenReturn(List.of(mockUser));

            notificationService.send(List.of(1L), dummyContext, 500L);

            ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(notifCaptor.capture());
            assertEquals("{}", notifCaptor.getValue().getMetadata());
        }

        @Test
        @DisplayName("Send to single user (Overload method) - Success")
        void send_SingleUser_Success() {
            // Chỉ cần verify nó gọi qua hàm list và không chết là được
            when(userNotificationMapper.toDto(any(UserNotification.class)))
                    .thenReturn(new NotificationDTO());
            when(mockStrategy.supports(any())).thenReturn(true);
            when(mockStrategy.getType()).thenReturn(NotificationType.PROJECT_INVITATION);
            when(userRepository.findAllById(anyList())).thenReturn(List.of(mockUser));

            notificationService.send(1L, dummyContext, 500L);

            verify(notificationRepository).save(any(Notification.class));
            verify(userNotificationRepository).saveAll(anyList());
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

            assertEquals(5, notificationService.countUnreadNotifications(1L));
        }
    }
}