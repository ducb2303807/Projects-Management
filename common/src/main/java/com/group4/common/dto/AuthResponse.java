package com.group4.common.dto; /***********************************************************************
 * Module:  AuthResponse.java
 * Author:  Lenovo
 * Purpose: Defines the Class AuthResponse
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 501f6c3a-a7da-4181-8db4-b9bce1286562 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
   /** @pdOid 51b3b5a4-da8e-4b33-9038-b278fa8d864c */
   private java.lang.String token;
   /** @pdOid 86df68da-b199-44bf-af81-a6771ac48ac7 */
   private UserDTO user;
}