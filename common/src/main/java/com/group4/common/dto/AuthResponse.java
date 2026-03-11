package com.group4.common.dto; /***********************************************************************
 * Module:  AuthResponse.java
 * Author:  Lenovo
 * Purpose: Defines the Class AuthResponse
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 501f6c3a-a7da-4181-8db4-b9bce1286562 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
   private java.lang.String token;
   private UserDTO user;
}