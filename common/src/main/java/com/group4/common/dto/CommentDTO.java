package com.group4.common.dto; /***********************************************************************
 * Module:  CommentDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommentDTO extends CommentBaseDTO {
   private Long commentId;
   private Long parentId;
   private java.lang.String userName;
   private java.lang.String fullName;
   private LocalDateTime createAt;
}