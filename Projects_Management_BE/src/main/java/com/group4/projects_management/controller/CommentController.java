package com.group4.projects_management.controller; /***********************************************************************
 * Module:  CommentController.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentController
 ***********************************************************************/

import com.group4.common.dto.CommentCreateRequestDTO;
import com.group4.common.dto.CommentDTO;
import com.group4.projects_management.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** @pdOid 98320d4a-632b-4031-ac14-b0d540fb9dfd */
@RestController
public class CommentController {
   /** @pdRoleInfo migr=no name=CommentService assc=association33 mult=1..1 */

   @Autowired
   private CommentService commentService;
   
   /** @param taskId
    * @pdOid a62f1736-f2f9-46f0-bbdb-7d7c8e03093a */
   public ResponseEntity<List<CommentDTO>> getCommentsByTask(Long taskId) {
      // TODO: implement
      return null;
   }
   
   /** @param request
    * @pdOid ececf358-9443-4ee6-bbe4-d7de220e4334 */
   public ResponseEntity<CommentDTO> createComment(CommentCreateRequestDTO request) {
      // TODO: implement
      return null;
   }
   
   /** @param parentId 
    * @param request
    * @pdOid a2ec5e5e-9a92-4032-97d6-e4612d27ac17 */
   public ResponseEntity<CommentDTO> replyComment(Long parentId, CommentCreateRequestDTO request) {
      // TODO: implement
      return null;
   }

}