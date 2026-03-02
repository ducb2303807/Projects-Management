package com.group4.projects_management.service.base; /***********************************************************************
 * Module:  BaseService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface BaseService
 ***********************************************************************/

import java.util.*;

/** @pdOid 59b8c30f-57fd-4bbe-8b5d-1ae26a9c6aa5 */
public interface BaseService <T,ID> {
   /** @param id
    * @pdOid ccb21639-8882-4ff6-9fa8-8e2358de1ba1 */
   Optional<T> findById(ID id);
   /** @pdOid da937783-44b5-4ec4-bd06-cc1b6d41a17e */
   List<T> findAll();
   /** @param id
    * @pdOid eb84231b-e476-4748-847c-6d68c19afa96 */
   void deleteById(ID id);
   /** @param id
    * @pdOid 4b37e9f3-016d-4458-9b87-2a01830ae100 */
   boolean existsById(ID id);

}