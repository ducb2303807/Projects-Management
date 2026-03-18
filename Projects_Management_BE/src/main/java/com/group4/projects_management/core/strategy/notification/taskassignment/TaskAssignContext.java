package com.group4.projects_management.core.strategy.notification.taskassignment;

import com.group4.projects_management.entity.Task;
import com.group4.projects_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TaskAssignContext {
    private Task task;
    private User assigner;
}
