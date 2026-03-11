package com.group4.common.dto; /***********************************************************************
 * Module:  MemberBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class MemberBaseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** @pdOid 9193756f-f687-4466-8c01-b5b9594679d3 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
abstract class MemberBaseDTO {
   private java.lang.String roleName;
   private java.lang.String statusName;
}