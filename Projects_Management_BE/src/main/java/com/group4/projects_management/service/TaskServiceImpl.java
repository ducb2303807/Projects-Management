package com.group4.projects_management.service; /***********************************************************************
 * Module:  TaskServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskServiceImpl
 ***********************************************************************/

import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.projects_management.entity.Task;
import com.group4.projects_management.repository.TaskAssignmentRepository;
import com.group4.projects_management.repository.TaskHistoryRepository;
import com.group4.projects_management.repository.TaskRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/** @pdOid 1a6aa067-ba28-4950-88e1-78bd1bfc652e */
@Service
public class TaskServiceImpl extends BaseServiceImpl<Task,Long> implements TaskService {
   /** @pdRoleInfo migr=no name=TaskRepository assc=association23 mult=1..1 */
   private final TaskRepository taskRepository;
   /** @pdRoleInfo migr=no name=TaskHistoryRepository assc=association46 mult=1..1 */
   private final TaskHistoryRepository taskHistoryRepository;
   /** @pdRoleInfo migr=no name=TaskAssignmentRepository assc=association47 mult=1..1 */
   private final TaskAssignmentRepository taskAssignmentRepository;

   public TaskServiceImpl(TaskRepository taskRepository, TaskHistoryRepository taskHistoryRepository, TaskAssignmentRepository taskAssignmentRepository) {
      super(taskRepository);
      this.taskRepository = taskRepository;
      this.taskHistoryRepository = taskHistoryRepository;
      this.taskAssignmentRepository = taskAssignmentRepository;
   }

   @Override
   public void assignMember(Long taskId, Long assigneeId, Long assignerId) {

   }

   @Override
   public void assignMembers(Long taskId, List<Long> assigneeIdList, Long assignerId) {

   }

   @Override
   public List<TaskResponseDTO> getTasksByProject(Long projectId) {
      return List.of();
   }

   @Override
   public List<TaskHistoryDTO> getTaskHistory(Long taskId) {
      return List.of();
   }

   @Override
   public void updateTaskPriority(Long taskId, Long taskPriorityId) {

   }

   @Override
   public void updateTaskStatus(Long taskId, Long taskStatusId) {

   }

   @Override
   public void removeMemberFromTask(Long taskAssignmentId) {

   }


   @Override
   public void removeMembersFromTask(Long taskId, List<Long> membersId) {

   }

   @Override
   public TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto) {
      return null;
   }

   @Override
   public TaskResponseDTO createTask(TaskCeateRequestDTO dto) {
      return null;
   }

   @Override
   public List<TaskResponseDTO> getTasksByStatus(Long projectId, Long statusId) {
      return List.of();
   }
}