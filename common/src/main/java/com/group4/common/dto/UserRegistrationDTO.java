package com.group4.common.dto; /***********************************************************************
 * Module:  UserRegistrationDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserRegistrationDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid 6f1d4c7f-190c-4fc1-a988-1e587cc688e0 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO extends UserBaseDTO {
   /** @pdOid 7164cea3-1586-4ae5-850c-047dab159f8d */
   private java.lang.String password;

}