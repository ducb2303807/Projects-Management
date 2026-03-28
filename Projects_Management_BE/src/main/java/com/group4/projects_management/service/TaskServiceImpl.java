package com.group4.projects_management.service;

import com.group4.common.dto.TaskCreateRequestDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.common.enums.BusinessErrorCode;
import com.group4.common.enums.MemberStatusCode;
import com.group4.common.enums.TaskStatusCode;
import com.group4.projects_management.core.event.FieldChange;
import com.group4.projects_management.core.event.TaskHistoryEvent;
import com.group4.projects_management.core.exception.BusinessException;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import com.group4.projects_management.core.strategy.notification.task.TaskAssignContext;
import com.group4.projects_management.core.strategy.notification.task.TaskStatusContext;
import com.group4.projects_management.core.strategy.notification.task.TaskUnassignContext;
import com.group4.projects_management.core.strategy.notification.task.TaskUpdateContext;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.mapper.TaskAssignmentMapper;
import com.group4.projects_management.mapper.TaskHistoryMapper;
import com.group4.projects_management.mapper.TaskMapper;
import com.group4.projects_management.repository.*;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class TaskServiceImpl extends BaseServiceImpl<Task, Long> implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PriorityRepository priorityRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberStatusRepository projectMemberStatusRepository;
    private final TaskAssignmentMapper taskAssignmentMapper;
    private final TaskMapper taskMapper;
    private final NotificationService notificationService;
    private final TaskHistoryMapper taskHistoryMapper;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            TaskHistoryRepository taskHistoryRepository,
            TaskAssignmentRepository taskAssignmentRepository,
            ProjectMemberRepository projectMemberRepository,
            PriorityRepository priorityRepository,
            TaskStatusRepository taskStatusRepository,
            ProjectRepository projectRepository, ProjectMemberStatusRepository projectMemberStatusRepository, TaskAssignmentMapper taskAssignmentMapper, TaskMapper taskMapper, NotificationService notificationService, TaskHistoryMapper taskHistoryMapper, UserRepository userRepository, ApplicationEventPublisher eventPublisher
    ) {
        super(taskRepository);
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.priorityRepository = priorityRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.projectRepository = projectRepository;
        this.projectMemberStatusRepository = projectMemberStatusRepository;
        this.taskAssignmentMapper = taskAssignmentMapper;
        this.taskMapper = taskMapper;
        this.notificationService = notificationService;
        this.taskHistoryMapper = taskHistoryMapper;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void assignMember(Long taskId, Long assigneeId, Long assignerId) {
        System.out.println("assignerId = " + assignerId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        ProjectMember assignee = projectMemberRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));

        ProjectMember assigner = projectMemberRepository
                .findByUser_IdAndProject_Id(assignerId, task.getProject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigner not in project"));

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
    public List<TaskResponseDTO> getTasksByProject(Long projectId, boolean includeCancelled) {

        if (includeCancelled) {
            return taskRepository.findByProject_Id(projectId).stream()
                    .map(taskMapper::toDto).toList();
        }

        // Chỉ lấy task nếu cả Task và Project đều KHÔNG ở trạng thái CANCELLED
        return taskRepository.findActiveTasksByProjectId(
                        projectId, "CANCELLED", "CANCELLED")
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByUserId(Long userId, boolean includeCancelled) {
        final String activeMemberStatus = MemberStatusCode.ACTIVE.name();
        List<Task> tasks;

        if (includeCancelled) {
            tasks = taskAssignmentRepository.findAllTasksForUser(userId, activeMemberStatus);
        } else {
            tasks = taskAssignmentRepository.findActiveTasksForUser(
                    userId,
                    activeMemberStatus,
                    TaskStatusCode.CANCELLED.name(),
                    TaskStatusCode.CANCELLED.name());
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Priority priority = priorityRepository.findById(taskPriorityId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found"));

        task.setPriority(priority);

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void updateTaskStatus(Long taskId, Long taskStatusId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskStatus status = taskStatusRepository.findById(taskStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        task.setTaskStatus(status);

        taskRepository.save(task);

        List<Long> assignedUserIds = task.getMembersId();

        if (!assignedUserIds.isEmpty()) {
            TaskStatusContext context = TaskStatusContext.builder()
                    .task(task)
                    .newStatusName(status.getName())
                    // .actor(currentUser)
                    .build();
            notificationService.send(assignedUserIds, context, task.getId());
        }
    }


    @Override
    @Transactional
    public void removeMembersFromTask(Long taskId, List<Long> projectMemberIds, Long requesterId) {
        if (projectMemberIds == null || projectMemberIds.isEmpty()) return;
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User requester  = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        List<Long> receiverIds = projectMemberRepository.findAllById(projectMemberIds)
                .stream()
                .map(m -> m.getUser().getId())
                .toList();

        // Kiểm tra xem requesterId có phải là PM của project này không, nếu không ném Exception 403.

        taskAssignmentRepository.deleteByTaskIdAndProjectMemberIdIn(taskId, projectMemberIds);

        TaskUnassignContext context = TaskUnassignContext.builder()
                .task(task)
                .actor(requester)
                .build();
        notificationService.send(receiverIds, context, task.getId());
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto, Long actorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        ProjectMember actor = projectMemberRepository.findByUser_IdAndProject_Id(actorId, task.getProject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Actor is not a member of this project"));

        Priority priority = priorityRepository.findById(dto.getPriorityId())
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found"));

        TaskStatus status = taskStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        List<FieldChange> detectedChanges = detectTaskChanges(task, dto, priority, status);

        taskMapper.updateEntityFromDto(dto, task, priority, status);
        Task savedTask = taskRepository.save(task);

        if (!detectedChanges.isEmpty()) {
            TaskHistoryEvent historyEvent = TaskHistoryEvent.builder()
                    .task(savedTask)
                    .changedBy(actor)
                    .changes(detectedChanges)
                    .build();
            eventPublisher.publishEvent(historyEvent);
        }

        List<Long> managerIds = task.getProject().getProjectManagers()
                .stream().map(pm -> pm.getUser().getId()).toList();
        List<Long> receiverIds = task.getMembersId();

        List<Long> finalReceiverIds = Stream.concat(receiverIds.stream(), managerIds.stream())
                .distinct()
                .filter(id -> !id.equals(actorId))
                .toList();

        if(!finalReceiverIds.isEmpty()) {
            TaskUpdateContext context = TaskUpdateContext.builder()
                    .task(savedTask)
                    .actor(actor.getUser())
                    .build();
            notificationService.send(receiverIds, context, savedTask.getId());
        }

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskCreateRequestDTO dto) {

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

    @Override
    @Transactional
    public void deleteTask(Long taskId, Long requesterId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskStatus cancelledStatus = taskStatusRepository.findBySystemCode(TaskStatusCode.CANCELLED.name())
                .orElseThrow(() -> new ResourceNotFoundException("Task status CANCELLED not found in system"));

        task.setTaskStatus(cancelledStatus);
        taskRepository.save(task);

        List<Long> receiverIds = task.getMembersId();

        if (!receiverIds.isEmpty()) {
            User requester = userRepository.findById(requesterId).orElse(null);

            TaskStatusContext context = TaskStatusContext.builder()
                    .task(task)
                    .newStatusName(cancelledStatus.getName())
                    .actor(requester)
                    .build();

            notificationService.send(receiverIds, context, task.getId());
        }
    }

    private List<FieldChange> detectTaskChanges(Task oldTask, TaskUpdateDTO dto, Priority newPriority, TaskStatus newStatus) {
        List<FieldChange> changes = new ArrayList<>();

        if (!Objects.equals(oldTask.getName(), dto.getName())) {
            changes.add(new FieldChange("Name", oldTask.getName(), dto.getName()));
        }

        if (!Objects.equals(oldTask.getDescription(), dto.getDescription())) {
            changes.add(new FieldChange("Description", oldTask.getDescription(), dto.getDescription()));
        }

        if (!Objects.equals(oldTask.getDeadline(), dto.getDeadline())) {
            String oldDl = oldTask.getDeadline() != null ? oldTask.getDeadline().toString() : "";
            String newDl = dto.getDeadline() != null ? dto.getDeadline().toString() : "";
            changes.add(new FieldChange("Deadline", oldDl, newDl));
        }

        if (!oldTask.getPriority().getId().equals(newPriority.getId())) {
            changes.add(new FieldChange("Priority", oldTask.getPriority().getName(), newPriority.getName()));
        }

        if (!oldTask.getTaskStatus().getId().equals(newStatus.getId())) {
            changes.add(new FieldChange("Status", oldTask.getTaskStatus().getName(), newStatus.getName()));
        }

        return changes;
    }

}