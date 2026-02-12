package com.group4.projects_management.service; /***********************************************************************
 * Module:  LookupServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class LookupServiceImpl
 ***********************************************************************/

import com.group4.common.dto.LookupDTO;
import com.group4.common.enums.LookupType;
import com.group4.projects_management.entity.BaseLookup;
import com.group4.projects_management.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
/** @pdOid 5b26dc17-fb64-403c-b0f2-7a4c192ac07d */
public class LookupServiceImpl implements LookupService {
    /**
     * @pdRoleInfo migr=no name=AppRoleRepository assc=association38 mult=1..1
     */
    @Autowired
    private AppRoleRepository appRoleRepository;
    /**
     * @pdRoleInfo migr=no name=PermissionRepository assc=association39 mult=1..1
     */
    @Autowired
    private PermissionRepository permissionRepository;
    /**
     * @pdRoleInfo migr=no name=ProjectRoleRepository assc=association40 mult=1..1
     */
    @Autowired
    private ProjectRoleRepository projectRoleRepository;
    /**
     * @pdRoleInfo migr=no name=PriorityRepository assc=association41 mult=1..1
     */
    @Autowired
    private PriorityRepository priorityRepository;
    /**
     * @pdRoleInfo migr=no name=ProjectStatusRepository assc=association42 mult=1..1
     */
    @Autowired
    private ProjectStatusRepository projectStatusRepository;
    /**
     * @pdRoleInfo migr=no name=ProjectMemberStatusRepository assc=association43 mult=1..1
     */
    @Autowired
    private ProjectMemberStatusRepository projectMemberStatusRepository;
    /**
     * @pdRoleInfo migr=no name=TaskStatusRepository assc=association44 mult=1..1
     */
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private Map<LookupType, JpaRepository<? extends BaseLookup<?>, ?>> repositoryMap;

    @PostConstruct
    public void init() {
        repositoryMap = new EnumMap<>(LookupType.class);
        repositoryMap.put(LookupType.APP_ROLE, appRoleRepository);
        repositoryMap.put(LookupType.PERMISSION, permissionRepository);
        repositoryMap.put(LookupType.PRIORITY, priorityRepository);
        repositoryMap.put(LookupType.PROJECT_ROLE, projectRoleRepository);
        repositoryMap.put(LookupType.MEMBER_STATUS, projectMemberStatusRepository);
        repositoryMap.put(LookupType.PROJECT_STATUS, projectStatusRepository);
        repositoryMap.put(LookupType.TASK_STATUS, taskStatusRepository);
    }

    @Override
    public List<LookupDTO> getAll(LookupType type) {
        JpaRepository<? extends BaseLookup<?>, ?> repo = repositoryMap.get(type);
        if (repo == null) {
            throw new IllegalArgumentException("Chưa cấu hình Repository cho type: " + type);
        }

        return repo.findAll().stream()
                .map(e -> new LookupDTO(e.getId().toString(), e.getName(), e.getDescription()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public int update() {
        return 0;
    }
}