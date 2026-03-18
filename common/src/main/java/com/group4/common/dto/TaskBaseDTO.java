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
   /** @pdOid 82f2b31e-1218-4973-9732-05c55f588ebe */
   @NotBlank ( message = "Task name cannot be blank")
   protected java.lang.String name;
   /** @pdOid 24bb26a6-9aff-4645-9e37-2db2bb542814 */
   protected java.lang.String description;
   /** @pdOid 796aae6b-6c2f-4e63-baf2-6753f65f0625 */
   @NotNull ( message = "Deadline cannot be null")
   protected LocalDateTime deadline;

}