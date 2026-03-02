package com.group4.projects_management_fe.core.plugin; /***********************************************************************
 * Module:  PluginLoader.java
 * Author:  Lenovo
 * Purpose: Defines the Class PluginLoader
 ***********************************************************************/


import com.group4.common.interfaces.Plugin;
import java.util.List;

/* use Pf4j Loader alternative manual plugin reflection */
/** @pdOid 165ae04e-984b-44a4-a7bc-528331f0ed41 */
public class ManualPluginLoader implements PluginLoader {
   /** @pdRoleInfo migr=no name=Plugin assc=association13 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Aggregation */
   public java.util.Collection<Plugin> plugin;
   
   /** @pdOid b146dcb0-6db8-4238-a258-f8ceef5f0ad7 */
   public Plugin loadPluginFromJar() {
      // TODO: implement
      return null;
   }
   
   /** @param folderPath
    * @pdOid b14c9e3e-b5ca-4a92-b55d-8cd501a1dedc */
   public List<Plugin> scanPlugins(java.lang.String folderPath) {
      // TODO: implement
      return null;
   }
   
   /** @pdOid 7eaaf660-f75d-4587-ab52-ab163434693e */
   public boolean validatePlugin() {
      // TODO: implement
      return false;
   }
   
   
   /** @pdGenerated default getter */
   public java.util.Collection<Plugin> getPlugin() {
      if (plugin == null)
         plugin = new java.util.HashSet<Plugin>();
      return plugin;
   }
   
   /** @pdGenerated default iterator getter */
   public java.util.Iterator getIteratorPlugin() {
      if (plugin == null)
         plugin = new java.util.HashSet<Plugin>();
      return plugin.iterator();
   }
   
   /** @pdGenerated default setter
     * @param newPlugin */
   public void setPlugin(java.util.Collection<Plugin> newPlugin) {
      removeAllPlugin();
      for (java.util.Iterator iter = newPlugin.iterator(); iter.hasNext();)
         addPlugin((Plugin)iter.next());
   }
   
   /** @pdGenerated default add
     * @param newPlugin */
   public void addPlugin(Plugin newPlugin) {
      if (newPlugin == null)
         return;
      if (this.plugin == null)
         this.plugin = new java.util.HashSet<Plugin>();
      if (!this.plugin.contains(newPlugin))
         this.plugin.add(newPlugin);
   }
   
   /** @pdGenerated default remove
     * @param oldPlugin */
   public void removePlugin(Plugin oldPlugin) {
      if (oldPlugin == null)
         return;
      if (this.plugin != null)
         if (this.plugin.contains(oldPlugin))
            this.plugin.remove(oldPlugin);
   }
   
   /** @pdGenerated default removeAll */
   public void removeAllPlugin() {
      if (plugin != null)
         plugin.clear();
   }

   @Override
   public void loadPlugins() {

   }
}