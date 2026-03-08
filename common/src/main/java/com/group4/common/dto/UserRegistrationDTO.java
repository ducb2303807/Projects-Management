package com.group4.common.dto; /***********************************************************************
 * Module:  UserRegistrationDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserRegistrationDTO
 ***********************************************************************/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** @pdOid 6f1d4c7f-190c-4fc1-a988-1e587cc688e0 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO extends UserBaseDTO {
   @NotBlank( message = "Username cannot be blank")
   protected java.lang.String username;
   @NotBlank( message = "Password is required")
   @Size(min = 6, message = "Password must be at least 6 characters long")
   private java.lang.String password;
}