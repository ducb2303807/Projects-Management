/***********************************************************************
 * Module:  ProjectRole.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectRole
 ***********************************************************************/

/** @pdOid 235eeec6-021f-4ed1-bca5-d62e8563ef45 */
public class ProjectRole extends BaseLookup {
   /** @pdRoleInfo migr=no name=Permission assc=rolePermission coll=java.util.Collection mult=0..* type=Aggregation */
   public java.util.Collection<Permission> permissions;
   
   
   /** @pdGenerated default getter */
   public java.util.Collection<Permission> getPermissions() {
      if (permissions == null)
         permissions = new java.util.Vector<Permission>();
      return permissions;
   }
   
   /** @pdGenerated default iterator getter */
   public java.util.Iterator getIteratorPermissions() {
      if (permissions == null)
         permissions = new java.util.Vector<Permission>();
      return permissions.iterator();
   }
   
   /** @pdGenerated default setter
     * @param newPermissions */
   public void setPermissions(java.util.Collection<Permission> newPermissions) {
      removeAllPermissions();
      for (java.util.Iterator iter = newPermissions.iterator(); iter.hasNext();)
         addPermissions((Permission)iter.next());
   }
   
   /** @pdGenerated default add
     * @param newPermission */
   public void addPermissions(Permission newPermission) {
      if (newPermission == null)
         return;
      if (this.permissions == null)
         this.permissions = new java.util.Vector<Permission>();
      if (!this.permissions.contains(newPermission))
         this.permissions.add(newPermission);
   }
   
   /** @pdGenerated default remove
     * @param oldPermission */
   public void removePermissions(Permission oldPermission) {
      if (oldPermission == null)
         return;
      if (this.permissions != null)
         if (this.permissions.contains(oldPermission))
            this.permissions.remove(oldPermission);
   }
   
   /** @pdGenerated default removeAll */
   public void removeAllPermissions() {
      if (permissions != null)
         permissions.clear();
   }

}