package com.group4.projects_management.core.strategy.notification.deadline;

import com.group4.projects_management.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskOverdueContext {
    private Task task;
}