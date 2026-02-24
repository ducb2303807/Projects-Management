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
import com.group4.projects_management.repository.ProjectMemberRepository;
import com.group4.projects_management.repository.ProjectMemberStatusRepository;
import com.group4.projects_management.repository.ProjectRepository;
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
    /**
     * @pdRoleInfo migr=no name=ProjectRepository assc=association25 mult=1..1
     */
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberStatusRepository projectMemberStatusRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository repository, ProjectMemberRepository projectMemberRepository, ProjectMemberStatusRepository projectMemberStatusRepository, ProjectMapper projectMapper) {
        super(repository);
        this.projectRepository = repository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectMemberStatusRepository = projectMemberStatusRepository;
        this.projectMapper = projectMapper;
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
        return List.of();
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

    @Override
    public List<ProjectResponseDTO> getProjectsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public ProjectResponseDTO updateProject(Long projectId, ProjectUpdateRequestDTO dto) {
        return null;
    }

    @Override
    public List<ProjectMemberDTO> getMembersOfProject(Long projectId) {
        return List.of();
    }

    @Override
    public void leaveProject(Long projectMemberId) {

    }

    @Override
    public ProjectResponseDTO createProject(ProjectCreateRequestDTO dto) {
        return null;
    }

    @Override
    public ProjectResponseDTO getProjectDetail(Long projectId) {
        return null;
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