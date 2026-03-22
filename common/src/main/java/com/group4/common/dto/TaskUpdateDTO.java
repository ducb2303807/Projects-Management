package com.group4.common.dto; /***********************************************************************
 * Module:  TaskUpdateDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskUpdateDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 817effc2-bf87-4834-be32-d3ff3c481840 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDTO extends TaskBaseDTO {
   private Long statusId;
   private Long priorityId;
}