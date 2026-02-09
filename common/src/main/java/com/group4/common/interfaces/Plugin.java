package com.group4.common.interfaces; /***********************************************************************
 * Module:  Plugin.java
 * Author:  Lenovo
 * Purpose: Defines the Interface Plugin
 ***********************************************************************/

/** @pdOid c8cba02f-7958-423b-aea1-29d5bb3f9453 */
public interface Plugin {
   /** @param context
    * @pdOid 638d1046-2029-4139-8a28-439663b118e8 */
   void init(HostContext context);
   /** @pdOid ab7a5b95-ac74-4c10-9cef-ea517f249940 */
   java.lang.String getName();
   /** @pdOid 42755801-90bb-4f3d-a34d-1d957a3f2d9e */
   void start();
   /** @pdOid 450a1bb3-f3de-4a2b-806a-daa3c446c99b */
   void stop();
   /** @pdOid c2c3b253-6329-4144-8f0d-a2c72eb15b3f */
   Long getId();
   /** @pdOid 582d2330-29b8-40b6-9d53-8ac3f1a7cdb0 */
   java.lang.String getVersion();

}