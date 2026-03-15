package com.group4.common.dto; /***********************************************************************
 * Module:  InvitationDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class InvitationDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/** @pdOid f77d72a0-4eaa-4b22-ba16-2b18cf389b62 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvitationDTO extends MemberBaseDTO {

   private Long projectMemberId;

   private java.lang.String projectName;

   private java.lang.String inviterName;

   private LocalDateTime sentAt;
}