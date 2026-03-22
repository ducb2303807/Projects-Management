package com.group4.projects_management.core.strategy.notification.task;

import com.group4.projects_management.entity.Task;
import com.group4.projects_management.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskUnassignContext {
    private Task task;
    private User actor; // Người thực hiện gỡ
}
