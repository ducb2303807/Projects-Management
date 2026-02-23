package com.group4.projects_management.service; /***********************************************************************
 * Module:  TaskService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface TaskService
 ***********************************************************************/

import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;

import java.util.List;

/** @pdOid 53c36a48-a1fd-4eca-831f-2c7b4fc304a0 */
public interface TaskService {
   /** @param taskId 
    * @param assigneeId 
    * @param assignerId
    * @pdOid 176a4cb2-afb9-4eda-b2dd-bcafc0447754 */
   void assignMember(Long taskId, Long assigneeId, Long assignerId);
   /** @param taskId 
    * @param assigneeIdList 
    * @param assignerId
    * @pdOid 63705ccc-6b8d-432b-9873-0e7eca6df851 */
   void assignMembers(Long taskId, List<Long> assigneeIdList, Long assignerId);
   /** @param projectId
    * @pdOid d174b4c9-a28a-426a-aa1c-ff79f514f6ab */
   List<TaskResponseDTO> getTasksByProject(Long projectId);
   /** @param taskId
    * @pdOid 4eec4cea-792f-4ab2-bd99-05642a7da919 */
   List<TaskHistoryDTO> getTaskHistory(Long taskId);
   /** @param taskId 
    * @param taskPriorityId
    * @pdOid b51efeab-2912-4056-a4d3-cdcfa5e5768a */
   void updateTaskPriority(Long taskId, Long taskPriorityId);
   /** @param taskId 
    * @param taskStatusId
    * @pdOid 2875ea94-938f-41c2-8fb9-b822d7e83ed5 */
   void updateTaskStatus(Long taskId, Long taskStatusId);

   void removeMemberFromTask(Long taskAssignmentId);

   void removeMembersFromTask(Long taskId, List<Long> membersId);
   /** @param taskId 
    * @param dto
    * @pdOid 19ffe925-d5b2-40a5-ad6d-d6ea1d48f65e */
   TaskResponseDTO updateTask(Long taskId, TaskUpdateDTO dto);

   TaskResponseDTO createTask(TaskCeateRequestDTO dto);
   /** @param projectId 
    * @param statusId
    * @pdOid 17f62ca3-613b-4673-b6f1-aa6745bf4827 */
   List<TaskResponseDTO> getTasksByStatus(Long projectId, Long statusId);

}