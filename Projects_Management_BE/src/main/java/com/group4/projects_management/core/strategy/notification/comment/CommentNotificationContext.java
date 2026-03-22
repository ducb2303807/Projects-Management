package com.group4.projects_management.core.strategy.notification.comment;

import com.group4.projects_management.entity.Task;
import com.group4.projects_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentNotificationContext {
    private Task task;
    private User author;
    private User parentAuthor;
}
