package com.group4.projects_management.service;

import com.group4.common.dto.CommentDTO;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.core.strategy.notification.comment.CommentNotificationContext;
import com.group4.projects_management.entity.Comment;
import com.group4.projects_management.entity.ProjectMember;
import com.group4.projects_management.entity.Task;
import com.group4.projects_management.entity.User;
import com.group4.projects_management.mapper.CommentMapper;
import com.group4.projects_management.repository.ProjectMemberRepository;
import com.group4.projects_management.repository.TaskAssignmentRepository;
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
   private final CommentMapper commentMapper;
   private final NotificationService notificationService;
   private final TaskAssignmentRepository taskAssignmentRepository;

   public CommentServiceImpl(TaskCommentRepository taskCommentRepository,
                             TaskRepository taskRepository,
                             ProjectMemberRepository projectMemberRepository, CommentMapper commentMapper, NotificationService notificationService, TaskAssignmentRepository taskAssignmentRepository) {
      super(taskCommentRepository);
      this.taskCommentRepository = taskCommentRepository;
      this.taskRepository = taskRepository;
      this.projectMemberRepository = projectMemberRepository;
       this.commentMapper = commentMapper;
       this.notificationService = notificationService;
       this.taskAssignmentRepository = taskAssignmentRepository;
   }

   @Override
   public List<CommentDTO> getCommentsByTask(Long taskId) {
      List<Comment> comments = taskCommentRepository.findByTaskId(taskId);

      return comments.stream()
              .map(commentMapper::toDto)
              .collect(Collectors.toList());
   }

   @Override
   public CommentDTO createComment(Long taskId, Long projectMemberId, String text, Long replyCommentId) {
      Task task = taskRepository.findById(taskId)
              .orElseThrow(() -> new RuntimeException("Task not found"));

      ProjectMember member = projectMemberRepository.findById(projectMemberId)
              .orElseThrow(() -> new RuntimeException("Project member not found"));

      User author = member.getUser();

      Comment comment = new Comment();
      comment.setContent(text);
      comment.setCreateAt(LocalDateTime.now());
      comment.setTask(task);
      comment.setMember(member);

      User parentAuthor = null;
      if (replyCommentId != null) {
         Comment parent = taskCommentRepository.findById(replyCommentId)
                 .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
         comment.setParent(parent);
         parentAuthor = parent.getMember().getUser();
      }

      Comment saved = taskCommentRepository.save(comment);

      CommentNotificationContext context = CommentNotificationContext.builder()
              .task(task)
              .author(author)
              .parentAuthor(parentAuthor)
              .build();

      if (replyCommentId != null) {
         // reply
         if (!parentAuthor.getId().equals(author.getId())) {
            notificationService.send(parentAuthor.getId(), context, task.getId());
         }
      } else {
         // normal
         List<Long> assigneeIds = taskAssignmentRepository.findByTask_Id(taskId)
                 .stream()
                 .map(a -> a.getAssignee().getUser().getId())
                 .filter(id -> !id.equals(author.getId())) // Loại bỏ chính mình
                 .toList();

         if (!assigneeIds.isEmpty()) {
            notificationService.send(assigneeIds, context, task.getId());
         }
      }

      return commentMapper.toDto(saved);
   }
}