package com.group4.projects_management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.core.event.NotificationEvent;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.core.strategy.notification.NotificationStrategy;
import com.group4.projects_management.entity.Notification;
import com.group4.projects_management.entity.UserNotification;
import com.group4.projects_management.mapper.UserNotificationMapper;
import com.group4.projects_management.repository.NotificationRepository;
import com.group4.projects_management.repository.UserNotificationRepository;
import com.group4.projects_management.repository.UserRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImp extends BaseServiceImpl<Notification, Long> implements NotificationService {
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserNotificationMapper userNotificationMapper;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private final List<NotificationStrategy<?>> strategies;


    public NotificationServiceImp(NotificationRepository notificationRepository, UserNotificationRepository userNotificationRepository, UserNotificationMapper userNotificationMapper, UserRepository userRepository, ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper, List<NotificationStrategy<?>> strategies) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.userNotificationMapper = userNotificationMapper;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.strategies = strategies;
    }

    @Override
    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        var notifications = this.userNotificationRepository
                .findAllByUserIdWithNotification(userId);

        return notifications
                .stream()
                .map(this.userNotificationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        var userNotification = this.userNotificationRepository.findByUser_IdAndNotification_Id(userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (userNotification.isRead()) {
            return;
        }

        userNotification.markAsRead();

        this.userNotificationRepository.save(userNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        var notifications = this.userNotificationRepository.findAllByUser_Id(userId);

        var unreadNotifications = notifications.stream()
                .filter(un -> !un.isRead()) // Chỉ lấy những cái chưa đọc
                .toList();

        if (unreadNotifications.isEmpty()) {
            return;
        }

        for (var userNotification : unreadNotifications) {
            userNotification.markAsRead();
        }

        this.userNotificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public <T> void send(List<Long> receiverIds, T contextData, Long referenceId) {
        if (receiverIds == null || receiverIds.isEmpty()) return;

        NotificationStrategy<T> strategy = (NotificationStrategy<T>) strategies.stream()
                .filter(s -> s.supports(contextData.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Strategy hỗ trợ loại dữ liệu này!"));


        Notification notif = new Notification();
        notif.setReferenceId(referenceId.toString());
        notif.setType(strategy.getType().name());
        notif.setTitle(strategy.buildTitle(contextData));

        Map<String, Object> metadataMap = strategy.buildMetadata(contextData);

        try {
            String jsonMetadata = objectMapper.writeValueAsString(metadataMap);
            notif.setMetadata(jsonMetadata);
        } catch (JsonProcessingException e) {
            notif.setMetadata("{}");
        }

        notificationRepository.save(notif);

        var users = userRepository.findAllById(receiverIds);

        var userNotifications = users.stream().map(user -> {
            var un = new UserNotification();
            un.setUser(user);
            un.setNotification(notif);

            var dto = userNotificationMapper.toDto(un);
            dto.setMetadata(metadataMap);

            eventPublisher.publishEvent(new NotificationEvent(user.getId(), dto));
            return un;
        }).toList();

        if (!userNotifications.isEmpty()) {
            userNotificationRepository.saveAll(userNotifications);
        }
    }

    @Transactional
    public <T> void send(Long receiverId, T contextData, Long referenceId) {
        this.send(Collections.singletonList(receiverId), contextData, referenceId);
    }

    @Override
    public int countUnreadNotifications(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return this.userNotificationRepository.countByUser_IdAndIsReadIsFalse(userId);
    }
}