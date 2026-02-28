package com.group4.common.dto; /***********************************************************************
 * Module:  UserBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserBaseDTO
 ***********************************************************************/

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid c1947970-0a64-4fb8-87c5-b7b769d6e515 */
@Data
@NoArgsConstructor
@AllArgsConstructor
abstract class UserBaseDTO {
   protected java.lang.String fullName;
   @Email
   @NotBlank( message = "Email cannot be blank")
   protected java.lang.String email;
   protected java.lang.String address;
}