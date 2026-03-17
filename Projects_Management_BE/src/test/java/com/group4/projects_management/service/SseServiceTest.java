package com.group4.projects_management.service;

import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SseService sseService;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("CREATE: Nên ném lỗi nếu User không tồn tại")
    void createEmitter_UserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> sseService.createEmitter(userId));
    }

    @Test
    @DisplayName("CREATE: Tạo mới thành công và gửi sự kiện INIT")
    void createEmitter_Success() {
        when(userRepository.existsById(userId)).thenReturn(true);

        SseEmitter emitter = sseService.createEmitter(userId);

        assertNotNull(emitter);
        assertEquals(0L, emitter.getTimeout()); // Kiểm tra timeout set là 0L (vô hạn)
        verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("CREATE: Nếu user đã có kết nối cũ, phải đóng kết nối cũ trước")
    void createEmitter_ReplaceOldOne() throws Exception {
        when(userRepository.existsById(userId)).thenReturn(true);

        SseEmitter mockOldEmitter = mock(SseEmitter.class);
        injectMockEmitterIntoMap(userId, mockOldEmitter);

        sseService.createEmitter(userId);

        verify(mockOldEmitter).complete();
    }

    @Test
    @DisplayName("SEND: Gửi sự kiện thành công")
    void send_Success() throws Exception {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        injectMockEmitterIntoMap(userId, mockEmitter);

        sseService.send(userId, "TEST_EVENT", "Hello World");

        verify(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    @DisplayName("SEND: Khi gửi lỗi (IOException), phải xóa emitter khỏi map")
    void send_Fail_RemoveEmitter() throws Exception {
        // Arrange
        SseEmitter mockEmitter = mock(SseEmitter.class);
        doThrow(new IOException("Connection broken"))
                .when(mockEmitter)
                .send(any(SseEmitter.SseEventBuilder.class));
        injectMockEmitterIntoMap(userId, mockEmitter);

        sseService.send(userId, "EVENT", "DATA");

        verify(mockEmitter).complete();
        sseService.send(userId, "EVENT", "DATA");
        verify(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));

    }

    // Helper: Sử dụng Reflection để truy cập vào Map emitters private
    @SuppressWarnings("unchecked")
    private void injectMockEmitterIntoMap(Long userId, SseEmitter emitter) throws Exception {
        Field field = SseService.class.getDeclaredField("emitters");
        field.setAccessible(true);
        Map<Long, SseEmitter> map = (Map<Long, SseEmitter>) field.get(sseService);
        map.put(userId, emitter);
    }
}