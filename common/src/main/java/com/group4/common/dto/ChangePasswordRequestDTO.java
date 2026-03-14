package com.group4.common.dto; /***********************************************************************
 * Module:  ChangePasswordRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ChangePasswordRequestDTO
 ***********************************************************************/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 963d8f2a-0767-4f31-ad57-205e6e63e895 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequestDTO {

   @NotBlank
   @Size( min = 5, message = "oldPassword must be at least 5 characters long")
   private java.lang.String oldPassword;

   @NotBlank
   @Size( min = 5, message = "newPassword must be at least 5 characters long")
   private java.lang.String newPassword;

}