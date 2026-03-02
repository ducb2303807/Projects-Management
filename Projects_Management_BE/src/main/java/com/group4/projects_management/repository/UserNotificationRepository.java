package com.group4.projects_management.repository; /***********************************************************************
 * Module:  UserNotificationRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface UserNotificationRepository
 ***********************************************************************/

import com.group4.projects_management.entity.UserNotification;
import com.group4.projects_management.repository.Base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends BaseRepository<UserNotification, Long> {

    @Query("SELECT un FROM UserNotification un JOIN FETCH un.notification n " +
            "WHERE un.user.id = :userId " +
            "ORDER BY n.createdAt DESC")
    List<UserNotification> findAllByUserIdWithNotification(@Param("userId") Long userId);

    Optional<UserNotification> findByUser_IdAndNotification_Id(Long userId, Long notificationId);

}