package com.group4.projects_management.service; /***********************************************************************
 * Module:  LookupService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface LookupService
 ***********************************************************************/

import com.group4.common.dto.LookupDTO;
import com.group4.common.enums.LookupType;

import java.util.List;

/** @pdOid 0c84eb52-199d-434d-97db-246da9ab53d7 */
public interface LookupService {
   /** @param type
    * @pdOid 95133962-e731-4541-ac21-f74b22d4cbf0 */
   List<LookupDTO> getAll(LookupType type);
   /** @pdOid 3aa6e98e-e7b7-468e-941b-cce05af69f1e */
   LookupDTO saveOrUpdate(LookupType type, LookupDTO dto) throws Throwable;
}