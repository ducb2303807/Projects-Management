package com.group4.common.dto; /***********************************************************************
 * Module:  TaskCeateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskCeateRequestDTO
 ***********************************************************************/

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @pdOid b1ba0251-dd62-459e-a0a3-1f2650a2b5f9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaskCreateRequestDTO extends TaskBaseDTO {
    @NotNull
    private Long projectId;
    @NotNull
    private Long priorityId;
    @NotNull
    private Long taskStatusId;
}