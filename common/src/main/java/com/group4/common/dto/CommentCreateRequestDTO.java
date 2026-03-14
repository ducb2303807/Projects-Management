package com.group4.common.dto; /***********************************************************************
 * Module:  CommentCreateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentCreateRequestDTO
 ***********************************************************************/

import lombok.*;
import lombok.experimental.SuperBuilder;

/** @pdOid a8be8eb7-a378-403b-8c05-09b741660836 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommentCreateRequestDTO extends CommentBaseDTO {

   private Long taskId;

   private Long parentId;

   private Long projectMemberId;
}