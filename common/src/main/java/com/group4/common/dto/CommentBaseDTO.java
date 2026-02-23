package com.group4.common.dto; /***********************************************************************
 * Module:  CommentBaseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentBaseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @pdOid 3be23bac-306a-4541-b159-33a29ed66c50 */
@Data
@NoArgsConstructor
@AllArgsConstructor
abstract class CommentBaseDTO {
   /** @pdOid 663643e1-0cb9-49c9-b1bf-fd9d00445be3 */
   protected java.lang.String content;

}