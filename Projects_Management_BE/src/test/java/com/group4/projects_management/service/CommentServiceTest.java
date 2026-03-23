package com.group4.projects_management.service;

import com.group4.common.dto.CommentDTO;
import com.group4.projects_management.core.strategy.notification.comment.CommentNotificationContext;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.mapper.CommentMapper;
import com.group4.projects_management.repository.ProjectMemberRepository;
import com.group4.projects_management.repository.TaskAssignmentRepository;
import com.group4.projects_management.repository.TaskCommentRepository;
import com.group4.projects_management.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private TaskCommentRepository taskCommentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private TaskAssignmentRepository taskAssignmentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Task mockTask;
    private User mockUser;
    private ProjectMember mockMember;

    @BeforeEach
    void setUp() {
        mockTask = new Task();
        mockTask.setId(1L);

        mockUser = new User();
        mockUser.setId(100L);

        mockMember = new ProjectMember();
        mockMember.setId(10L);
        mockMember.setUser(mockUser);
    }

    @Test
    @DisplayName("Get comments by task ID - Success")
    void getCommentsByTask_Success() {
        Comment comment = new Comment();
        when(taskCommentRepository.findByTaskId(1L)).thenReturn(List.of(comment));
        when(commentMapper.toDto(any())).thenReturn(new CommentDTO());

        List<CommentDTO> result = commentService.getCommentsByTask(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskCommentRepository, times(1)).findByTaskId(1L);
    }

    @Test
    @DisplayName("Create top-level comment - Notify assignees")
    void createComment_TopLevel_Success() {
        // Arrange
        String text = "Hello world";
        User assigneeUser = new User();
        assigneeUser.setId(200L); // Khác với author id (100L)

        ProjectMember assigneeMember = new ProjectMember();
        assigneeMember.setUser(assigneeUser);

        TaskAssignment assignment = new TaskAssignment();
        assignment.setAssignee(assigneeMember);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(projectMemberRepository.findById(10L)).thenReturn(Optional.of(mockMember));
        when(taskAssignmentRepository.findByTask_Id(1L)).thenReturn(List.of(assignment));
        when(taskCommentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);
        when(commentMapper.toDto(any())).thenReturn(new CommentDTO());

        commentService.createComment(1L, 10L, text, null);

        verify(taskCommentRepository).save(any(Comment.class));
        verify(notificationService).send(eq(List.of(200L)), any(CommentNotificationContext.class), eq(1L));
    }

    @Test
    @DisplayName("Create reply comment - Notify parent author")
    void createComment_Reply_Success() {
        // Arrange
        Long parentCommentId = 50L;
        User parentAuthor = new User();
        parentAuthor.setId(300L);

        ProjectMember parentMember = new ProjectMember();
        parentMember.setUser(parentAuthor);

        Comment parentComment = new Comment();
        parentComment.setMember(parentMember);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(projectMemberRepository.findById(10L)).thenReturn(Optional.of(mockMember));
        when(taskCommentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(taskCommentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);

        commentService.createComment(1L, 10L, "Reply text", parentCommentId);

        // Phải gửi cho parent author (300L)
        verify(notificationService).send(eq(300L), any(CommentNotificationContext.class), eq(1L));
        verify(taskAssignmentRepository, never()).findByTask_Id(any());
    }

    @Test
    @DisplayName("Create comment - Task not found - Throw Exception")
    void createComment_TaskNotFound_ThrowsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                commentService.createComment(1L, 10L, "text", null)
        );
    }

    @Test
    @DisplayName("Reply to own comment - No notification sent")
    void createComment_ReplyToSelf_NoNotification() {
        Long parentCommentId = 50L;
        Comment parentComment = new Comment();
        parentComment.setMember(mockMember);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(projectMemberRepository.findById(10L)).thenReturn(Optional.of(mockMember));
        when(taskCommentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(taskCommentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        commentService.createComment(1L, 10L, "Reply to myself", parentCommentId);

        // verify notificationService KHÔNG được gọi send
        verify(notificationService, never()).send(anyLong(), any(), anyLong());
    }
}
