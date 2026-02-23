package com.group4.common.dto; /***********************************************************************
 * Module:  PluginDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class PluginDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 46510657-5606-4ae1-a51c-1029e8cdc797 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginDTO {
   /** @pdOid 904c0c56-cb72-41c9-a297-0f8e7a9b751a */
   private Long id;
   /** @pdOid 73386ad8-12ff-49d1-9276-9097d5c0d60e */
   private java.lang.String name;
   /** @pdOid 1bad25ca-8ba6-46fd-b457-95a866cbe557 */
   private java.lang.String version;
   /** @pdOid f33be37b-c52a-46a8-a7bc-eb02a5244c42 */
   private java.lang.String description;
   /** @pdOid d3dffff7-ab9b-42ab-825f-1024538ba67c */
   private java.lang.String mainClass;
   /** @pdOid be12ab42-60ea-4828-8b0a-d2b55bb4c030 */
   private java.lang.String path;

}