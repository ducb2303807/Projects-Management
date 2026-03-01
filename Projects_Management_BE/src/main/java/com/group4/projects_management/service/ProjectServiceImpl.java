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

    public ProjectServiceImpl(ProjectRepository repository, UserRepository userRepository, ProjectRoleRepository projectRoleRepository, ProjectStatusRepository projectStatusRepository, ProjectMemberRepository projectMemberRepository, ProjectMemberStatusRepository projectMemberStatusRepository, ProjectMapper projectMapper, ProjectMemberMapper projectMemberMapper) {
        super(repository);
        this.projectRepository = repository;
        this.userRepository = userRepository;
        this.projectRoleRepository = projectRoleRepository;
        this.projectStatusRepository = projectStatusRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberStatusRepository = projectMemberStatusRepository;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
    }

    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public void inviteMember(Long projectId, Long inviteeId, Long inviterId, Long roleId) {

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
    public void removeMemberFromProject(Long projectMemberId) {

    }

    @Override
    public void updateMemberRole(Long projectMemberId, Long roleId) {

    }

    @Override
    public ProjectStatsDTO getProjectStatistics(Long projectId) {
        return null;
    }

    // Lấy tất cả project mà user tham gia
    @Override
    public List<ProjectResponseDTO> getProjectsByUserId(Long userId) {

        final String activeMemberStatusCode = "ACCEPTED";

        return projectMemberRepository.findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(userId, activeMemberStatusCode)
                .stream()
                .map(ProjectMember::getProject)
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public ProjectResponseDTO updateProject(Long projectId, ProjectUpdateRequestDTO dto) {
        return null;
    }

    @Override
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

        var ownerRole = projectRoleRepository.findBySystemCode("OWNER")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectRole systemCode=OWNER"));

        // If you have a different default project status code in DB, change it here.
        var defaultProjectStatus = projectStatusRepository.findBySystemCode("ACTIVE")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectStatus systemCode=ACTIVE"));

        var activeMemberStatus = projectMemberStatusRepository.findBySystemCode("ACCEPTED")
                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình ProjectMemberStatus systemCode=ACCEPTED"));

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
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án"));

        var response = projectMapper.toDto(project);
        return response;
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