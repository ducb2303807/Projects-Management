package com.group4.projects_management.controller; /***********************************************************************
 * Module:  PluginController.java
 * Author:  Lenovo
 * Purpose: Defines the Class PluginController
 ***********************************************************************/

import com.group4.common.dto.PluginDTO;
import com.group4.common.dto.UserWidgetConfigDTO;
import com.group4.projects_management.service.PluginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** @pdOid 9c12543b-7a1a-42d3-b305-3758954e3331 */
@RestController
@RequestMapping("/api/plugins")
public class PluginController {
   @Autowired
   private PluginService pluginService;

   @GetMapping
   public ResponseEntity<List<PluginDTO>> getAvailablePlugins() {
      return ResponseEntity.ok(pluginService.getAvailablePlugins());
   }

   @GetMapping("/dashboard/{userId}")
   public ResponseEntity<List<UserWidgetConfigDTO>> getUserDashboardConfig(@PathVariable Long userId) {
      return ResponseEntity.ok(pluginService.getUserDashboardConfig(userId));
   }
   

   @PostMapping("/dashboard/{userId}")
   public ResponseEntity<Void> saveDashboardLayout(
           @PathVariable Long userId,
           @Valid @RequestBody List<UserWidgetConfigDTO> configs) {
      pluginService.saveDashboardLayout(userId, configs);
      return ResponseEntity.ok().build();
   }

}