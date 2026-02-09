package com.group4.common.FE_Temp; /***********************************************************************
 * Module:  HostContextRemoteImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class HostContextRemoteImpl
 ***********************************************************************/

import com.group4.common.interfaces.HostContext;

import java.util.List;

/** @pdOid b143ae7a-e68d-4634-83bc-7d84084e2e8c */
public class HostContextRemoteImpl implements HostContext {
    @Override
    public List<ProjectDTO> getAllProjects() {
        return List.of();
    }

    @Override
    public List<TaskDTO> getTasksByProject(Long projectId) {
        return List.of();
    }

    @Override
    public void publishNotification(String msg, String type) {

    }

    @Override
    public Long getCurrentUserId() {
        return 0L;
    }

    @Override
    public void registerWidget(Object widget) {

    }
}