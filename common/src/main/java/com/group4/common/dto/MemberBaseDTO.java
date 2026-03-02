package com.group4.common.dto; /***********************************************************************
 * Module:  MemberBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class MemberBaseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 9193756f-f687-4466-8c01-b5b9594679d3 */
@Data
@NoArgsConstructor
@AllArgsConstructor
abstract class MemberBaseDTO {
   /** @pdOid ab59159b-0378-489e-b27a-3a3b132f5e87 */
   protected java.lang.String roleName;
   /** @pdOid 66c306ce-ce1b-454e-a9a7-2e3e2c703011 */
   protected java.lang.String statusName;
}