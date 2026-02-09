/***********************************************************************
 * Module:  HostContext.java
 * Author:  Lenovo
 * Purpose: Defines the Interface HostContext
 ***********************************************************************/

import java.util.*;

/** @pdOid 53f72f6f-c7c8-404d-ae54-7173f98eb1a1 */
public interface HostContext {
   /** @pdOid dfbcdb6e-23db-4c33-9e37-a17e472b2dc9 */
   List<Project> getAllProjects();
   /** @param projectId
    * @pdOid 4342903f-b265-4bf7-aa55-1707ad4373d9 */
   List<Task> getTasksByProject(Long projectId);
   /** @param msg 
    * @param type
    * @pdOid 94fca0d7-deb0-4b24-b002-f732a9cdd246 */
   void publishNotification(java.lang.String msg, java.lang.String type);
   /** @pdOid d2182cb0-c05b-49bc-9c97-4acef6d2b4a7 */
   Long getCurrentUserId();
   /** @param widget
    * @pdOid f819445f-e911-4541-b617-a795765e3aaa */
   void registerWidget(java.lang.Object widget);

}