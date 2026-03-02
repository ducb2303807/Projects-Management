package com.group4.projects_management.service; /***********************************************************************
 * Module:  CommentService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface CommentService
 ***********************************************************************/

import com.group4.common.dto.CommentDTO;

import java.util.*;

/** @pdOid 4d75d74f-75b9-4200-a5ce-a1e52fd1736c */
public interface CommentService {
   /** @param taskId
    * @pdOid 94a813e8-88f2-4390-a7cf-0d5fec1e0c21 */
   List<CommentDTO> getCommentsByTask(Long taskId);
   /** @param taskId 
    * @param projectMemberId 
    * @param text 
    * @param replyCommentId
    * @pdOid 7ed2bf78-6bb6-4098-b780-6d2827d0e5e4 */
   CommentDTO createComment(Long taskId, Long projectMemberId, java.lang.String text, Long replyCommentId);
}