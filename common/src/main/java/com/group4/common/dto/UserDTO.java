package com.group4.common.dto; /***********************************************************************
 * Module:  UserDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends UserBaseDTO {
   private Long id;
   protected java.lang.String username;
   private boolean isActive;
   private java.lang.String systemRoleName;
}