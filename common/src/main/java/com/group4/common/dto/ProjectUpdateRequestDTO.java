package com.group4.common.dto; /***********************************************************************
 * Module:  ProjectUpdateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectUpdateRequestDTO
 ***********************************************************************/

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

/** @pdOid 2b75ad6e-a302-4a18-b410-a201bc9fbf85 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProjectUpdateRequestDTO extends ProjectBaseDTO {
   @NotNull(message = "Status ID cannot be null")
   private Long statusId;
}