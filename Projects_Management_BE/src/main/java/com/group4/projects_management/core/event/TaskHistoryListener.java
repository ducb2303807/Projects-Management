package com.group4.projects_management.core.event;

import com.group4.projects_management.entity.TaskHistory;
import com.group4.projects_management.repository.TaskHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskHistoryListener {
    private final TaskHistoryRepository taskHistoryRepository;

    /**
     * @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
     * Đảm bảo lịch sử chỉ được tạo ra và lưu cùng với Transaction của Task.
     * Nếu hàm update Task bị lỗi (Rollback), lịch sử này cũng sẽ không bị lưu rác vào DB.
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTaskHistoryEvent(TaskHistoryEvent event) {
        if (event.getChanges() == null || event.getChanges().isEmpty()) return;
        log.info("Bắt đầu ghi lịch sử cho Task ID: {}", event.getTask().getId());

        List<TaskHistory> historyList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (FieldChange change : event.getChanges()) {
            TaskHistory history = getTaskHistory(event, change, now);
            historyList.add(history);
        }

        taskHistoryRepository.saveAll(historyList);
    }

    private static @NonNull TaskHistory getTaskHistory(TaskHistoryEvent event, FieldChange change, LocalDateTime now) {
        TaskHistory history = new TaskHistory();
        history.setTask(event.getTask());
        history.setChangedBy(event.getChangedBy());
        history.setColumnName(change.getColumnName());

        history.setOldValue(change.getOldValue() != null ? change.getOldValue() : "");
        history.setNewValue(change.getNewValue() != null ? change.getNewValue() : "");
        history.setChangedAt(now);
        return history;
    }
}
