package com.group4.common.dto; /***********************************************************************
 * Module:  ProjectBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectBaseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/** @pdOid 1d93d456-c62d-4010-84ec-821b38f47b5a */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
abstract class ProjectBaseDTO {
   private java.lang.String projectName;
   private java.lang.String description;
   private LocalDateTime startDate;
   private LocalDateTime endDate;
}