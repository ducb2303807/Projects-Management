package com.group4.common.dto; /***********************************************************************
 * Module:  UserUpdateDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserUpdateDTO
 ***********************************************************************/

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** @pdOid cb5a0887-d90a-4462-98f6-bb241d9297f9 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserUpdateDTO extends UserBaseDTO {
}