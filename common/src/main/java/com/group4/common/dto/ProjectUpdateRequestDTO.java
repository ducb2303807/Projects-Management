package com.group4.common.dto; /***********************************************************************
 * Module:  ProjectUpdateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectUpdateRequestDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 2b75ad6e-a302-4a18-b410-a201bc9fbf85 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequestDTO extends ProjectBaseDTO {
   /** @pdOid 26cc8fc1-1c98-44dd-bf90-32306ad02c57 */
   private Long statusId;

}