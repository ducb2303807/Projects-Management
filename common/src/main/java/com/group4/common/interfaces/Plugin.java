package com.group4.common.interfaces;

import java.util.List;


public interface Plugin {
   void init(HostContext context);
   java.lang.String getName();
   void start();
   void stop();
   Long getId();
   java.lang.String getVersion();
   List<WidgetProvider> getWidgetProviders();
}