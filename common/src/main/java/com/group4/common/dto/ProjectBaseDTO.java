package com.group4.common.dto; /***********************************************************************
 * Module:  ProjectBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectBaseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid 1d93d456-c62d-4010-84ec-821b38f47b5a */
@Data
@NoArgsConstructor
@AllArgsConstructor
abstract class ProjectBaseDTO {
   /** @pdOid 0424a3d8-12ef-4ebd-a1b0-01527bd7c0c6 */
   protected java.lang.String projectName;
   /** @pdOid 565c0453-e1b0-43a0-88ef-56597d76fdcb */
   protected java.lang.String description;
   /** @pdOid 18938c0e-d5f2-430f-8777-1572c34e99ad */
   protected LocalDateTime startAt;
   /** @pdOid 66401136-bb5c-4036-8b17-166bdd62c43a */
   protected LocalDateTime endAt;

}