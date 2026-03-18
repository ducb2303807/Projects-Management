package com.group4.common.dto; /***********************************************************************
 * Module:  TaskHistoryDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskHistoryDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @pdOid f1a3e9dd-d3b5-49c2-a69a-5627adedf548
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskHistoryDTO {
    private LocalDateTime changedAt;
    private java.lang.String changedBy;
    private java.lang.String columnName;
    private java.lang.String oldValue;
    private java.lang.String newValue;
}