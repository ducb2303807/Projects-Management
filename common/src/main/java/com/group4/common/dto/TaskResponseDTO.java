package com.group4.common.dto; /***********************************************************************
 * Module:  TaskResponseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskResponseDTO
 ***********************************************************************/

import java.time.LocalDateTime;
import java.util.*;

/** @pdOid a88a633b-faff-47e9-b176-39d5649e641a */
public class TaskResponseDTO extends TaskBaseDTO {
   /** @pdOid 17bf6769-7058-4aea-b78e-8bec7f242f2d */
   private Long taskId;
   /** @pdOid a992803b-8abd-4169-ad0d-8de007111a33 */
   private java.lang.String statusName;
   /** @pdOid e019add9-42da-4100-a916-ff41591b1018 */
   private java.lang.String priorityName;
   /** @pdOid c152bbcf-e2ec-414b-8544-cb5467e6d178 */
   private LocalDateTime createAt;
   /** @pdOid 4aadc7a4-f347-42bc-b25f-b5a70ce7fc9b */
   private List<TaskAssigneeDTO> assignees;

}