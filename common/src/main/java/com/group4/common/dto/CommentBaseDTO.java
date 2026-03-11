package com.group4.common.dto; /***********************************************************************
 * Module:  CommentBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentBaseDTO
 ***********************************************************************/

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** @pdOid 3be23bac-306a-4541-b159-33a29ed66c50 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
abstract class CommentBaseDTO {
   @NotBlank( message = "Content cannot be blank")
   private java.lang.String content;
}