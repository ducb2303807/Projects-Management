package com.group4.common.dto; /***********************************************************************
 * Module:  UserDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserDTO
 ***********************************************************************/

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO extends UserBaseDTO {
   private Long id;
   protected java.lang.String username;
   private boolean isActive;
   private java.lang.String systemRoleName;
}