package com.group4.common.dto; /***********************************************************************
 * Module:  TaskBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskBaseDTO
 ***********************************************************************/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/** @pdOid b4486e18-bb57-4470-a479-3dbe3a90490b */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
abstract class TaskBaseDTO {
   @NotBlank ( message = "Task name cannot be blank")
   protected java.lang.String name;
   protected java.lang.String description;
   @NotNull ( message = "Deadline cannot be null")
   protected LocalDateTime deadline;
}