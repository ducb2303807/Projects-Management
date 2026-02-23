package com.group4.common.dto; /***********************************************************************
 * Module:  UserWidgetConfigDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserWidgetConfigDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 6afbb6fb-c598-496c-af68-d799ec8ca2c5 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWidgetConfigDTO {
   /** @pdOid d6b71760-a4b1-49c0-933f-b9241988d8e4 */
   private Long widgetConfigId;
   /** @pdOid f209cc8e-10b5-422a-b72d-804f96358449 */
   private Long pluginId;
   /** @pdOid c6b06bbf-0736-42e8-9616-f4eaf5d7950c */
   private java.lang.String pluginName;
   /** @pdOid 54a55a22-d44c-40f1-859d-4c3d9d3d2900 */
   private Boolean isVisible;
   /** @pdOid 0d5e6ff0-e0f3-450b-9f5c-81124d192f3e */
   private int posX;
   /** @pdOid 333c6fa5-f2ef-4405-bfcd-d5bb1f8b647e */
   private int posY;

}