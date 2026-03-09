package com.group4.common.dto; /***********************************************************************
 * Module:  UserWidgetConfigDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserWidgetConfigDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 6afbb6fb-c598-496c-af68-d799ec8ca2c5 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWidgetConfigDTO {

   private Long widgetConfigId;

   private Long pluginId;

   private java.lang.String pluginName;

   private Boolean isVisible;

   private int posX;

   private int posY;

}