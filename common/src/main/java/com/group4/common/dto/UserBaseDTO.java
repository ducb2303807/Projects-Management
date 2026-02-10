package com.group4.common.dto; /***********************************************************************
 * Module:  UserBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserBaseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid c1947970-0a64-4fb8-87c5-b7b769d6e515 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserBaseDTO {
   /** @pdOid c9964c5e-0cb2-4bd0-b0b4-5559cf719820 */
   private java.lang.String username;
   /** @pdOid 5d88243f-8050-46e5-af73-d847cfcd34c2 */
   private java.lang.String fullname;
   /** @pdOid ef87ba16-163c-4043-82d3-868f6d6b862d */
   private java.lang.String email;
   /** @pdOid 11d11e2a-17ed-49dd-8c5f-c6a024d7e6a5 */
   private java.lang.String address;

}