package com.group4.projects_management.service; /***********************************************************************
 * Module:  PluginServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class PluginServiceImpl
 ***********************************************************************/

import com.group4.common.dto.PluginDTO;
import com.group4.common.dto.UserWidgetConfigDTO;
import com.group4.projects_management.entity.Plugin;
import com.group4.projects_management.repository.PluginRepository;
import com.group4.projects_management.repository.UserWidgetConfigRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginServiceImpl extends BaseServiceImpl<Plugin,Long> implements PluginService {
   private final PluginRepository pluginRepository;
   private final UserWidgetConfigRepository userWidgetConfigRepository;

   public PluginServiceImpl(PluginRepository repository, UserWidgetConfigRepository userWidgetConfigRepository) {
      super(repository);
      this.pluginRepository = repository;
       this.userWidgetConfigRepository = userWidgetConfigRepository;
   }

   @Override
   public List<PluginDTO> getAvailablePlugins() {
      return List.of();
   }

   @Override
   public List<UserWidgetConfigDTO> getUserDashboardConfig(Long userId) {
      return null;
   }

   @Override
   public void saveDashboardLayout(Long userId, List<UserWidgetConfigDTO> configs) {

   }

}