package com.group4.common.dto; /***********************************************************************
 * Module:  TaskResponseDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskResponseDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @pdOid a88a633b-faff-47e9-b176-39d5649e641a
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaskResponseDTO extends TaskBaseDTO {

    private Long taskId;

    private java.lang.String statusName;

    private java.lang.String priorityName;

    private LocalDateTime createdAt;

    private List<TaskAssigneeDTO> assignees;

    private List<CommentDTO> comments;

}