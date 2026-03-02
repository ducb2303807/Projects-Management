package com.group4.common.dto; /***********************************************************************
 * Module:  LoginRequest.java
 * Author:  Lenovo
 * Purpose: Defines the Class LoginRequest
 ***********************************************************************/

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 21c8c80e-2c41-4357-a1a3-8f0127fca47a */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
   /** @pdOid 4603558d-7934-420b-afbb-0fe713b6e127 */
   @NotBlank( message = "Username cannot be blank")
   private java.lang.String username;
   /** @pdOid 71bb78d9-87a7-4627-b581-47d82d1e3329 */
   @NotBlank( message = "Password cannot be blank")
   @Min( value = 6, message = "Password must be at least 6 characters long")
   private java.lang.String password;
}