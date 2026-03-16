package com.group4.projects_management.service;

import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.projects_management.entity.*;
import com.group4.projects_management.repository.*;
import com.group4.projects_management.service.base.BaseServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

   public TaskServiceImpl(
           TaskRepository taskRepository,
           TaskHistoryRepository taskHistoryRepository,
           TaskAssignmentRepository taskAssignmentRepository,
           ProjectMemberRepository projectMemberRepository,
           PriorityRepository priorityRepository,
           TaskStatusRepository taskStatusRepository,
           ProjectRepository projectRepository
   ) {
      super(taskRepository);
      this.taskRepository = taskRepository;
      this.taskHistoryRepository = taskHistoryRepository;
      this.taskAssignmentRepository = taskAssignmentRepository;
      this.projectMemberRepository = projectMemberRepository;
      this.priorityRepository = priorityRepository;
      this.taskStatusRepository = taskStatusRepository;
      this.projectRepository = projectRepository;
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

      taskAssignmentRepository.save(assignment);
   }

   @Override
   @Transactional
   public void assignMembers(Long taskId, List<Long> assigneeIdList, Long assignerId) {

      for (Long assigneeId : assigneeIdList) {
         assignMember(taskId, assigneeId, assignerId);
      }

   }

   @Override
   public List<TaskResponseDTO> getTasksByProject(Long projectId) {

      List<Task> tasks = taskRepository.findByProject_Id(projectId);
      List<TaskResponseDTO> result = new ArrayList<>();

      for (Task task : tasks) {

         if (task.getProject().getId().equals(projectId)) {

            TaskResponseDTO dto = new TaskResponseDTO();
            dto.setTaskId(task.getId());
            dto.setTaskName(task.getName());
            dto.setDescription(task.getDescription());
            dto.setDeadline(task.getDeadline());
            dto.setCreatedAt(task.getCreatedAt());

            if (task.getPriority() != null) {
               dto.setPriorityName(task.getPriority().getName());
            }

            if (task.getTaskStatus() != null) {
               dto.setStatusName(task.getTaskStatus().getName());
            }

            result.add(dto);
         }
      }

      return result;
   }

   @Override
   public List<TaskHistoryDTO> getTaskHistory(Long taskId) {

      List<TaskHistory> histories = taskHistoryRepository.findByTaskId(taskId);

      List<TaskHistoryDTO> result = new ArrayList<>();

      for (TaskHistory history : histories) {

         TaskHistoryDTO dto = new TaskHistoryDTO();

         dto.setChangedAt(history.getChangedAt());
         dto.setColumnName(history.getColumnName());
         dto.setOldValue(history.getOldValue());
         dto.setNewValue(history.getNewValue());

         if (history.getChangedBy() != null) {
            dto.setChangeBy(history.getChangedBy().getUser().getUsername());
         }

         result.add(dto);
      }

      return result;
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
   public void removeMemberFromTask(Long taskAssignmentId) {
      taskAssignmentRepository.deleteById(taskAssignmentId);
   }

   @Override
   @Transactional
   public void removeMembersFromTask(Long taskId, List<Long> membersId) {

      Task task = taskRepository.findById(taskId)
              .orElseThrow(() -> new RuntimeException("Task not found"));

      if (task.getAssignments() == null) return;

      for (TaskAssignment assignment : task.getAssignments()) {

         if (membersId.contains(assignment.getId())) {
            taskAssignmentRepository.delete(assignment);
         }

      }

   }

   @Override
   @Transactional
   public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto) {

      Task task = taskRepository.findById(taskId)
              .orElseThrow(() -> new RuntimeException("Task not found"));

      task.setName(dto.getTaskName());
      task.setDescription(dto.getDescription());
      task.setDeadline(dto.getDeadline());

      taskRepository.save(task);

      TaskResponseDTO response = new TaskResponseDTO();

      response.setTaskId(task.getId());
      response.setTaskName(task.getName());
      response.setDescription(task.getDescription());
      response.setDeadline(task.getDeadline());
      response.setCreatedAt(task.getCreatedAt());

      if (task.getPriority() != null) {
         response.setPriorityName(task.getPriority().getName());
      }

      if (task.getTaskStatus() != null) {
         response.setStatusName(task.getTaskStatus().getName());
      }

      return response;
   }

   @Override
   @Transactional
   public TaskResponseDTO createTask(TaskCeateRequestDTO dto) {

      Task task = new Task();

      task.setName(dto.getTaskName());
      task.setDescription(dto.getDescription());
      task.setDeadline(dto.getDeadline());

      Priority priority = priorityRepository.findById(dto.getPriorityId())
              .orElseThrow(() -> new RuntimeException("Priority not found"));

      task.setPriority(priority);

      Project project = projectRepository.findById(dto.getProjectId())
              .orElseThrow(() -> new RuntimeException("Project not found"));

      task.setProject(project);

      TaskStatus status = taskStatusRepository.findById(dto.getTaskStatusId())
              .orElseThrow(() -> new RuntimeException("Task status not found"));

      task.setTaskStatus(status);

      taskRepository.save(task);

      TaskResponseDTO response = new TaskResponseDTO();
      response.setTaskId(task.getId());
      response.setTaskName(task.getName());
      response.setDescription(task.getDescription());
      response.setDeadline(task.getDeadline());
      response.setCreatedAt(task.getCreatedAt());
      response.setPriorityName(priority.getName());

      return response;
   }

   @Override
   public List<TaskResponseDTO> getTasksByStatus(Long projectId, Long statusId) {

      List<Task> tasks =
              taskRepository.findByProject_IdAndTaskStatus_Id(projectId, statusId);

      List<TaskResponseDTO> result = new ArrayList<>();

      for (Task task : tasks) {

         TaskResponseDTO dto = new TaskResponseDTO();
         dto.setTaskId(task.getId());
         dto.setTaskName(task.getName());
         dto.setDescription(task.getDescription());
         dto.setDeadline(task.getDeadline());
         dto.setCreatedAt(task.getCreatedAt());

         if (task.getPriority() != null) {
            dto.setPriorityName(task.getPriority().getName());
         }

         if (task.getTaskStatus() != null) {
            dto.setStatusName(task.getTaskStatus().getName());
         }

         result.add(dto);
      }

      return result;
   }

}