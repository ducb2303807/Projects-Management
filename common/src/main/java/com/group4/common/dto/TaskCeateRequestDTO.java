package com.group4.common.dto; /***********************************************************************
 * Module:  TaskCeateRequestDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskCeateRequestDTO
 ***********************************************************************/

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @pdOid b1ba0251-dd62-459e-a0a3-1f2650a2b5f9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaskCeateRequestDTO extends TaskBaseDTO {
    @NonNull
    private Long projectId;
    @NonNull
    private Long priorityId;
    @NonNull
    private Long taskStatusId;
}