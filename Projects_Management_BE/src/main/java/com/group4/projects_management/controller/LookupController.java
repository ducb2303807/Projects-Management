package com.group4.projects_management.controller; /***********************************************************************
 * Module:  LookupController.java
 * Author:  Lenovo
 * Purpose: Defines the Class LookupController
 ***********************************************************************/

import com.group4.common.dto.LookupDTO;
import com.group4.common.enums.LookupType;
import com.group4.projects_management.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lookups")
public class LookupController {
   @Autowired
   private LookupService lookupService;

   @GetMapping("/{type}")
   public ResponseEntity<List<LookupDTO>> getAll(@PathVariable LookupType type) {
      return ResponseEntity.ok(lookupService.getAll(type));
   }

   @PreAuthorize("hasRole('ADMIN')")
   @PostMapping("/{type}")
   public ResponseEntity<LookupDTO> createOrUpdate(@PathVariable LookupType type,
                                                         @RequestBody LookupDTO dto)
           throws Throwable {
      return ResponseEntity.ok(lookupService.saveOrUpdate(type, dto));
   }

}