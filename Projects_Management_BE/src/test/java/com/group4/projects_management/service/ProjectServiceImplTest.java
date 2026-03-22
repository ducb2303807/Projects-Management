package com.group4.projects_management.service;

import com.group4.common.dto.*;
import com.group4.common.enums.*;
import com.group4.projects_management.core.strategy.notification.invitation.ProjectInvitationContext;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.mapper.ProjectMapper;
import com.group4.projects_management.mapper.ProjectMemberMapper;
import com.group4.projects_management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectRoleRepository projectRoleRepository;
    @Mock private ProjectStatusRepository projectStatusRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private ProjectMemberStatusRepository projectMemberStatusRepository;
    @Mock private ProjectMapper projectMapper;
    @Mock private ProjectMemberMapper projectMemberMapper;
    @Mock private TaskRepository taskRepository;
    @Mock private NotificationService notificationService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User mockUser;
    private Project mockProject;
    private ProjectMember mockInviter;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("Admin User");

        mockProject = new Project();
        mockProject.setId(10L);
        mockProject.setName("Test Project");

        mockInviter = new ProjectMember();
        mockInviter.setUser(mockUser);
        mockInviter.setProject(mockProject);
    }

    // =========================================================================================
    // 1. NHÓM TEST QUẢN LÝ VÒNG ĐỜI DỰ ÁN (CREATE, UPDATE, DELETE, GET DETAILS)
    // =========================================================================================
    @Nested
    @DisplayName("Project Core Operations Tests")
    class ProjectCoreOperationsTests {

        @Test
        @DisplayName("Create Project - Success")
        void createProject_Success() {
            ProjectCreateRequestDTO dto = new ProjectCreateRequestDTO();
            ProjectRole pmRole = new ProjectRole();
            ProjectStatus activeStatus = new ProjectStatus();
            ProjectMemberStatus memberActiveStatus = new ProjectMemberStatus();

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(projectRoleRepository.findBySystemCode(ProjectMemberRoleCode.PM.name())).thenReturn(Optional.of(pmRole));
            when(projectStatusRepository.findBySystemCode(ProjectStatusCode.ACTIVE.name())).thenReturn(Optional.of(activeStatus));
            when(projectMemberStatusRepository.findBySystemCode(MemberStatusCode.ACTIVE.name())).thenReturn(Optional.of(memberActiveStatus));

            when(projectMapper.toCreateEntity(dto)).thenReturn(mockProject);
            when(projectRepository.save(any())).thenReturn(mockProject);
            when(projectMemberMapper.createInitialMember(any(), any(), any(), any())).thenReturn(new ProjectMember());
            when(projectMapper.toDto(any())).thenReturn(new ProjectResponseDTO());

            ProjectResponseDTO result = projectService.createProject(1L, dto);

            assertNotNull(result);
            verify(projectRepository).save(any(Project.class));
            verify(projectMemberRepository).save(any(ProjectMember.class));
        }

        @Test
        @DisplayName("Create Project - Throws exception when userId is null")
        void createProject_NullUserId_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () ->
                    projectService.createProject(null, new ProjectCreateRequestDTO())
            );
        }

        @Test
        @DisplayName("Update Project - Success and Notify Members")
        void updateProject_Success() {
            Long projectId = 10L;
            Long actorId = 1L;
            ProjectUpdateRequestDTO dto = new ProjectUpdateRequestDTO();
            dto.setStatusId(2L);

            ProjectStatus newStatus = new ProjectStatus();
            newStatus.setId(2L);

            ProjectMember member = new ProjectMember();
            User otherUser = new User();
            otherUser.setId(5L);
            member.setUser(otherUser);
            mockProject.setMembers(new ArrayList<>(List.of(member)));

            when(userRepository.findById(actorId)).thenReturn(Optional.of(mockUser));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
            when(projectStatusRepository.findById(2L)).thenReturn(Optional.of(newStatus));
            when(projectRepository.save(any())).thenReturn(mockProject);
            when(projectMapper.toDto(any())).thenReturn(new ProjectResponseDTO());

            projectService.updateProject(projectId, dto, actorId);

            verify(projectMapper).updateProjectFromDto(dto, mockProject);
            verify(notificationService).send(eq(List.of(5L)), any(), eq(projectId));
        }

        @Test
        @DisplayName("Delete Project - Set Cancelled and Notify Members")
        void deleteProject_Success() {
            Long projectId = 10L;
            Long actorId = 1L;

            User otherUser = new User();
            otherUser.setId(2L);
            ProjectMember m1 = new ProjectMember();
            m1.setUser(mockUser);
            ProjectMember m2 = new ProjectMember();
            m2.setUser(otherUser);
            mockProject.setMembers(List.of(m1, m2));

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
            when(userRepository.findById(actorId)).thenReturn(Optional.of(mockUser));
            when(projectStatusRepository.findBySystemCode(ProjectStatusCode.CANCELLED.name()))
                    .thenReturn(Optional.of(new ProjectStatus()));

            projectService.deleteProject(projectId, actorId);

            verify(projectRepository).save(mockProject);
            verify(notificationService).send(eq(List.of(2L)), any(), eq(projectId));
        }

        @Test
        @DisplayName("Get Project Detail - Success")
        void getProjectDetail_Success() {
            when(projectRepository.findByIdWithMembers(10L)).thenReturn(Optional.of(mockProject));

            projectService.getProjectDetail(10L);

            verify(projectMapper).toDto(mockProject);
        }
    }

    // =========================================================================================
    // 2. NHÓM TEST QUẢN LÝ THÀNH VIÊN (INVITE, REMOVE, UPDATE ROLE/STATUS)
    // =========================================================================================
    @Nested
    @DisplayName("Member Management Tests")
    class MemberManagementTests {

        @Test
        @DisplayName("Invite Members - New and Re-invite logic")
        void inviteMembers_Success() {
            Long projectId = 10L;
            Long inviterId = 1L;

            MemberInviteRequest req1 = new MemberInviteRequest(2L, 100L);
            MemberInviteRequest req2 = new MemberInviteRequest(3L, 100L);

            User user2 = new User(); user2.setId(2L);
            User user3 = new User(); user3.setId(3L);
            ProjectRole role = new ProjectRole(); role.setId(100L);
            ProjectMemberStatus pendingStatus = new ProjectMemberStatus();

            ProjectMember leftMember = new ProjectMember();
            leftMember.setUser(user3);
            leftMember.setLeftAt(LocalDateTime.now().minusDays(1));

            when(projectMemberRepository.findByProject_IdAndUser_Id(projectId, inviterId)).thenReturn(Optional.of(mockInviter));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
            when(projectMemberStatusRepository.findBySystemCode(MemberStatusCode.PENDING.name())).thenReturn(Optional.of(pendingStatus));

            when(userRepository.findAllById(anySet())).thenReturn(List.of(user2, user3));
            when(projectRoleRepository.findAllById(anySet())).thenReturn(List.of(role));
            when(projectMemberRepository.findAllByProject_IdAndUser_IdIn(eq(projectId), anyList())).thenReturn(List.of(leftMember));

            when(projectMemberRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

            projectService.inviteMembers(projectId, List.of(req1, req2), inviterId);

            verify(projectMemberRepository).saveAll(argThat(list -> ((Collection<?>) list).size() == 2));
            verify(notificationService, times(2)).send(anyLong(), any(ProjectInvitationContext.class), any());
        }

        @Test
        @DisplayName("Invite Members - Empty List Throws Exception")
        void inviteMembers_EmptyList_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () ->
                    projectService.inviteMembers(10L, List.of(), 1L)
            );
        }

        @Test
        @DisplayName("Remove Member From Project - Success")
        void removeMemberFromProject_Success() {
            Long memberId = 100L;
            Long requesterId = 1L;

            User userToBeRemoved = new User();
            userToBeRemoved.setId(99L);
            userToBeRemoved.setFullName("User bị xóa");

            ProjectMember member = spy(new ProjectMember());
            member.setProject(mockProject);
            member.setUser(userToBeRemoved);

            when(projectMemberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(userRepository.findById(requesterId)).thenReturn(Optional.of(mockUser));

            ProjectMemberStatus leftStatus = new ProjectMemberStatus();
            leftStatus.setSystemCode(MemberStatusCode.LEFT.name());
            when(projectMemberStatusRepository.findBySystemCode(MemberStatusCode.LEFT.name()))
                    .thenReturn(Optional.of(leftStatus));

            projectService.removeMemberFromProject(memberId, requesterId);

            verify(member).leave();
            verify(projectMemberRepository).save(member);
            verify(notificationService).send(eq(99L), any(), eq(10L));
        }

        @Test
        @DisplayName("Leave Project - Success and Notify Managers")
        void leaveProject_Success() {
            Long memberId = 100L;
            ProjectMember member = spy(new ProjectMember());
            member.setProject(mockProject);
            member.setUser(mockUser);

            ProjectMember manager = new ProjectMember();
            User managerUser = new User();
            managerUser.setId(9L);
            manager.setUser(managerUser);
            ProjectRole pmRole = new ProjectRole();
            pmRole.setSystemCode(ProjectMemberRoleCode.PM.name());
            manager.setProjectRole(pmRole);

            mockProject.setMembers(new ArrayList<>(List.of(member, manager)));

            when(projectMemberRepository.findById(memberId)).thenReturn(Optional.of(member));

            projectService.leaveProject(memberId);

            verify(member).leave();
            verify(notificationService).send(eq(List.of(9L)), any(), any());
        }

        @Test
        @DisplayName("Update Member Status - Removed case")
        void updateMemberStatus_Removed() {
            Long memberId = 100L;
            ProjectMemberUpdateDTO request = new ProjectMemberUpdateDTO();
            request.setStatus(MemberStatusCode.REMOVED);

            ProjectMember member = spy(new ProjectMember());
            member.setUser(mockUser);
            member.setProject(mockProject);

            ProjectMemberStatus statusEntity = new ProjectMemberStatus();
            statusEntity.setSystemCode(MemberStatusCode.REMOVED.name());

            when(projectMemberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(projectMemberStatusRepository.findBySystemCode(MemberStatusCode.REMOVED.name())).thenReturn(Optional.of(statusEntity));

            projectService.updateMemberStatus(memberId, request);

            verify(member).leave();
            verify(projectMemberRepository).save(member);
            verify(notificationService).send(anyLong(), any(), any());
        }

        @Test
        @DisplayName("Update Member Status - Left case should notify managers")
        void updateMemberStatus_Left() {
            Long memberId = 100L;
            ProjectMemberUpdateDTO request = new ProjectMemberUpdateDTO();
            request.setStatus(MemberStatusCode.LEFT);

            ProjectMember member = spy(new ProjectMember());
            member.setProject(mockProject);
            member.setUser(mockUser);

            ProjectMember manager = new ProjectMember();
            User managerUser = new User();
            managerUser.setId(88L);
            manager.setUser(managerUser);
            ProjectRole pmRole = new ProjectRole();
            pmRole.setSystemCode(ProjectMemberRoleCode.PM.name());
            manager.setProjectRole(pmRole);
            mockProject.setMembers(List.of(member, manager));

            ProjectMemberStatus statusEntity = new ProjectMemberStatus();
            statusEntity.setSystemCode(MemberStatusCode.LEFT.name());

            when(projectMemberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(projectMemberStatusRepository.findBySystemCode(MemberStatusCode.LEFT.name())).thenReturn(Optional.of(statusEntity));

            projectService.updateMemberStatus(memberId, request);

            verify(member).leave();
            verify(notificationService).send(eq(List.of(88L)), any(), any());
        }

        @Test
        @DisplayName("Update Member Role - Success")
        void updateMemberRole_Success() {
            ProjectMember member = new ProjectMember();
            ProjectRole newRole = new ProjectRole();
            newRole.setId(5L);

            when(projectMemberRepository.findById(100L)).thenReturn(Optional.of(member));
            when(projectRoleRepository.findById(5L)).thenReturn(Optional.of(newRole));

            projectService.updateMemberRole(100L, 5L);

            assertEquals(newRole, member.getProjectRole());
            verify(projectMemberRepository).save(member);
        }

        @Test
        @DisplayName("Get Members Of Project - Success")
        void getMembersOfProject_Success() {
            ProjectMember activeMember = new ProjectMember();
            mockProject.setMembers(new ArrayList<>(List.of(activeMember)));

            when(projectRepository.findById(10L)).thenReturn(Optional.of(mockProject));

            projectService.getMembersOfProject(10L);

            verify(projectMemberMapper, atLeastOnce()).toDto(any());
        }
    }

    // =========================================================================================
    // 3. NHÓM TEST XỬ LÝ LỜI MỜI THAM GIA DỰ ÁN
    // =========================================================================================
    @Nested
    @DisplayName("Project Invitation Handling Tests")
    class ProjectInvitationTests {

        @Test
        @DisplayName("Handle Invitation - Accept Success")
        void handleInvitation_Accept_Success() {
            Long invitationId = 100L;
            InvitationRequestDTO request = new InvitationRequestDTO();
            request.setAction(InvitationAction.ACCEPT);

            ProjectMember member = spy(new ProjectMember());
            member.setUser(mockUser);
            member.setProject(mockProject);

            ProjectMember manager = new ProjectMember();
            manager.setUser(new User());
            mockProject.setMembers(new ArrayList<>(List.of(manager)));

            when(projectMemberRepository.findById(invitationId)).thenReturn(Optional.of(member));
            when(projectMemberStatusRepository.findBySystemCode(MemberStatusCode.ACTIVE.name())).thenReturn(Optional.of(new ProjectMemberStatus()));

            projectService.handleInvitation(invitationId, request);

            assertNotNull(member.getJoinAt());
            verify(projectMemberRepository).save(member);
            verify(notificationService).send(anyList(), any(), any());
        }

        @Test
        @DisplayName("Handle Invitation - Decline Success")
        void handleInvitation_Decline_Success() {
            Long invitationId = 100L;
            InvitationRequestDTO request = new InvitationRequestDTO();
            request.setAction(InvitationAction.DECLINE);

            ProjectMember member = new ProjectMember();
            when(projectMemberRepository.findById(invitationId)).thenReturn(Optional.of(member));

            projectService.handleInvitation(invitationId, request);

            verify(projectMemberRepository).delete(member);
        }

        @Test
        @DisplayName("Get Pending Invitations - Success")
        void getPendingInvitations_Success() {
            Long userId = 1L;
            when(projectMemberRepository.findAllByUser_IdAndProjectMemberStatus_SystemCode(eq(userId), eq(MemberStatusCode.PENDING.name())))
                    .thenReturn(List.of(new ProjectMember()));

            projectService.getPendingInvitations(userId);

            verify(projectMemberMapper).toInvitationDto(any());
        }
    }

    // =========================================================================================
    // 4. NHÓM TEST TRUY VẤN VÀ THỐNG KÊ (QUERIES & STATISTICS)
    // =========================================================================================
    @Nested
    @DisplayName("Queries and Statistics Tests")
    class QueriesAndStatisticsTests {

        @Test
        @DisplayName("Get Project Statistics - Calculate correctly")
        void getProjectStatistics_Success() {
            Long projectId = 10L;
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
            when(taskRepository.countByProjectIdAndStatusNot(projectId, TaskStatusCode.CANCELLED.name())).thenReturn(10);
            when(taskRepository.countByProject_IdAndTaskStatus_SystemCode(projectId, TaskStatusCode.DONE.name())).thenReturn(4);
            when(taskRepository.countByProject_IdAndTaskStatus_SystemCode(projectId, TaskStatusCode.IN_PROGRESS.name())).thenReturn(3);

            ProjectStatsDTO stats = projectService.getProjectStatistics(projectId);

            assertEquals(10, stats.getTotalTasks());
            assertEquals(4, stats.getCompletedTasks());
            assertEquals(40.0, stats.getProgressPercentage());
        }

        @Test
        @DisplayName("Get Project Statistics - Zero tasks case (No ArithmeticException)")
        void getProjectStatistics_ZeroTasks() {
            when(projectRepository.findById(10L)).thenReturn(Optional.of(mockProject));
            when(taskRepository.countByProjectIdAndStatusNot(anyLong(), anyString())).thenReturn(0);

            ProjectStatsDTO stats = projectService.getProjectStatistics(10L);

            assertEquals(0.0, stats.getProgressPercentage());
        }

        @Test
        @DisplayName("Get Projects By User - Include Cancelled")
        void getProjectsByUserId_IncludeCancelled() {
            Long userId = 1L;
            when(projectMemberRepository.findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(anyLong(), anyString()))
                    .thenReturn(List.of(mockInviter));
            when(projectMapper.toDto(any())).thenReturn(new ProjectResponseDTO());

            List<ProjectResponseDTO> result = projectService.getProjectsByUserId(userId, true);

            assertNotNull(result);
            verify(projectMemberRepository).findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(eq(userId), eq(MemberStatusCode.ACTIVE.name()));
        }

        @Test
        @DisplayName("Get Projects By User - Active Only")
        void getProjectsByUserId_ActiveOnly() {
            Long userId = 1L;
            when(projectMemberRepository.findActiveProjectsForUser(anyLong(), anyString(), anyString()))
                    .thenReturn(List.of(mockInviter));
            when(projectMapper.toDto(any())).thenReturn(new ProjectResponseDTO());

            List<ProjectResponseDTO> result = projectService.getProjectsByUserId(userId, false);

            assertNotNull(result);
            verify(projectMemberRepository).findActiveProjectsForUser(eq(userId), eq(MemberStatusCode.ACTIVE.name()), eq(TaskStatusCode.CANCELLED.name()));
        }
    }
}
