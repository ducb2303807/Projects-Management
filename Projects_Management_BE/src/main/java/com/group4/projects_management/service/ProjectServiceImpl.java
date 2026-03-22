package com.group4.projects_management.service; /***********************************************************************
 * Module:  ProjectServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectServiceImpl
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.common.enums.MemberStatusCode;
import com.group4.common.enums.ProjectStatusCode;
import com.group4.common.enums.TaskStatusCode;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.core.strategy.notification.invitation.MemberJoinContext;
import com.group4.projects_management.core.strategy.notification.invitation.MemberLeftContext;
import com.group4.projects_management.core.strategy.notification.invitation.MemberRemovedContext;
import com.group4.projects_management.core.strategy.notification.invitation.ProjectInvitationContext;
import com.group4.projects_management.core.strategy.notification.project.ProjectDeleteContext;
import com.group4.projects_management.core.strategy.notification.project.ProjectUpdatedContext;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.mapper.ProjectMapper;
import com.group4.projects_management.mapper.ProjectMemberMapper;
import com.group4.projects_management.repository.*;
import com.group4.projects_management.service.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @pdOid 8ed7a716-6d3c-4f74-ae7a-3d4f7734e7a8
 */
@Service
@Slf4j
public class ProjectServiceImpl extends BaseServiceImpl<Project, Long> implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberStatusRepository projectMemberStatusRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    private final ApplicationEventPublisher eventPublisher;

    public ProjectServiceImpl(ProjectRepository repository, UserRepository userRepository, ProjectRoleRepository projectRoleRepository, ProjectStatusRepository projectStatusRepository, ProjectMemberRepository projectMemberRepository, ProjectMemberStatusRepository projectMemberStatusRepository, ProjectMapper projectMapper, ProjectMemberMapper projectMemberMapper, TaskRepository taskRepository, NotificationService notificationService, ApplicationEventPublisher eventPublisher) {
        super(repository);
        this.projectRepository = repository;
        this.userRepository = userRepository;
        this.projectRoleRepository = projectRoleRepository;
        this.projectStatusRepository = projectStatusRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberStatusRepository = projectMemberStatusRepository;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAllWithMembers()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }


    @Override
    @Transactional
    public void inviteMembers(Long projectId, List<MemberInviteRequest> inviteRequests, Long inviterUserId) {
        if (inviteRequests == null || inviteRequests.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mời không được trống.");
        }

        // 1. Tiền xử lý Input: Lọc null & Gom nhóm để chống trùng lặp từ chính request gửi lên
        // Nếu FE gửi lên 2 request cho cùng 1 userId, ta giữ lại role của request đầu tiên
        Map<Long, Long> validUserRoleMap = inviteRequests.stream()
                .filter(req -> req.getUserId() != null && req.getRoleId() != null)
                .collect(Collectors.toMap(
                        MemberInviteRequest::getUserId,
                        MemberInviteRequest::getRoleId,
                        (existingRole, newRole) -> existingRole
                ));

        if (validUserRoleMap.isEmpty()) return;

        // 2. Fetch dữ liệu dùng chung
        var inviter = projectMemberRepository.findByProject_IdAndUser_Id(projectId, inviterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Inviter not found in project."));

        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        var pendingStatus = projectMemberStatusRepository.findBySystemCode("PENDING")
                .orElseThrow(() -> new RuntimeException("Cấu hình hệ thống lỗi: Thiếu status PENDING"));

        // 3. Batch Fetching (Lấy dữ liệu hàng loạt)
        Set<Long> userIds = validUserRoleMap.keySet();
        Set<Long> roleIds = new HashSet<>(validUserRoleMap.values());

        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, ProjectRole> roleMap = projectRoleRepository.findAllById(roleIds).stream()
                .collect(Collectors.toMap(ProjectRole::getId, r -> r));

        List<ProjectMember> existingMembers = projectMemberRepository.findAllByProject_IdAndUser_IdIn(projectId, userIds.stream().toList());
        Map<Long, ProjectMember> existingMemberMap = existingMembers.stream()
                .collect(Collectors.toMap(m -> m.getUser().getId(), m -> m));

        LocalDateTime now = LocalDateTime.now();
        List<ProjectMember> membersToSave = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : validUserRoleMap.entrySet()) {
            Long userId = entry.getKey();
            Long roleId = entry.getValue();

            // Bỏ qua nếu User hoặc Role gửi lên là ID ảo không có trong DB
            if (!userMap.containsKey(userId) || !roleMap.containsKey(roleId)) continue;

            ProjectMember existingMember = existingMemberMap.get(userId);

            if (existingMember != null) {
                if (existingMember.getLeftAt() != null) {
                    existingMember.setProjectMemberStatus(pendingStatus);
                    existingMember.setProjectRole(roleMap.get(roleId));
                    existingMember.setInvitedBy(inviter);
                    existingMember.setInvitedAt(now);
                    existingMember.setLeftAt(null);
                    existingMember.setJoinAt(null);

                    membersToSave.add(existingMember);
                }
            } else
            // chưa từng giam giá
            {
                ProjectMember newMember = new ProjectMember();
                newMember.setProject(project);
                newMember.setUser(userMap.get(userId));
                newMember.setProjectRole(roleMap.get(roleId));
                newMember.setProjectMemberStatus(pendingStatus);
                newMember.setInvitedBy(inviter);
                newMember.setInvitedAt(now);

                membersToSave.add(newMember);
            }
        }

        if (membersToSave.isEmpty()) {
            log.warn("Không có thành viên nào đủ điều kiện để mời trong dự án {}", projectId);
            return;
        }

        // 5. Lưu và Gửi thông báo
        List<ProjectMember> savedMembers = projectMemberRepository.saveAll(membersToSave);

        savedMembers.forEach(m -> {
            ProjectInvitationContext context = new ProjectInvitationContext(project,
                    inviter.getUser(),
                    m.getProjectRole());

            notificationService.send(m.getUser().getId(), context, m.getId());
        });
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long memberId, ProjectMemberUpdateDTO request) {
        ProjectMember member = projectMemberRepository.findById(memberId).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy thành viên")
        );

        String targetCode = request.getStatus().name();

        var statusEntity = projectMemberStatusRepository.findBySystemCode(targetCode)
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình trạng thái: " + targetCode));

        if (statusEntity.getSystemCode().equalsIgnoreCase(MemberStatusCode.REMOVED.name())) {
            member.leave();
            notifyMemberBeingRemoved(member);
        }
        if (targetCode.equalsIgnoreCase(MemberStatusCode.LEFT.name())) {
            member.leave();
            notifyManagersAboutMemberLeft(member);
        }

        member.setProjectMemberStatus(statusEntity);
        projectMemberRepository.save(member);
    }

    @Override
    @Transactional
    public void handleInvitation(Long invitationId, InvitationRequestDTO request) {
        var invitation = projectMemberRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Project invitation not found"));

        switch (request.getAction()) {
            case ACCEPT -> handleAccept(invitation);
            case DECLINE -> handleDecline(invitation);
            default -> throw new IllegalArgumentException("Invalid invitation type");
        }

        try {
            notificationService.updateMetadataResponseByRef(invitationId.toString(), request.getAction().name());
        } catch (Exception e) {
            System.err.println("Error updating metadata: " + e.getMessage());
        }
    }

    @Override
    public List<InvitationDTO> getPendingInvitations(Long userId) {
        var invitations = this.projectMemberRepository
                .findAllByUser_IdAndProjectMemberStatus_SystemCode(userId, "PENDING")
                .stream()
                .map(this.projectMemberMapper::toInvitationDto)
                .toList();

        return invitations;
    }

    @Override
    @Transactional
    public void removeMemberFromProject(Long projectMemberId, Long requesterId) {
        var member = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên trong project"));

        var requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        var leftStatus = projectMemberStatusRepository.findBySystemCode(MemberStatusCode.LEFT.name())
                .orElseThrow(() -> new RuntimeException("System code not found: " + MemberStatusCode.LEFT.name()));

        member.setProjectMemberStatus(leftStatus);
        member.leave();

        projectMemberRepository.save(member);

        notificationService.send(member.getUser().getId(),
                new MemberRemovedContext(member.getProject(),
                        member.getUser(),
                        requester
                ),
                member.getProject().getId());
    }

    @Override
    @Transactional
    public void updateMemberRole(Long projectMemberId, Long roleId) {
        var member = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên trong project"));

        var newRole = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role"));

        member.setProjectRole(newRole);

        projectMemberRepository.save(member);
    }

    @Override
    public ProjectStatsDTO getProjectStatistics(Long projectId) {
        // Đảm bảo project tồn tại
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án"));

        int totalActiveTasks = taskRepository.countByProjectIdAndStatusNot(projectId, TaskStatusCode.CANCELLED.name());
        int completedTasks = taskRepository.countByProject_IdAndTaskStatus_SystemCode(projectId, TaskStatusCode.DONE.name());
        int inProgressTasks = taskRepository.countByProject_IdAndTaskStatus_SystemCode(projectId, TaskStatusCode.IN_PROGRESS.name());

        double progressPercentage = totalActiveTasks == 0 ? 0.0 :
                (double) completedTasks / totalActiveTasks * 100.0;

        return new ProjectStatsDTO(
                totalActiveTasks,
                completedTasks,
                inProgressTasks,
                progressPercentage
        );
    }

    // Lấy tất cả project mà user tham gia
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByUserId(Long userId, boolean includeCancelled) {
        List<ProjectMember> members;
        if (includeCancelled) {
            members = projectMemberRepository.findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(
                    userId, MemberStatusCode.ACTIVE.name());
        } else {
            members = projectMemberRepository.findActiveProjectsForUser(
                    userId, MemberStatusCode.ACTIVE.name(), TaskStatusCode.CANCELLED.name());
        }

        return members.stream()
                .map(ProjectMember::getProject)
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponseDTO updateProject(Long projectId, ProjectUpdateRequestDTO dto, Long actorId) {

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("actorId not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));


        projectMapper.updateProjectFromDto(dto, project);

        ProjectStatus status = projectStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Project status not found"));
        project.setProjectStatus(status);

        Project updated = projectRepository.save(project);
        List<Long> memberIds = updated.getActiveMembers().stream()
                .map(m -> m.getUser().getId())
                .toList();

        if (!memberIds.isEmpty()) {
            var updateContext = ProjectUpdatedContext.builder()
                    .actor(actor)
                    .project(project)
                    .build();

            notificationService.send(memberIds,
                    updateContext,
                    updated.getId());
        }

        return projectMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId, Long actorId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));

        ProjectStatus cancelledStatus = projectStatusRepository.findBySystemCode(ProjectStatusCode.CANCELLED.name())
                .orElseThrow(() -> new ResourceNotFoundException("Project status CANCELLED not found"));


        List<Long> memberIds = project.getActiveMembers().stream()
                .map(m -> m.getUser().getId())
                .filter(id -> !id.equals(actorId))
                .toList();

        String projectName = project.getName();

        project.setProjectStatus(cancelledStatus);
        projectRepository.save(project);

        if (!memberIds.isEmpty()) {
            ProjectDeleteContext deleteContext = ProjectDeleteContext.builder()
                    .projectName(projectName)
                    .actor(actor)
                    .build();

            notificationService.send(memberIds, deleteContext, projectId);
        }
    }


    @Override
    @Transactional
    public List<ProjectMemberDTO> getMembersOfProject(Long projectId) {
        var project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project!"));

        return project.getActiveMembers()
                .stream().map(this.projectMemberMapper::toDto)
                .toList();
    }

    @Override
    public void leaveProject(Long projectMemberId) {
        var member = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        member.leave();

        var receivedIds = member.getProject().getProjectManagers()
                .stream()
                .map(ProjectMember::getUser)
                .map(User::getId)
                .toList();

        notificationService.send(receivedIds,
                new MemberLeftContext(member.getProject(), member.getUser()),
                member.getProject().getId());
    }

    @Override
    @Transactional
    public ProjectResponseDTO createProject(Long userId, ProjectCreateRequestDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("Request cannot be null");

        if (userId == null) {
            throw new IllegalArgumentException("createByUserId is required");
        }

        var creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người tạo dự án"));

        var ownerRole = projectRoleRepository.findBySystemCode("PM")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectRole systemCode=PM"));

        var defaultProjectStatus = projectStatusRepository.findBySystemCode("ACTIVE")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectStatus systemCode=ACTIVE"));

        var activeMemberStatus = projectMemberStatusRepository.findBySystemCode("ACTIVE")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectMemberStatus systemCode=ACTIVE"));

        Project project = projectMapper.toCreateEntity(dto);
        project.setCreatedBy(creator);
        project.setProjectStatus(defaultProjectStatus);
        Project savedProject = projectRepository.save(project);


        ProjectMember creatorMember = projectMemberMapper.createInitialMember(
                savedProject, creator, ownerRole, activeMemberStatus
        );

        var saved = projectMemberRepository.save(creatorMember);

        if (savedProject.getMembers() == null) {
            savedProject.setMembers(new ArrayList<>());
        }
        savedProject.getMembers().add(saved);

        return projectMapper.toDto(savedProject);
    }

    @Override
    public ProjectResponseDTO getProjectDetail(Long projectId) {
        var project = projectRepository.findByIdWithMembers(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDto(project);
    }

    private void handleAccept(ProjectMember member) {
        var activeProjectStatus = projectMemberStatusRepository.findBySystemCode("ACTIVE")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectStatus systemCode=ACTIVE"));
        member.setProjectMemberStatus(activeProjectStatus);
        member.setJoinAt(LocalDateTime.now());
        member.joined();

        var receivedIds = member.getProject().getProjectManagers()
                .stream()
                .map(ProjectMember::getUser)
                .map(User::getId)
                .toList();

        notificationService.send(receivedIds,
                new MemberJoinContext(member.getProject(), member.getUser()),
                member.getProject().getId());


        projectMemberRepository.save(member);
    }

    private void handleDecline(ProjectMember member) {
        projectMemberRepository.delete(member);
    }

    private void notifyManagersAboutMemberLeft(ProjectMember member) {
        List<Long> managerIds = member.getProject().getProjectManagers()
                .stream()
                .map(pm -> pm.getUser().getId())
                .toList();

        if (!managerIds.isEmpty()) {
            MemberLeftContext context = new MemberLeftContext(member.getProject(), member.getUser());
            notificationService.send(managerIds, context, member.getProject().getId());
        }
    }

    private void notifyMemberBeingRemoved(ProjectMember member) {
        User actor = member.getProject().getProjectManagers().stream()
                .map(ProjectMember::getUser)
                .findFirst()
                .orElse(null);

        MemberRemovedContext context = new MemberRemovedContext(
                member.getProject(),
                member.getUser(),
                actor
        );

        notificationService.send(member.getUser().getId(), context, member.getProject().getId());
    }
}