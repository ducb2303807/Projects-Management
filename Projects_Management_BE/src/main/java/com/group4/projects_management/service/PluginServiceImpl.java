package com.group4.projects_management.service;
/***********************************************************************
 * Module:  PluginServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class PluginServiceImpl
 ***********************************************************************/

import com.group4.common.dto.PluginDTO;
import com.group4.common.dto.UserWidgetConfigDTO;
import com.group4.projects_management.entity.Plugin;
import com.group4.projects_management.entity.User;
import com.group4.projects_management.entity.UserWidgetConfig;
import com.group4.projects_management.mapper.PluginMapper;
import com.group4.projects_management.repository.PluginRepository;
import com.group4.projects_management.repository.UserWidgetConfigRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginServiceImpl extends BaseServiceImpl<Plugin, Long> implements PluginService {
   private final PluginRepository pluginRepository;
   private final UserWidgetConfigRepository userWidgetConfigRepository;
   private final PluginMapper pluginMapper;

   @PersistenceContext
   private EntityManager entityManager;

   public PluginServiceImpl(PluginRepository repository,
                            UserWidgetConfigRepository userWidgetConfigRepository,
                            PluginMapper pluginMapper) {
      super(repository);
      this.pluginRepository = repository;
      this.userWidgetConfigRepository = userWidgetConfigRepository;
      this.pluginMapper = pluginMapper;
   }

   @Override
   public List<PluginDTO> getAvailablePlugins() {
      return pluginRepository.findAll()
              .stream()
              .map(pluginMapper::toDto)
              .toList();
   }

   @Override
   public List<UserWidgetConfigDTO> getUserDashboardConfig(Long userId) {
      return userWidgetConfigRepository.findAllByUser_Id(userId)
              .stream()
              .map(pluginMapper::toDto)
              .toList();
   }

   @Override
   @Transactional
   public void saveDashboardLayout(Long userId, List<UserWidgetConfigDTO> configs) {
      // Xóa layout cũ
      userWidgetConfigRepository.deleteAllByUser_Id(userId);

      // Lưu layout mới
      configs.forEach(dto -> {
         UserWidgetConfig entity = pluginMapper.toEntity(dto);

         // set user và plugin từ repo
         var userRef = entityManager.getReference(User.class, userId);
         var plugin = pluginRepository.findById(dto.getPluginId())
                 .orElseThrow(() -> new RuntimeException("Không tìm thấy plugin với ID: " + dto.getPluginId()));

         entity.setUser(userRef);
         entity.setPlugin(plugin);
         entity.setIsVisible(dto.getIsVisible());

         userWidgetConfigRepository.save(entity);
      });
   }
}
