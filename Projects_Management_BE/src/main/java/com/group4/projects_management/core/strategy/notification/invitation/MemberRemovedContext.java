package com.group4.projects_management.core.strategy.notification.invitation;

import com.group4.projects_management.entity.Project;
import com.group4.projects_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberRemovedContext {
    private Project project;
    private User removedMember;
    private User actor;
}
