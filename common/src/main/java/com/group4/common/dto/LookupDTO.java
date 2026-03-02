package com.group4.common.dto; /***********************************************************************
 * Module:  BaseLookupDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class BaseLookupDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 6f9a3baf-74eb-4d83-8443-e8e351dfbd66 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupDTO {
   private String id;
   private java.lang.String name;
   private java.lang.String description;
}