package com.group4.projects_management.service; /***********************************************************************
 * Module:  NotificationServiceImp.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationServiceImp
 ***********************************************************************/

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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImp extends BaseServiceImpl<Notification, Long> implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserNotificationMapper userNotificationMapper;

    private final UserRepository userRepository;

    public NotificationServiceImp(NotificationRepository notificationRepository, UserNotificationRepository userNotificationRepository, UserNotificationMapper userNotificationMapper, UserRepository userRepository) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.userNotificationMapper = userNotificationMapper;
        this.userRepository = userRepository;
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
    public void sendNotification(Long userId, String text, String type, Long referenceId) {
        var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var notification = Notification.create(text, type, referenceId.toString());
        notificationRepository.save(notification);

        var userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(notification);
        userNotificationRepository.save(userNotification);
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
                    return userNotification;
                })
                .toList();

        userNotificationRepository.saveAll(userNotifications);
    }
}