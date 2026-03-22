package com.group4.projects_management.core.strategy.notification.project;

import com.group4.projects_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProjectDeleteContext {
    private String projectName;
    private User actor;
}
