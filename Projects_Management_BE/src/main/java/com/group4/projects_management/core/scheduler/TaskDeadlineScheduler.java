package com.group4.projects_management.core.scheduler;

import com.group4.projects_management.core.strategy.notification.deadline.TaskDeadlineApproachingContext;
import com.group4.projects_management.core.strategy.notification.deadline.TaskOverdueContext;
import com.group4.projects_management.entity.Task;
import com.group4.projects_management.repository.TaskAssignmentRepository;
import com.group4.projects_management.repository.TaskRepository;
import com.group4.projects_management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskDeadlineScheduler {
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void checkDeadlines() {
        log.info("Bắt đầu quét deadline công việc: {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startSearch = now;
        LocalDateTime endSearch = now.plusDays(1).withHour(23).withMinute(59).withSecond(59);


        List<Task> approachingTasks = taskRepository.findTasksExpiringBetween(startSearch, endSearch);
        log.info("Kết quả: Tìm thấy {} task sắp hạn.", approachingTasks.size());

        for (Task task : approachingTasks) {
            log.info("=> ĐANG XỬ LÝ TASK: {} (Deadline: {})", task.getName(), task.getDeadline());
            sendNotificationToAssignees(task, new TaskDeadlineApproachingContext(task, 1));
        }

        List<Task> overdueTasks = taskRepository.findOverdueTasks(now);
        log.info("Kết quả: Tìm thấy {} task quá hạn.", overdueTasks.size());

        for (Task task : overdueTasks) {
            log.info("=> ĐANG XỬ LÝ TASK QUÁ HẠN: {}", task.getName());
            sendNotificationToAssignees(task, new TaskOverdueContext(task));
        }
    }

    private void sendNotificationToAssignees(Task task, Object context) {
        List<Long> receiverIds = taskAssignmentRepository.findByTask_Id(task.getId())
                .stream()
                .map(a -> a.getAssignee().getUser().getId())
                .toList();

        if (!receiverIds.isEmpty()) {
            notificationService.send(receiverIds, context, task.getId());
        }
    }
}
