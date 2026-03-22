package com.group4.projects_management.core.event;

import com.group4.projects_management.entity.ProjectMember;
import com.group4.projects_management.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TaskHistoryEvent {
    private Task task;
    private ProjectMember changedBy;
    private List<FieldChange> changes;
}