package com.group4.common.dto; /***********************************************************************
 * Module:  TaskAssigneeDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskAssigneeDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 25139825-5f59-4ef3-ad29-733e3edbd0cd */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssigneeDTO {
   /** @pdOid 855e2f12-7257-4f16-bc1a-2ebddf1a7ff5 */
   private Long projectMemberId;
   /** @pdOid 005ab338-81f5-40ac-bd64-51b2c2fd1d22 */
   private Long userId;
   /** @pdOid 8ea4acfe-6d1d-4f66-8fe5-d2845a9fad74 */
   private java.lang.String fullName;

}