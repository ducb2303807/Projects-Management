package com.group4.common.dto; /***********************************************************************
 * Module:  UserDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserDTO
 ***********************************************************************/

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDTO extends UserBaseDTO {
   private Long id;
   private java.lang.String username;
   private boolean isActive;
   private java.lang.String systemRoleName;
}