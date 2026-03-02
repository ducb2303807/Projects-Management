package com.group4.common.dto; /***********************************************************************
 * Module:  ProjectCreateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectCreateRequestDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 8b7c632b-7418-4bc6-8fb2-dac5e4e59473 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequestDTO extends ProjectBaseDTO {
   /** @pdOid e63b7911-e7d6-413b-86bf-b371fbfca20c */
   private Long createByUserId;
}