package com.group4.projects_management.repository;

import com.group4.projects_management.entity.AppRole;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.List;

public interface AppRoleRepository extends BaseRepository<AppRole, Long> {
    List<AppRole> getAppRoleByName(String name);

    AppRole getAppRoleBySystemCode(String systemCode);
}