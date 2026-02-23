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
   /** @pdOid c9964c5e-0cb2-4bd0-b0b4-5559cf719820 */
   @NotBlank( message = "Username cannot be blank")
   protected java.lang.String username;
   /** @pdOid 5d88243f-8050-46e5-af73-d847cfcd34c2 */
   protected java.lang.String fullName;
   /** @pdOid ef87ba16-163c-4043-82d3-868f6d6b862d */
   @Email
   @NotBlank( message = "Email cannot be blank")
   protected java.lang.String email;
   /** @pdOid 11d11e2a-17ed-49dd-8c5f-c6a024d7e6a5 */
   protected java.lang.String address;
}