package com.group4.projects_management.repository; /***********************************************************************
 * Module:  NotificationRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface NotificationRepository
 ***********************************************************************/

import com.group4.projects_management.entity.Notification;
import com.group4.projects_management.repository.Base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/** @pdOid cdfeb880-3fec-4ff7-a60a-88371563fb5c */
public interface NotificationRepository extends BaseRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.referenceId = :refId AND n.type = 'PROJECT_INVITATION'")
    Optional<Notification> findByReferenceIdAndType(@Param("refId") String refId);
}