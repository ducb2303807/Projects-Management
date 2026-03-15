package com.group4.projects_management.service; /***********************************************************************
 * Module:  ProjectServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectServiceImpl
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.entity.Project;
import com.group4.projects_management.entity.ProjectMember;
import com.group4.projects_management.entity.ProjectMemberStatus;
import com.group4.projects_management.entity.ProjectStatus;
import com.group4.projects_management.mapper.ProjectMapper;
import com.group4.projects_management.mapper.ProjectMemberMapper;
import com.group4.projects_management.repository.*;
import com.group4.projects_management.service.base.BaseServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @pdOid 8ed7a716-6d3c-4f74-ae7a-3d4f7734e7a8
 */
@Service
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

    public ProjectServiceImpl(ProjectRepository repository, UserRepository userRepository, ProjectRoleRepository projectRoleRepository, ProjectStatusRepository projectStatusRepository, ProjectMemberRepository projectMemberRepository, ProjectMemberStatusRepository projectMemberStatusRepository, ProjectMapper projectMapper, ProjectMemberMapper projectMemberMapper, TaskRepository taskRepository) {
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
    public void inviteMember(Long projectId, Long inviteeId, Long inviterMemberId, Long roleId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));

        var invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user được mời"));

        var inviterMember = projectMemberRepository.findById(inviterMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên mời"));

        var role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role"));

        var pendingStatus = projectMemberStatusRepository.findBySystemCode("PENDING")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectMemberStatus systemCode=PENDING"));

        boolean exists = projectMemberRepository.existsByProject_IdAndUser_Id(projectId, inviteeId);
        if (exists) {
            throw new RuntimeException("User đã thuộc project này rồi");
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(invitee);
        member.setProjectRole(role);
        member.setProjectMemberStatus(pendingStatus);
        member.setInvitedBy(inviterMember); // ✅ gán bằng ProjectMember
        member.setInvitedAt(LocalDateTime.now());

        projectMemberRepository.save(member);
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


        switch (request.getStatus()) {
            case ACCEPTED -> handleAccept(member, statusEntity);
            case DECLINED -> handleDecline(member, statusEntity);
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ");
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
    public List<ProjectResponseDTO> getProjectsByUserId(Long userId) {

        final String activeMemberStatusCode = "ACCEPTED";

        return projectMemberRepository
                .findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(userId, activeMemberStatusCode)
                .stream()
                .map(ProjectMember::getProject)
                .map(projectMapper::toDto
                )
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
    public ProjectResponseDTO createProject(ProjectCreateRequestDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("Request cannot be null");

        if (dto.getCreateByUserId() == null) {
            throw new IllegalArgumentException("createByUserId is required");
        }

        var creator = userRepository.findById(dto.getCreateByUserId())
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

        projectMemberRepository.save(creatorMember);

        return projectMapper.toDto(savedProject);
    }

    @Override
    public ProjectResponseDTO getProjectDetail(Long projectId) {
        var project = projectRepository.findByIdWithMembers(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án"));
        return projectMapper.toDto(project);
    }

    private void handleAccept(ProjectMember member, ProjectMemberStatus statusEntity) {
        member.setProjectMemberStatus(statusEntity);
        member.setJoinAt(LocalDateTime.now());
        projectMemberRepository.save(member);
    }

    private void handleDecline(ProjectMember member, ProjectMemberStatus statusEntity) {
        // Có thể xóa record hoặc chuyển trạng thái tùy nghiệp vụ
        member.setProjectMemberStatus(statusEntity);
        projectMemberRepository.save(member);
    }
}