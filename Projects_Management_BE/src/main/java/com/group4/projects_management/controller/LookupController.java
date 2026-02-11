package com.group4.projects_management.controller; /***********************************************************************
 * Module:  LookupController.java
 * Author:  Lenovo
 * Purpose: Defines the Class LookupController
 ***********************************************************************/

import com.group4.common.dto.LookupDTO;
import com.group4.projects_management.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** @pdOid 2886621b-803e-4f31-877b-6e25a9502ef8 */
@RestController
public class LookupController {
   /** @pdRoleInfo migr=no name=LookupService assc=association35 mult=1..1 */
   @Autowired
   private LookupService lookupService;

   /** @param type
    * @pdOid cb54068c-3294-46b1-884b-2a82559f1a0e */
   public ResponseEntity<List<LookupDTO>> getAllByType(java.lang.String type) {
      return null;
   }

}