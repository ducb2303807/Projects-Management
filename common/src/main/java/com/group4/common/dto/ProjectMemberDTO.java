package com.group4.common.dto; /***********************************************************************
 * Module:  ProjectMemberDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectMemberDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid 3aa9ca1f-e669-47ae-bbb1-057f69951cec */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO extends MemberBaseDTO {
   /** @pdOid 497856ae-20e3-4f70-bea3-4ed1c018927a */
   private Long projectMemberId;
   /** @pdOid 2e933ee9-4b68-4dc6-9e9f-52ee6c7e6264 */
   private Long userId;
   /** @pdOid 9e52c739-e92c-46fc-85e3-67c2a8b54bf6 */
   private java.lang.String username;
   /** @pdOid cb6f4911-9d44-4e13-81c1-f06d47545b32 */
   private java.lang.String fullname;
   /** @pdOid 358fa326-6881-4936-b916-b2cba40d8b6a */
   private java.lang.String email;
   /** @pdOid cf6a32e7-188b-48b1-a25f-d7a34fa4a89a */
   private LocalDateTime joinDate;

}