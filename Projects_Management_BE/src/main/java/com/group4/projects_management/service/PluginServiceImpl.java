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

/** @pdOid 9139fdfa-02cb-4036-8013-9bb9925855fe */
@Service
public class PluginServiceImpl extends BaseServiceImpl<Plugin,Long> implements PluginService {
   /** @pdRoleInfo migr=no name=PluginRepository assc=association37 mult=1..1 */
   private final PluginRepository pluginRepository;
   /** @pdRoleInfo migr=no name=UserWidgetConfigRepository assc=association49 mult=1..1 */
   public UserWidgetConfigRepository userWidgetConfigRepository;

   public PluginServiceImpl(PluginRepository repository) {
      super(repository);
      this.pluginRepository = repository;
   }

   @Override
   public List<PluginDTO> getAvailablePlugins() {
      return List.of();
   }

   @Override
   public UserWidgetConfigDTO getUserDashboardConfig(Long userId) {
      return null;
   }

   @Override
   public void saveDashboardLayout(int userId, List<UserWidgetConfigDTO> configs) {

   }
}