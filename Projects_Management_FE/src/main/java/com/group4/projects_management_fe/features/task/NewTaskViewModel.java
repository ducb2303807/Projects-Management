package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.common.enums.LookupType;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import javafx.application.Platform;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewTaskViewModel {

    // --- Dependencies ---
    // Dùng LookupApi (có sẵn, đúng endpoint /lookups/{type}) thay vì TaskApi.getLookups()
    private final LookupApi lookupApi;
    private final TaskApi   taskApi;

    // --- STATE: dữ liệu dropdown tải từ DB ---
    // Không dùng .hide() → giữ nguyên tính replay của BehaviorSubject
    private final BehaviorSubject<List<LookupDTO>> taskStatuses =
            BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<LookupDTO>> priorities =
            BehaviorSubject.createDefault(new ArrayList<>());

    // --- STATE: dữ liệu form ---
    private final BehaviorSubject<String>       taskName    = BehaviorSubject.createDefault("");
    private final BehaviorSubject<LocalDate>    dueDate     = BehaviorSubject.createDefault(LocalDate.MIN);
    private final BehaviorSubject<LookupDTO>    status      = BehaviorSubject.createDefault(new LookupDTO());
    private final BehaviorSubject<LookupDTO>    priority    = BehaviorSubject.createDefault(new LookupDTO());
    private final BehaviorSubject<String>       assignee    = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String>       description = BehaviorSubject.createDefault("");
    private final BehaviorSubject<List<String>> comments    = BehaviorSubject.createDefault(new ArrayList<>());
    @Setter
    private Long projectId;
    // --- SUBMIT ---
    @Setter
    private Runnable onSuccess;

    public NewTaskViewModel(AuthSessionProvider sessionProvider) {
        this.lookupApi = new LookupApi(sessionProvider);
        this.taskApi   = new TaskApi(sessionProvider);
        loadLookups();
    }

    // Gọi LookupApi.getAll() với LookupType enum — đúng endpoint, đúng kiểu
    private void loadLookups() {
        lookupApi.getAll(LookupType.TASK_STATUS)
                .thenAccept(taskStatuses::onNext)
                .exceptionally(ex -> {
                    System.err.println("[ViewModel] Lỗi tải TASK_STATUS: " + ex.getMessage());
                    return null;
                });

        lookupApi.getAll(LookupType.PRIORITY)
                .thenAccept(priorities::onNext)
                .exceptionally(ex -> {
                    System.err.println("[ViewModel] Lỗi tải PRIORITY: " + ex.getMessage());
                    return null;
                });
    }

    // --- INPUT ---
    public void setTaskName(String name)       { taskName.onNext(name != null ? name : ""); }
    public void setDueDate(LocalDate date)     { dueDate.onNext(date != null ? date : LocalDate.MIN); }
    public void setStatus(LookupDTO dto)       { status.onNext(dto != null ? dto : new LookupDTO()); }
    public void setPriority(LookupDTO dto)     { priority.onNext(dto != null ? dto : new LookupDTO()); }
    public void setDescription(String desc)    { description.onNext(desc != null ? desc : ""); }
    public void setAssignee(String username)   { assignee.onNext(username != null ? username.trim() : ""); }

    public void addComment(String text) {
        if (text == null || text.trim().isEmpty()) return;
        List<String> current = new ArrayList<>(comments.getValue());
        current.add(text.trim());
        comments.onNext(current);
    }

    // --- OUTPUT ---
    public Observable<List<LookupDTO>> taskStatusesObservable() { return taskStatuses; }
    public Observable<List<LookupDTO>> prioritiesObservable()   { return priorities; }
    public Observable<String>          assigneeObservable()     { return assignee; }
    public Observable<List<String>>    commentsObservable()     { return comments; }

    public Observable<Boolean> isFormValidObservable() {
        return Observable.combineLatest(taskName, dueDate,
                (name, date) -> !name.trim().isEmpty() && !date.equals(LocalDate.MIN));
    }

    public void submitTask() {
        com.group4.common.dto.TaskCreateRequestDTO dto = new com.group4.common.dto.TaskCreateRequestDTO();

        dto.setName(taskName.getValue());
        dto.setProjectId(this.projectId);

        if (priority.getValue() != null && priority.getValue().getId() != null) {
            dto.setPriorityId(Long.parseLong(priority.getValue().getId().toString()));
        }

        if (status.getValue() != null && status.getValue().getId() != null) {
            dto.setTaskStatusId(Long.parseLong(status.getValue().getId().toString()));
        }

        if (dueDate.getValue() != null) {
            dto.setDeadline(dueDate.getValue().atStartOfDay());
        }

        dto.setDescription(description.getValue());

        taskApi.createTaskInProject(dto)
                .thenAccept(res -> {
                    if (onSuccess != null) {
                        Platform.runLater(onSuccess);
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}