package com.group4.projects_management.repository;
/***********************************************************************
 * Module:  UserWidgetConfigRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface UserWidgetConfigRepository
 ***********************************************************************/

import com.group4.projects_management.entity.UserWidgetConfig;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.List;

/** @pdOid 20f038f5-8850-4c1c-bab7-c80044fdec7c */
public interface UserWidgetConfigRepository extends BaseRepository<UserWidgetConfig, Long> {

    // Lấy tất cả cấu hình dashboard của một user
    List<UserWidgetConfig> findAllByUser_Id(Long userId);

    // Xóa toàn bộ cấu hình dashboard của một user
    void deleteAllByUser_Id(Long userId);
}
