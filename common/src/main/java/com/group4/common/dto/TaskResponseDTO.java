package com.group4.common.dto; /***********************************************************************
 * Module:  TaskResponseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskResponseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** @pdOid a88a633b-faff-47e9-b176-39d5649e641a */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO extends TaskBaseDTO {
   /** @pdOid 17bf6769-7058-4aea-b78e-8bec7f242f2d */
   private Long taskId;
   /** @pdOid a992803b-8abd-4169-ad0d-8de007111a33 */
   private java.lang.String statusName;
   /** @pdOid e019add9-42da-4100-a916-ff41591b1018 */
   private java.lang.String priorityName;
   /** @pdOid c152bbcf-e2ec-414b-8544-cb5467e6d178 */
   private LocalDateTime createdAt;
   /** @pdOid d3afb28f-2f74-4b61-8f77-a7d7605106c1 */
   private List<TaskAssigneeDTO> assignees;

}