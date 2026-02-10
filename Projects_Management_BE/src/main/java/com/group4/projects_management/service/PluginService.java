package com.group4.projects_management.service; /***********************************************************************
 * Module:  PluginService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface PluginService
 ***********************************************************************/

import com.group4.common.dto.PluginDTO;
import com.group4.common.dto.UserWidgetConfigDTO;

import java.util.List;

/** @pdOid 63165158-2890-4f89-98b9-3be829b0bb39 */
public interface PluginService {
   /** @pdOid a2879aa2-4f4e-4013-ae6f-074485ca3f1b */
   List<PluginDTO> getAvailablePlugins();
   /** @param userId
    * @pdOid c025775c-facf-4244-89f9-f16157bb51fb */
   UserWidgetConfigDTO getUserDashboardConfig(Long userId);
   /** @param userId 
    * @param configs
    * @pdOid aeea4104-4c00-43c1-a8f3-cf12d485c7e0 */
   void saveDashboardLayout(int userId, List<UserWidgetConfigDTO> configs);

}