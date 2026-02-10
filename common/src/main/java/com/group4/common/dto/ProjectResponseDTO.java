package com.group4.common.dto; /***********************************************************************
 * Module:  ProjectResponseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectResponseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid f0107505-70d5-42a9-8580-ebf3ecce3738 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO extends ProjectBaseDTO {
   /** @pdOid 231b9935-45b0-4bf9-b424-964462659cec */
   private Long projectId;
   /** @pdOid 1ca8890b-4adc-430d-8c80-f6b072a4c571 */
   private java.lang.String userCreatedUsername;
   /** @pdOid bc4d7272-c477-4e44-a9f6-114bb6c26d01 */
   private java.lang.String userCreatedFullName;
   /** @pdOid bf10ca1c-7e01-4b7c-8e46-b356d23bf0cf */
   private java.lang.String statusName;
   /** @pdOid 44989061-4ea8-4f45-b1f2-d4e70f5ecb7b */
   private LocalDateTime updateAt;
   /** @pdOid 26404511-d0a3-4f19-8966-0d2b273ebf2c */
   private LocalDateTime createdAt;
   /** @pdOid ebf2bb09-330f-4268-abce-c1c2dea3716e */
   private int memberCount;

}