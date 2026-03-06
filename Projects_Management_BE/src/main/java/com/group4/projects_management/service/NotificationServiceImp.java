package com.group4.projects_management.service;

import com.group4.common.dto.NotificationDTO;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.entity.Notification;
import com.group4.projects_management.entity.UserNotification;
import com.group4.projects_management.mapper.UserNotificationMapper;
import com.group4.projects_management.repository.NotificationRepository;
import com.group4.projects_management.repository.UserNotificationRepository;
import com.group4.projects_management.repository.UserRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImp extends BaseServiceImpl<Notification, Long> implements NotificationService {
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserNotificationMapper userNotificationMapper;
    private final UserRepository userRepository;


    public NotificationServiceImp(NotificationRepository notificationRepository, UserNotificationRepository userNotificationRepository, UserNotificationMapper userNotificationMapper, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.userNotificationMapper = userNotificationMapper;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
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
        var notification = this.userNotificationRepository.findByUser_IdAndNotification_Id(userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.markAsRead();

        this.userNotificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        var notifications = this.userNotificationRepository.findAllByUser_Id(userId);

        for (var userNotification : notifications) {
            userNotification.markAsRead();
        }

        this.userNotificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void sendNotification(Long userId, String text, String type, Long referenceId) {
        var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var notification = Notification.create(text, type, referenceId.toString());
        notificationRepository.save(notification);

        var userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(notification);
        userNotificationRepository.save(userNotification);

        var dto = userNotificationMapper.toDto(userNotification);
        this.eventPublisher.publishEvent(dto);
    }

    @Override
    @Transactional
    public void sendNotification(List<Long> usersId, String text, String type, Long referenceId) {
        var notification = Notification.create(text, type, referenceId.toString());
        notificationRepository.save(notification);

        var users = this.userRepository.findAllById(usersId);

        if (users.size() != usersId.size()) {
            // Check chuyên sâu nếu cần
        }

        var userNotifications = users.stream().map(user -> {
                    var userNotification = new UserNotification();
                    userNotification.setUser(user);
                    userNotification.setNotification(notification);

                    this.eventPublisher.publishEvent(userNotificationMapper.toDto(userNotification));
                    return userNotification;
                })
                .toList();

        userNotificationRepository.saveAll(userNotifications);
    }

    @Override
    public int countUnreadNotifications(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return this.userNotificationRepository.countByUser_IdAndIsReadIsFalse(userId);
    }
}