package com.group4.projects_management.controller; /***********************************************************************
 * Module:  PluginController.java
 * Author:  Lenovo
 * Purpose: Defines the Class PluginController
 ***********************************************************************/

import com.group4.common.dto.PluginDTO;
import com.group4.common.dto.UserWidgetConfigDTO;
import com.group4.projects_management.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** @pdOid 9c12543b-7a1a-42d3-b305-3758954e3331 */
@RestController
public class PluginController {
   /** @pdRoleInfo migr=no name=PluginService assc=association36 mult=1..1 */
   @Autowired
   private PluginService pluginService;
   
   /** @pdOid 00674d8a-b4ed-4cc1-ac2d-64bf403a50ac */
   public ResponseEntity<List<PluginDTO>> getAvailablePlugins() {
      // TODO: implement
      return null;
   }
   
   /** @param userId
    * @pdOid 43fb8959-3130-4b97-bb3e-d2d991711f76 */
   public ResponseEntity<List<UserWidgetConfigDTO>> getUserDashboardConfig(Long userId) {
      // TODO: implement
      return null;
   }
   
   /** @param userId 
    * @param configs
    * @pdOid 1564f32b-bb55-4103-8e1b-dfaaa15ffa41 */
   public ResponseEntity<Void> saveDashboardLayout(Long userId, List<UserWidgetConfigDTO> configs) {
      // TODO: implement
      return null;
   }

}