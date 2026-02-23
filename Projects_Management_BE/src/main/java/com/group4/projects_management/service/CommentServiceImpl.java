/***********************************************************************
 * Module:  CommentServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentServiceImpl
 ***********************************************************************/

package com.group4.projects_management.service;

import com.group4.common.dto.CommentDTO;
import com.group4.projects_management.entity.Comment;
import com.group4.projects_management.repository.TaskCommentRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment,Long> implements CommentService {
   /** @pdRoleInfo migr=no name=TaskCommentRepository assc=association32 mult=1..1 */
   private final TaskCommentRepository taskCommentRepository;

   public CommentServiceImpl(TaskCommentRepository taskCommentRepository) {
      super(taskCommentRepository);
      this.taskCommentRepository = taskCommentRepository;
   }

   @Override
   public List<CommentDTO> getCommentsByTask(Long taskId) {
      return List.of();
   }

   @Override
   public CommentDTO createComment(Long taskId, Long projectMemberId, String text, Long replyCommentId) {
      return null;
   }
}