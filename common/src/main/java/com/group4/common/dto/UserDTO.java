package com.group4.common.dto; /***********************************************************************
 * Module:  UserDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid e21573cd-451b-4ed9-9ad6-89c8c433da72 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends UserBaseDTO {
   /** @pdOid 6de4b207-efb8-4b49-b4f1-104978cc0138 */
   private Long userId;
   /** @pdOid d7c2a91a-e918-46cd-b723-3076c586962f */
   private boolean isActive;
   /** @pdOid c87c62fe-b595-4faa-a269-3e4f57ef5eaa */
   private java.lang.String systemRoleName;

}