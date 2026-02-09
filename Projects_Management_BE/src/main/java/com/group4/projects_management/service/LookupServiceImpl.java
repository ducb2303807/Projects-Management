package com.group4.projects_management.service; /***********************************************************************
 * Module:  LookupServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class LookupServiceImpl
 ***********************************************************************/

import com.group4.projects_management.entity.BaseLookup;
import com.group4.projects_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/** @pdOid 5b26dc17-fb64-403c-b0f2-7a4c192ac07d */
public class LookupServiceImpl implements LookupService {
   /** @pdRoleInfo migr=no name=AppRoleRepository assc=association38 mult=1..1 */
   @Autowired
   private AppRoleRepository appRoleRepository;
   /** @pdRoleInfo migr=no name=PermissionRepository assc=association39 mult=1..1 */
   @Autowired
   private PermissionRepository permissionRepository;
   /** @pdRoleInfo migr=no name=ProjectRoleRepository assc=association40 mult=1..1 */
   @Autowired
   private ProjectRoleRepository projectRoleRepository;
   /** @pdRoleInfo migr=no name=PriorityRepository assc=association41 mult=1..1 */
   @Autowired
   private PriorityRepository priorityRepository;
   /** @pdRoleInfo migr=no name=ProjectStatusRepository assc=association42 mult=1..1 */
   @Autowired
   private ProjectStatusRepository projectStatusRepository;
   /** @pdRoleInfo migr=no name=ProjectMemberStatusRepository assc=association43 mult=1..1 */
   @Autowired
   private ProjectMemberStatusRepository projectMemberStatusRepository;
   /** @pdRoleInfo migr=no name=TaskStatusRepository assc=association44 mult=1..1 */
   @Autowired
   private TaskStatusRepository taskStatusRepository;

   @Override
   public List<BaseLookup> getAll(String type) {
      return List.of();
   }

   @Override
   public int update() {
      return 0;
   }
}