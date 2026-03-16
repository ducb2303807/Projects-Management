package com.group4.projects_management.core.strategy.notification;

import com.group4.projects_management.entity.Project;
import com.group4.projects_management.entity.ProjectRole;
import com.group4.projects_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectInviteContext {
    private Project project;
    private User inviter;
    private ProjectRole role;
}
