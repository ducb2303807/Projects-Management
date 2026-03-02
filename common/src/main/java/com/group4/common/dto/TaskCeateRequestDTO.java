package com.group4.common.dto; /***********************************************************************
 * Module:  TaskCeateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskCeateRequestDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid b1ba0251-dd62-459e-a0a3-1f2650a2b5f9 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCeateRequestDTO extends TaskBaseDTO {
   /** @pdOid e5aa353c-b3c4-4b81-8183-4b570b8356b9 */
   private Long projectId;
   /** @pdOid 7ac0eb29-41ae-4662-891a-29675b034b73 */
   private Long priorityId;

}