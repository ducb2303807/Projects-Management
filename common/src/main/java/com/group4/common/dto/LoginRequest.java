package com.group4.common.dto; /***********************************************************************
 * Module:  LoginRequest.java
 * Author:  Lenovo
 * Purpose: Defines the Class LoginRequest
 ***********************************************************************/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 21c8c80e-2c41-4357-a1a3-8f0127fca47a */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

   @NotBlank( message = "Username cannot be blank")
   private java.lang.String username;

   @NotBlank( message = "Password cannot be blank")
   @Size(min = 5, message = "Password must be at least 5 characters long")
   private java.lang.String password;
}