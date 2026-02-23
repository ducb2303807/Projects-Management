package com.group4.common.dto; /***********************************************************************
 * Module:  InvitationDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class InvitationDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid f77d72a0-4eaa-4b22-ba16-2b18cf389b62 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO extends MemberBaseDTO {
   /** @pdOid 25248f65-20d9-4063-8316-7e80e83fe3e2 */
   private Long projectMemberId;
   /** @pdOid ed0e70ee-34d8-41c5-8cc5-c7d5031c49b6 */
   private java.lang.String projectName;
   /** @pdOid 0d2d6624-8303-46f9-9aa1-fe757862694c */
   private java.lang.String inviterName;
   /** @pdOid c633d603-d1f2-4ba8-93a3-c21f0967dce8 */
   private LocalDateTime sentAt;
}