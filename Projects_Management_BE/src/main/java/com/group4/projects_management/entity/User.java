/***********************************************************************
 * Module:  User.java
 * Author:  Lenovo
 * Purpose: Defines the Class User
 ***********************************************************************/

import java.util.*;

/** @pdOid 2ec3d48f-9f0b-4d44-91e9-a43da8d13c77 */
public class User {
   /** @pdOid 37b7dd80-8f41-477a-a853-42d9fb10ae40 */
   private Long id;
   /** @pdOid 2d934aab-1e8e-4e2f-8f30-06ebca1a1c50 */
   private java.lang.String username;
   /** @pdOid 791c9eef-6329-4983-ae73-e16790de86ba */
   private java.lang.String hashedPassword;
   /** @pdOid 1a8c9487-6c10-404a-9292-0da9bf337c36 */
   private java.lang.String fullName;
   /** @pdOid 3e14300c-621d-4e4d-b8cf-3b97fcc742e5 */
   private java.lang.String email;
   /** @pdOid db79ecb1-3a84-462a-9151-aa2f93e0a25a */
   private java.lang.String address;
   /** @pdOid e6bd3c2a-d305-4d1c-9d32-c9950d4c0d02 */
   private boolean isActive;
   
   /** @pdRoleInfo migr=no name=AppRole assc=association3 mult=1..1 */
   public AppRole appRole;
   /** @pdRoleInfo migr=no name=Project assc=association7 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Aggregation */
   public java.util.Collection<Project> project;
   /** @pdRoleInfo migr=no name=UserNotification assc=association19 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Aggregation */
   public java.util.Collection<UserNotification> userNotification;
   
   /** @param roleName
    * @pdOid 62fcfaba-7039-4108-a9ca-15704bd90f5c */
   public boolean hasRole(java.lang.String roleName) {
      // TODO: implement
      return false;
   }
   
   
   /** @pdGenerated default getter */
   public java.util.Collection<Project> getProject() {
      if (project == null)
         project = new java.util.HashSet<Project>();
      return project;
   }
   
   /** @pdGenerated default iterator getter */
   public java.util.Iterator getIteratorProject() {
      if (project == null)
         project = new java.util.HashSet<Project>();
      return project.iterator();
   }
   
   /** @pdGenerated default setter
     * @param newProject */
   public void setProject(java.util.Collection<Project> newProject) {
      removeAllProject();
      for (java.util.Iterator iter = newProject.iterator(); iter.hasNext();)
         addProject((Project)iter.next());
   }
   
   /** @pdGenerated default add
     * @param newProject */
   public void addProject(Project newProject) {
      if (newProject == null)
         return;
      if (this.project == null)
         this.project = new java.util.HashSet<Project>();
      if (!this.project.contains(newProject))
      {
         this.project.add(newProject);
         newProject.setCreatedBy(this);      
      }
   }
   
   /** @pdGenerated default remove
     * @param oldProject */
   public void removeProject(Project oldProject) {
      if (oldProject == null)
         return;
      if (this.project != null)
         if (this.project.contains(oldProject))
         {
            this.project.remove(oldProject);
            oldProject.setCreatedBy((User)null);
         }
   }
   
   /** @pdGenerated default removeAll */
   public void removeAllProject() {
      if (project != null)
      {
         Project oldProject;
         for (java.util.Iterator iter = getIteratorProject(); iter.hasNext();)
         {
            oldProject = (Project)iter.next();
            iter.remove();
            oldProject.setCreatedBy((User)null);
         }
      }
   }
   /** @pdGenerated default getter */
   public java.util.Collection<UserNotification> getUserNotification() {
      if (userNotification == null)
         userNotification = new java.util.HashSet<UserNotification>();
      return userNotification;
   }
   
   /** @pdGenerated default iterator getter */
   public java.util.Iterator getIteratorUserNotification() {
      if (userNotification == null)
         userNotification = new java.util.HashSet<UserNotification>();
      return userNotification.iterator();
   }
   
   /** @pdGenerated default setter
     * @param newUserNotification */
   public void setUserNotification(java.util.Collection<UserNotification> newUserNotification) {
      removeAllUserNotification();
      for (java.util.Iterator iter = newUserNotification.iterator(); iter.hasNext();)
         addUserNotification((UserNotification)iter.next());
   }
   
   /** @pdGenerated default add
     * @param newUserNotification */
   public void addUserNotification(UserNotification newUserNotification) {
      if (newUserNotification == null)
         return;
      if (this.userNotification == null)
         this.userNotification = new java.util.HashSet<UserNotification>();
      if (!this.userNotification.contains(newUserNotification))
      {
         this.userNotification.add(newUserNotification);
         newUserNotification.setUser(this);      
      }
   }
   
   /** @pdGenerated default remove
     * @param oldUserNotification */
   public void removeUserNotification(UserNotification oldUserNotification) {
      if (oldUserNotification == null)
         return;
      if (this.userNotification != null)
         if (this.userNotification.contains(oldUserNotification))
         {
            this.userNotification.remove(oldUserNotification);
            oldUserNotification.setUser((User)null);
         }
   }
   
   /** @pdGenerated default removeAll */
   public void removeAllUserNotification() {
      if (userNotification != null)
      {
         UserNotification oldUserNotification;
         for (java.util.Iterator iter = getIteratorUserNotification(); iter.hasNext();)
         {
            oldUserNotification = (UserNotification)iter.next();
            iter.remove();
            oldUserNotification.setUser((User)null);
         }
      }
   }

}