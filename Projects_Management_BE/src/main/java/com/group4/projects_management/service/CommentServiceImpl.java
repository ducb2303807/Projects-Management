package com.group4.projects_management.service;

import com.group4.common.dto.CommentDTO;
import com.group4.projects_management.entity.Comment;
import com.group4.projects_management.entity.ProjectMember;
import com.group4.projects_management.entity.Task;
import com.group4.projects_management.repository.ProjectMemberRepository;
import com.group4.projects_management.repository.TaskCommentRepository;
import com.group4.projects_management.repository.TaskRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment, Long> implements CommentService {

   private final TaskCommentRepository taskCommentRepository;
   private final TaskRepository taskRepository;
   private final ProjectMemberRepository projectMemberRepository;

   public CommentServiceImpl(TaskCommentRepository taskCommentRepository,
                             TaskRepository taskRepository,
                             ProjectMemberRepository projectMemberRepository) {
      super(taskCommentRepository);
      this.taskCommentRepository = taskCommentRepository;
      this.taskRepository = taskRepository;
      this.projectMemberRepository = projectMemberRepository;
   }

   @Override
   public List<CommentDTO> getCommentsByTask(Long taskId) {

      List<Comment> comments = taskCommentRepository.findByTaskId(taskId);

      return comments.stream()
              .map(this::convertToDTO)
              .collect(Collectors.toList());
   }

   @Override
   public CommentDTO createComment(Long taskId, Long projectMemberId, String text, Long replyCommentId) {

      Comment comment = new Comment();
      comment.setContent(text);
      comment.setCreateAt(LocalDateTime.now());

      Task task = taskRepository.findById(taskId)
              .orElseThrow(() -> new RuntimeException("Task not found"));
      comment.setTask(task);

      ProjectMember member = projectMemberRepository.findById(projectMemberId)
              .orElseThrow(() -> new RuntimeException("Project member not found"));
      comment.setMember(member);

      if (replyCommentId != null) {
         Comment parent = taskCommentRepository.findById(replyCommentId)
                 .orElseThrow(() -> new RuntimeException("Parent comment not found"));
         comment.setParent(parent);
      }

      Comment saved = taskCommentRepository.save(comment);

      return convertToDTO(saved);
   }

   private CommentDTO convertToDTO(Comment comment) {

      CommentDTO dto = new CommentDTO();

      dto.setCommentId(comment.getId());
      dto.setContent(comment.getContent());
      dto.setCreateAt(comment.getCreateAt());

      if (comment.getParent() != null) {
         dto.setParentId(comment.getParent().getId());
      }

      if (comment.getMember() != null) {
         dto.setUserName(comment.getMember().getUser().getUsername());
         dto.setFullName(comment.getMember().getUser().getFullName());
      }

      return dto;
   }
}