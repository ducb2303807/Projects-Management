package com.group4.projects_management.controller; /***********************************************************************
 * Module:  TaskController.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskController
 ***********************************************************************/

import com.group4.common.dto.TaskCeateRequestDTO;
import com.group4.common.dto.TaskHistoryDTO;
import com.group4.common.dto.TaskResponseDTO;
import com.group4.common.dto.TaskUpdateDTO;
import com.group4.projects_management.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** @pdOid ce9b19d3-3ddd-449d-8d79-b270923a5f2d */
@RestController
public class TaskController {
   /** @pdRoleInfo migr=no name=TaskService assc=association24 mult=1..1 */
   @Autowired
   private TaskService taskService;
   
   /** @param request
    * @pdOid 49222821-fc6f-45ad-b1f7-520fdb70e879 */
   public ResponseEntity<TaskResponseDTO> createTask(TaskCeateRequestDTO request) {
      // TODO: implement
      return null;
   }
   
   /** @param projectId
    * @pdOid 68d9d356-6d7c-4517-b300-1391946016a2 */
   public ResponseEntity<List<TaskResponseDTO>> getTasksByProjectId(Long projectId) {
      // TODO: implement
      return null;
   }
   
   /** @param taskId 
    * @param statusId
    * @pdOid 444345b9-ad0d-4729-afc2-3700f866da0e */
   public ResponseEntity<Void> setTaskStatus(Long taskId, Long statusId) {
      // TODO: implement
      return null;
   }
   
   /** @param taskId 
    * @param priorityId
    * @pdOid 38beab9d-086f-4e0e-b7a1-6a2fb3226ed3 */
   public ResponseEntity<Void> setTaskPriority(Long taskId, Long priorityId) {
      // TODO: implement
      return null;
   }
   
   /** @param taskId 
    * @param projectMemberId
    * @pdOid 2786cc7f-f801-4009-a40b-c502dc0ea9c4 */
   public ResponseEntity<Void> assignMember(Long taskId, Long projectMemberId) {
      // TODO: implement
      return null;
   }
   
   /** @param taskAssignmentId
    * @pdOid 50229858-209e-4851-9c08-fe740e2fb134 */
   public ResponseEntity<Void> removeMemberFromTask(Long taskAssignmentId) {
      // TODO: implement
      return null;
   }
   
   /** @param taskId
    * @pdOid 8db1f273-adc6-44f7-afdf-bdfbbad0d2d4 */
   public ResponseEntity<List<TaskHistoryDTO>> getTaskHistory(Long taskId) {
      // TODO: implement
      return null;
   }
   
   /** @param taskId 
    * @param request
    * @pdOid da8ee595-ed0b-41a8-a418-abcfc8aa10f6 */
   public ResponseEntity<TaskResponseDTO> updateTask(Long taskId, TaskUpdateDTO request) {
      // TODO: implement
      return null;
   }

}