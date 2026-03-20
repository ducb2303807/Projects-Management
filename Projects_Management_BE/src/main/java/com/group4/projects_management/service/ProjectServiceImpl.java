package com.group4.projects_management.service; /***********************************************************************
 * Module:  ProjectServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectServiceImpl
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.common.enums.MemberStatus;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.core.strategy.notification.invitation.ProjectInviteContext;
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

        Set<Long> existingUserIdsInDb = projectMemberRepository.findAllByProject_IdAndUser_IdIn(projectId, userIds.stream().toList())
                .stream()
                .map(m -> m.getUser().getId())
                .collect(Collectors.toSet());

        // 4. Lọc điều kiện và Build Entity bằng Stream (Không dùng vòng lặp for & if-continue)
        LocalDateTime now = LocalDateTime.now();
        List<ProjectMember> newMembers = validUserRoleMap.entrySet().stream()
                .filter(entry -> !existingUserIdsInDb.contains(entry.getKey())) // Phải chưa có trong DB
                .filter(entry -> userMap.containsKey(entry.getKey()))            // User phải tồn tại
                .filter(entry -> roleMap.containsKey(entry.getValue()))          // Role phải tồn tại
                .map(entry -> {
                    ProjectMember member = new ProjectMember();
                    member.setProject(project);
                    member.setUser(userMap.get(entry.getKey()));
                    member.setProjectRole(roleMap.get(entry.getValue()));
                    member.setProjectMemberStatus(pendingStatus);
                    member.setInvitedBy(inviter);
                    member.setInvitedAt(now);
                    return member;
                })
                .toList();

        if (newMembers.isEmpty()) {
            log.warn("Không có thành viên nào đủ điều kiện để mời trong dự án {}", projectId);
            return;
        }

        // 5. Lưu và Gửi thông báo
        List<ProjectMember> savedMembers = projectMemberRepository.saveAll(newMembers);

        savedMembers.forEach(m -> {
            ProjectInviteContext context = new ProjectInviteContext(project, inviter.getUser(), m.getProjectRole());
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

        if (statusEntity.getSystemCode().equalsIgnoreCase(MemberStatus.ACTIVE.name())) {
            member.setLeftAt(null);
            member.setJoinAt(LocalDateTime.now());
        }
        if (statusEntity.getSystemCode().equalsIgnoreCase(MemberStatus.REMOVED.name())) {
            member.leave();
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
            System.err.println("Lỗi cập nhật metadata thông báo: " + e.getMessage());
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
    public void removeMemberFromProject(Long projectMemberId) {
        var member = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên trong project"));

        var leftStatus = projectMemberStatusRepository.findBySystemCode(MemberStatus.LEFT.name())
                        .orElseThrow(() -> new RuntimeException("System code not found: " + MemberStatus.LEFT.name()));

        member.setProjectMemberStatus(leftStatus);
        member.leave();

        projectMemberRepository.save(member);
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

        int totalTasks = taskRepository.countByProject_Id(projectId);
        int completedTasks = taskRepository.countByProject_IdAndTaskStatus_SystemCode(projectId, "COMPLETED");
        int inProgressTasks = taskRepository.countByProject_IdAndTaskStatus_SystemCode(projectId, "IN_PROGRESS");

        double progressPercentage = totalTasks == 0 ? 0.0 :
                (double) completedTasks / totalTasks * 100.0;

        return new ProjectStatsDTO(
                totalTasks,
                completedTasks,
                inProgressTasks,
                progressPercentage
        );
    }

    // Lấy tất cả project mà user tham gia
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByUserId(Long userId) {

        final String activeMemberStatusCode = "ACTIVE";

        return projectMemberRepository
                .findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(userId, activeMemberStatusCode)
                .stream()
                .map(ProjectMember::getProject)
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponseDTO updateProject(Long projectId, ProjectUpdateRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án"));

        if (dto.getProjectName() != null) {
            project.setName(dto.getProjectName());
        }

        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }

        if (dto.getStartDate() != null) {
            project.setStartDate(dto.getStartDate());
        }

        if (dto.getEndDate() != null) {
            project.setEndDate(dto.getEndDate());
        }

        if (dto.getStatusId() != null) {
            ProjectStatus status = projectStatusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái"));
            project.setProjectStatus(status);
        }

        Project updated = projectRepository.save(project);
        return projectMapper.toDto(updated);
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
                .orElseThrow(() -> new ResourceNotFoundException("member không tồn tại"));

        member.leave();
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

        ProjectMember creatorMember = new ProjectMember();
        creatorMember.setProject(savedProject);
        creatorMember.setUser(creator);
        creatorMember.setProjectRole(ownerRole);
        creatorMember.setProjectMemberStatus(activeMemberStatus);
        creatorMember.setInvitedBy(null);
        creatorMember.setJoinAt(LocalDateTime.now());

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án"));
        return projectMapper.toDto(project);
    }

    private void handleAccept(ProjectMember member) {
        var activeProjectStatus = projectMemberStatusRepository.findBySystemCode("ACTIVE")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectStatus systemCode=ACTIVE"));
        member.setProjectMemberStatus(activeProjectStatus);
        member.setJoinAt(LocalDateTime.now());
        projectMemberRepository.save(member);
    }

    private void handleDecline(ProjectMember member) {
        projectMemberRepository.delete(member);
    }
}