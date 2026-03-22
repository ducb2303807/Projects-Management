package com.group4.projects_management.service;
/***********************************************************************
 * Module:  LookupServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class LookupServiceImpl
 ***********************************************************************/

import com.group4.common.dto.LookupDTO;
import com.group4.common.enums.BusinessErrorCode;
import com.group4.common.enums.LookupType;
import com.group4.projects_management.core.exception.BusinessException;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.mapper.LookupMapper;
import com.group4.projects_management.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LookupServiceImpl implements LookupService {
    private final AppRoleRepository appRoleRepository;
    private final PermissionRepository permissionRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final PriorityRepository priorityRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectMemberStatusRepository projectMemberStatusRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LookupMapper lookupMapper;

    private final Map<LookupType, LookupMetadata<?, ?>> registry = new EnumMap<>(LookupType.class);

    @PostConstruct
    public void init() {
        // config ở db
        registry.put(LookupType.APP_ROLE, new LookupMetadata<>(appRoleRepository, AppRole::new, false));
        registry.put(LookupType.PERMISSION, new LookupMetadata<>(permissionRepository, Permission::new, false));
        registry.put(LookupType.PROJECT_ROLE, new LookupMetadata<>(projectRoleRepository, ProjectRole::new, false));

        // config business
        registry.put(LookupType.PRIORITY, new LookupMetadata<>(priorityRepository, Priority::new, true));
        registry.put(LookupType.PROJECT_STATUS, new LookupMetadata<>(projectStatusRepository, ProjectStatus::new, true));
        registry.put(LookupType.MEMBER_STATUS, new LookupMetadata<>(projectMemberStatusRepository, ProjectMemberStatus::new, true));
        registry.put(LookupType.TASK_STATUS, new LookupMetadata<>(taskStatusRepository, TaskStatus::new, true));
    }

    @Override
    public List<LookupDTO> getAll(LookupType type) {
        LookupMetadata<?, ?> meta = registry.get(type);
        if (meta == null) {
            log.error("Chưa cấu hình Metadata cho type: {}", type);
            throw new ResourceNotFoundException("Loại danh mục không tồn tại trong hệ thống!");
        }

        return meta.repository.findAll().stream()
                .map(lookupMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public LookupDTO saveOrUpdate(LookupType type, LookupDTO dto) {
        LookupMetadata<BaseLookup<Serializable>, Serializable> meta =
                (LookupMetadata<BaseLookup<Serializable>, Serializable>) registry.get(type);

        BaseLookup<Serializable> entity;
        boolean isUpdate = dto.getId() != null && !dto.getId().isBlank();

        if (isUpdate) {
            if (!meta.editable()) {
                throw new BusinessException("Danh mục hệ thống này không được phép chỉnh sửa!", BusinessErrorCode.SYSTEM_ACCESS_DENIED);
            }
            Serializable id = (Serializable) parseId(dto.getId());
            entity = meta.repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi để cập nhật"));
        } else {
            entity = meta.factory.get();
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setSystemCode(dto.getSystemCode());

        BaseLookup<Serializable> saved = meta.repository.save(entity);

        return lookupMapper.toDto(saved);
    }

    private Object parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (Exception e) {
            return id;
        }
    }

    private record LookupMetadata<E extends BaseLookup<ID>, ID extends Serializable>(JpaRepository<E, ID> repository,
                                                                                     Supplier<E> factory,
                                                                                     boolean editable) {
    }
}