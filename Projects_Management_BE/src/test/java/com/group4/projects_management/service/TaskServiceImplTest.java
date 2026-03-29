package com.group4.projects_management.service;

import com.group4.common.dto.TaskCreateRequestDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.common.enums.MemberStatusCode;
import com.group4.common.enums.TaskStatusCode;
import com.group4.projects_management.core.exception.BusinessException;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.mapper.TaskAssignmentMapper;
import com.group4.projects_management.mapper.TaskHistoryMapper;
import com.group4.projects_management.mapper.TaskMapper;
import com.group4.projects_management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskHistoryRepository taskHistoryRepository;
    @Mock
    private TaskAssignmentRepository taskAssignmentRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private PriorityRepository priorityRepository;
    @Mock
    private TaskStatusRepository taskStatusRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberStatusRepository projectMemberStatusRepository;
    @Mock
    private TaskAssignmentMapper taskAssignmentMapper;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private TaskHistoryMapper taskHistoryMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task mockTask;
    private Project mockProject;
    private ProjectMember mockMember;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockProject = new Project();
        mockProject.setId(1L);

        mockTask = new Task();
        mockTask.setId(100L);
        mockTask.setProject(mockProject);
        mockTask.setName("Test Task");

        mockUser = new User();
        mockUser.setId(10L);

        mockMember = new ProjectMember();
        mockMember.setId(50L);
        mockMember.setUser(mockUser);
        mockMember.setProject(mockProject);
    }

    @Nested
    @DisplayName("Tests for createProject")
    class CreateTaskTests {
        @Test
        @DisplayName("Create Task - Should throw exception when deadline is in the past")
        void createTask_PastDeadline_ThrowsException() {
            TaskCreateRequestDTO dto = new TaskCreateRequestDTO();
            dto.setDeadline(LocalDateTime.now().minusDays(1)); // Quá khứ

            assertThrows(BusinessException.class, () -> taskService.createTask(dto));
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Create Task - Success")
        void createTask_Success() {
            TaskCreateRequestDTO dto = new TaskCreateRequestDTO();
            dto.setDeadline(LocalDateTime.now().plusDays(5));
            dto.setPriorityId(1L);
            dto.setProjectId(1L);
            dto.setTaskStatusId(1L);

            when(priorityRepository.findById(anyLong())).thenReturn(Optional.of(new Priority()));
            when(projectRepository.findById(anyLong())).thenReturn(Optional.of(mockProject));
            when(taskStatusRepository.findById(anyLong())).thenReturn(Optional.of(new TaskStatus()));
            when(taskMapper.toEntity(any(), any(), any(), any())).thenReturn(mockTask);
            when(taskRepository.save(any())).thenReturn(mockTask);
            when(taskMapper.toDto(any())).thenReturn(new TaskResponseDTO());

            TaskResponseDTO result = taskService.createTask(dto);

            assertNotNull(result);
            verify(taskRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Tests for assignMember")
    class AssignmentTests {
        @Test
        @DisplayName("Assign Member - Success and notify")
        void assignMember_Success() {
            Long taskId = 100L;
            Long assigneeId = 50L;
            Long assignerUserId = 10L;

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
            when(projectMemberRepository.findById(assigneeId)).thenReturn(Optional.of(mockMember));
            when(projectMemberRepository.findByUser_IdAndProject_Id(assignerUserId, 1L))
                    .thenReturn(Optional.of(mockMember)); // Assigner cũng là member

            taskService.assignMember(taskId, assigneeId, assignerUserId);

            verify(taskAssignmentRepository).save(any(TaskAssignment.class));
            verify(notificationService).send(eq(10L), any(), eq(100L));
        }

        @Test
        @DisplayName("Assign Multiple Members - Success")
        void assignMembers_Success() {
            List<Long> assigneeIds = List.of(50L, 51L);
            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(mockTask));
            when(projectMemberRepository.findByUser_IdAndProject_Id(anyLong(), anyLong())).thenReturn(Optional.of(mockMember));
            when(projectMemberRepository.findAllById(anyList())).thenReturn(List.of(mockMember));

            taskService.assignMembers(100L, assigneeIds, 10L);

            verify(taskAssignmentRepository).saveAll(anyList());
            verify(notificationService).send(anyList(), any(), anyLong());
        }

        @Test
        @DisplayName("Assign Members - Do nothing when list is null or empty")
        void assignMembers_EmptyList_ReturnsEarly() {
            taskService.assignMembers(100L, List.of(), 10L);
            taskService.assignMembers(100L, null, 10L);

            // Đảm bảo repository không bao giờ được gọi
            verify(taskAssignmentRepository, never()).saveAll(any());
        }
    }

    @Nested
    @DisplayName("Tests for updateTaskStatus")
    class UpdateStatusTests {
        @Test
        @DisplayName("Update Status - Success and notify assigned members")
        void updateTaskStatus_Success() {
            TaskStatus status = new TaskStatus();
            status.setName("IN_PROGRESS");

            // Giả lập task có 2 người đang được giao
            Task spyTask = spy(mockTask);
            when(spyTask.getMembersId()).thenReturn(List.of(10L, 11L));

            when(taskRepository.findById(100L)).thenReturn(Optional.of(spyTask));
            when(taskStatusRepository.findById(2L)).thenReturn(Optional.of(status));

            taskService.updateTaskStatus(100L, 2L);

            verify(taskRepository).save(spyTask);
            // Verify gửi thông báo cho 2 người
            verify(notificationService).send(eq(List.of(10L, 11L)), any(), eq(100L));
        }
    }

    @Nested
    @DisplayName("Tests for deleteTask")
    class DeleteTaskTests {
        @Test
        @DisplayName("Delete Task - Should change status to CANCELLED and notify members")
        void deleteTask_Success() {
            // Arrange
            TaskStatus cancelledStatus = new TaskStatus();
            cancelledStatus.setName("CANCELLED");

            Task spyTask = spy(mockTask);
            doReturn(List.of(10L, 11L)).when(spyTask).getMembersId();

            when(taskRepository.findById(100L)).thenReturn(Optional.of(spyTask));
            when(taskStatusRepository.findBySystemCode(TaskStatusCode.CANCELLED.name()))
                    .thenReturn(Optional.of(cancelledStatus));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

            taskService.deleteTask(100L, 10L);

            assertEquals(cancelledStatus, spyTask.getTaskStatus());
            verify(taskRepository).save(spyTask);

            verify(notificationService).send(eq(List.of(10L, 11L)), any(), eq(100L));
        }
    }

    @Nested
    @DisplayName("Tests for removeMembers")
    class RemoveMemberTests {
        @Test
        @DisplayName("Remove Members - Success and notify unassigned")
        void removeMembersFromTask_Success() {
            List<Long> memberIds = List.of(50L);

            // 1. Setup 2 User riêng biệt để không bị lộn ID
            User mockRequesterUser = new User();
            mockRequesterUser.setId(10L);
            mockRequesterUser.setUsername("Admin");

            User mockRemovedUser = new User();
            mockRemovedUser.setId(50L);
             mockRemovedUser.setUsername("UserDeleted");

            ProjectMember mockRequesterMember = new ProjectMember();
            mockRequesterMember.setUser(mockRequesterUser);

            ProjectMember mockRemovedMember = new ProjectMember();
            mockRemovedMember.setUser(mockRemovedUser);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(projectMemberRepository.findByUser_IdAndProject_Id(eq(10L), any()))
                    .thenReturn(Optional.of(mockRequesterMember));
            when(projectMemberRepository.findAllById(memberIds))
                    .thenReturn(List.of(mockRemovedMember));

            taskService.removeMembersFromTask(100L, memberIds, 10L);

            verify(taskAssignmentRepository).deleteByTaskIdAndProjectMemberIdIn(100L, memberIds);
            verify(notificationService).send(eq(List.of(50L)), any(), eq(100L));
        }

        @Test
        @DisplayName("Remove Members - Do nothing when list is empty")
        void removeMembersFromTask_EmptyList_ReturnsEarly() {
            taskService.removeMembersFromTask(100L, new ArrayList<>(), 10L);
            verify(taskAssignmentRepository, never()).deleteByTaskIdAndProjectMemberIdIn(any(), any());
        }
    }

    @Nested
    @DisplayName("Tests for Query Methods")
    class TaskQueryTests {

        @Test
        @DisplayName("Get Tasks By Project - Branch includeCancelled = true")
        void getTasksByProject_IncludeCancelled() {
            when(taskRepository.findByProject_Id(1L)).thenReturn(List.of(mockTask));
            when(taskMapper.toDto(any())).thenReturn(new TaskResponseDTO());

            List<TaskResponseDTO> result = taskService.getTasksByProject(1L, true);

            assertEquals(1, result.size());
            verify(taskRepository).findByProject_Id(1L);
        }

        @Test
        @DisplayName("Get Tasks By Project - Branch includeCancelled = false")
        void getTasksByProject_ActiveOnly() {
            when(taskRepository.findActiveTasksByProjectId(anyLong(), anyString(), anyString()))
                    .thenReturn(List.of(mockTask));

            taskService.getTasksByProject(1L, false);

            verify(taskRepository).findActiveTasksByProjectId(1L, "CANCELLED", "CANCELLED");
        }

        @Test
        @DisplayName("Get Tasks By User - Test both branches")
        void getTasksByUserId_Test() {
            // Case 1: includeCancelled = true
            when(taskAssignmentRepository.findAllTasksForUser(anyLong(), anyString())).thenReturn(List.of(mockTask));
            taskService.getTasksByUserId(10L, true);
            verify(taskAssignmentRepository).findAllTasksForUser(10L, MemberStatusCode.ACTIVE.name());

            // Case 2: includeCancelled = false
            when(taskAssignmentRepository.findActiveTasksForUser(anyLong(), anyString(), anyString(), anyString()))
                    .thenReturn(List.of(mockTask));
            taskService.getTasksByUserId(10L, false);
            verify(taskAssignmentRepository).findActiveTasksForUser(eq(10L), anyString(), eq(TaskStatusCode.CANCELLED.name()), anyString());
        }

        @Test
        @DisplayName("Get Task History - Success")
        void getTaskHistory_Success() {
            when(taskHistoryRepository.findByTaskId(100L)).thenReturn(List.of(new TaskHistory()));
            taskService.getTaskHistory(100L);
            verify(taskHistoryMapper).toDto(any());
        }

        @Test
        @DisplayName("Get All Tasks - Success")
        void getAllTasks_Success() {
            when(taskRepository.findAll()).thenReturn(List.of(mockTask));
            when(taskMapper.toDto(any())).thenReturn(new TaskResponseDTO());

            List<TaskResponseDTO> result = taskService.getAllTasks();

            assertEquals(1, result.size());
            verify(taskRepository).findAll();
        }

        @Test
        @DisplayName("Get Tasks By Status - Success")
        void getTasksByStatus_Success() {
            when(taskRepository.findByProject_IdAndTaskStatus_Id(1L, 2L)).thenReturn(List.of(mockTask));
            when(taskMapper.toDto(any())).thenReturn(new TaskResponseDTO());

            List<TaskResponseDTO> result = taskService.getTasksByStatus(1L, 2L);

            assertEquals(1, result.size());
            verify(taskRepository).findByProject_IdAndTaskStatus_Id(1L, 2L);
        }
    }

    @Nested
    @DisplayName("Tests for Update Logic")
    class TaskUpdateTests {

        @Test
        @DisplayName("Update Task Priority - Success")
        void updateTaskPriority_Success() {
            Priority newPriority = new Priority();
            newPriority.setId(2L);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(priorityRepository.findById(2L)).thenReturn(Optional.of(newPriority));

            taskService.updateTaskPriority(100L, 2L);

            assertEquals(newPriority, mockTask.getPriority());
            verify(taskRepository).save(mockTask);
        }

        @Test
        @DisplayName("Update Task - Complete flow with filtered notification and history event")
        void updateTask_FullFlow() {
            // 1. Arrange
            Long taskId = 100L;
            Long actorId = 10L;
            Long otherMemberId = 20L;

            TaskUpdateDTO dto = new TaskUpdateDTO();
            dto.setName("New Task Name");
            dto.setPriorityId(2L);
            dto.setStatusId(3L);

            Priority p = new Priority();
            p.setId(2L);
            p.setName("High");
            TaskStatus s = new TaskStatus();
            s.setId(3L);
            s.setName("In Progress");

            Priority oldP = new Priority();
            oldP.setId(1L);
            oldP.setName("Low");
            TaskStatus oldS = new TaskStatus();
            oldS.setId(1L);
            oldS.setName("Todo");

            Project spyProject = spy(mockProject);
            Task spyTask = spy(mockTask);
            spyTask.setName("Old Task Name");
            spyTask.setProject(spyProject);
            spyTask.setPriority(oldP);
            spyTask.setTaskStatus(oldS);

            ProjectMember mockActorMember = new ProjectMember();
            mockActorMember.setUser(mockUser); // mockUser ID = 10L

            // Stubbing Spies
            doReturn(List.of(actorId, otherMemberId)).when(spyTask).getMembersId();
            doReturn(new ArrayList<ProjectMember>()).when(spyProject).getProjectManagers();

            // Mock Repositories
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(spyTask));
            when(priorityRepository.findById(2L)).thenReturn(Optional.of(p));
            when(taskStatusRepository.findById(3L)).thenReturn(Optional.of(s));
            when(projectMemberRepository.findByUser_IdAndProject_Id(eq(actorId), anyLong()))
                    .thenReturn(Optional.of(mockActorMember));
            when(taskRepository.save(any())).thenReturn(spyTask);
            when(taskMapper.toDto(any())).thenReturn(new TaskResponseDTO());

            // 2. Act
            taskService.updateTask(taskId, dto, actorId);

            verify(taskMapper).updateEntityFromDto(eq(dto), eq(spyTask), eq(p), eq(s));

            ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
            verify(notificationService).send(captor.capture(), any(), eq(taskId));

            List<Long> capturedIds = captor.getValue();
            assertTrue(capturedIds.contains(20L));
            assertFalse(capturedIds.contains(10L));

            verify(eventPublisher, times(1)).publishEvent(any(Object.class));
        }

        @Nested
        @DisplayName("Tests for Exceptions")
        class ExceptionTests {
            @Test
            @DisplayName("Any Method - Should throw ResourceNotFoundException when Task not found")
            void taskNotFound_ThrowsException() {
                when(taskRepository.findById(999L)).thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> taskService.updateTaskPriority(999L, 1L));
            }
        }
    }
}