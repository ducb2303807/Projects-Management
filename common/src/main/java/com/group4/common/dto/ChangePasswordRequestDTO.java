package com.group4.common.dto; /***********************************************************************
 * Module:  ChangePasswordRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class ChangePasswordRequestDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 963d8f2a-0767-4f31-ad57-205e6e63e895 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {
   /** @pdOid ea622504-d6a2-4325-8a65-ae08107f229d */
   private java.lang.String oldPassword;
   /** @pdOid a2dcabc0-3c51-41b4-a653-30fd78376ed2 */
   private java.lang.String newPassword;

}