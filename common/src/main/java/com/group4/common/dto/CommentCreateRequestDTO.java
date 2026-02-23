package com.group4.common.dto; /***********************************************************************
 * Module:  CommentCreateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentCreateRequestDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** @pdOid a8be8eb7-a378-403b-8c05-09b741660836 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequestDTO extends CommentBaseDTO {
   /** @pdOid 61a049e9-9457-4e0b-9b1a-6d17286b19c9 */
   private Long taskId;
   /** @pdOid a8a754a1-038b-4ba4-bb06-3913ef355fd2 */
   private Long parentId;
   /** @pdOid 54cbacb0-6cbd-4603-9b1c-69b27a02f0cd */
   private Long projectMemberId;
}