package com.group4.projects_management.service;

import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.common.enums.BusinessErrorCode;
import com.group4.projects_management.core.exception.BusinessException;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.core.strategy.notification.taskassignment.TaskAssignContext;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.mapper.TaskAssignmentMapper;
import com.group4.projects_management.mapper.TaskHistoryMapper;
import com.group4.projects_management.mapper.TaskMapper;
import com.group4.projects_management.repository.*;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl extends BaseServiceImpl<Task, Long> implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PriorityRepository priorityRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final ProjectRepository projectRepository;
    private final TaskAssignmentMapper taskAssignmentMapper;
    private final TaskMapper taskMapper;
    private final NotificationService notificationService;
    private final TaskHistoryMapper taskHistoryMapper;
    private final UserRepository userRepository;


    public TaskServiceImpl(
            TaskRepository taskRepository,
            TaskHistoryRepository taskHistoryRepository,
            TaskAssignmentRepository taskAssignmentRepository,
            ProjectMemberRepository projectMemberRepository,
            PriorityRepository priorityRepository,
            TaskStatusRepository taskStatusRepository,
            ProjectRepository projectRepository, TaskAssignmentMapper taskAssignmentMapper, TaskMapper taskMapper, NotificationService notificationService, TaskHistoryMapper taskHistoryMapper, UserRepository userRepository
    ) {
        super(taskRepository);
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.priorityRepository = priorityRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.projectRepository = projectRepository;
        this.taskAssignmentMapper = taskAssignmentMapper;
        this.taskMapper = taskMapper;
        this.notificationService = notificationService;
        this.taskHistoryMapper = taskHistoryMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void assignMember(Long taskId, Long assigneeId, Long assignerId) {
        System.out.println("assignerId = " + assignerId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        ProjectMember assignee = projectMemberRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        ProjectMember assigner = projectMemberRepository
                .findByUser_IdAndProject_Id(assignerId, task.getProject().getId())
                .orElseThrow(() -> new RuntimeException("Assigner not in project"));

        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setAssignee(assignee);
        assignment.setAssigner(assigner);
        assignment.setAssignAt(LocalDateTime.now());

        TaskAssignContext context = TaskAssignContext.builder()
                .task(task)
                .assigner(assigner.getUser())
                .build();

        Long receiverId = assignee.getUser().getId();
        Long referenceId = task.getId();

        notificationService.send(receiverId, context, referenceId);

        taskAssignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void assignMembers(Long taskId, List<Long> assigneeIdList, Long assignerId) {
        if (assigneeIdList == null || assigneeIdList.isEmpty()) return;

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        ProjectMember assigner = projectMemberRepository
                .findByUser_IdAndProject_Id(assignerId, task.getProject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigner not in project"));


        List<ProjectMember> assignees = projectMemberRepository.findAllById(assigneeIdList);

        List<TaskAssignment> assignments = new ArrayList<>();
        List<Long> receiverIds = new ArrayList<>();

        for (ProjectMember assignee : assignees) {
            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(task);
            assignment.setAssignee(assignee);
            assignment.setAssigner(assigner);

            assignments.add(assignment);

            receiverIds.add(assignee.getUser().getId());
        }

        taskAssignmentRepository.saveAll(assignments);

        TaskAssignContext context = TaskAssignContext.builder()
                .task(task)
                .assigner(assigner.getUser())
                .build();

        notificationService.send(receiverIds, context, task.getId());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByProject(Long projectId) {

        List<Task> tasks = taskRepository.findByProject_Id(projectId);

        return tasks.stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskHistoryDTO> getTaskHistory(Long taskId) {
        return taskHistoryRepository.findByTaskId(taskId)
                .stream()
                .map(taskHistoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateTaskPriority(Long taskId, Long taskPriorityId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Priority priority = priorityRepository.findById(taskPriorityId)
                .orElseThrow(() -> new RuntimeException("Priority not found"));

        task.setPriority(priority);

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void updateTaskStatus(Long taskId, Long taskStatusId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskStatus status = taskStatusRepository.findById(taskStatusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        task.setTaskStatus(status);

        taskRepository.save(task);
    }


    @Override
    @Transactional
    public void removeMembersFromTask(Long taskId, List<Long> projectMemberIds, Long requesterId) {
        if (projectMemberIds == null || projectMemberIds.isEmpty()) return;
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        // Kiểm tra xem requesterId có phải là PM của project này không, nếu không ném Exception 403.

        taskAssignmentRepository.deleteByTaskIdAndProjectMemberIdIn(taskId, projectMemberIds);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Priority priority = priorityRepository.findById(dto.getPriorityId())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found"));

        TaskStatus status = taskStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        taskMapper.updateEntityFromDto(dto, task, priority, status);

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskCeateRequestDTO dto) {

        if (dto.getDeadline().toLocalDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Deadline cannot be in the past", BusinessErrorCode.SYSTEM_VALIDATION_ERROR);
        }

        Priority priority = priorityRepository.findById(dto.getPriorityId())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found"));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        TaskStatus status = taskStatusRepository.findById(dto.getTaskStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));

        Task task = taskMapper.toEntity(dto, project, priority, status);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public List<TaskResponseDTO> getTasksByStatus(Long projectId, Long statusId) {

        List<Task> tasks = taskRepository.findByProject_IdAndTaskStatus_Id(projectId, statusId);

        return tasks.stream()
                .map(taskMapper::toDto)
                .toList();
    }

}