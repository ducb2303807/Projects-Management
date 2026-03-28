package com.group4.projects_management_fe.features.task;

import com.group4.common.dto.LookupDTO;
import com.group4.common.dto.ProjectMemberDTO;
import com.group4.common.dto.TaskCreateRequestDTO;
import com.group4.common.enums.LookupType;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.api.TaskApi;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import javafx.application.Platform;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewTaskViewModel {

    // ── Dependencies ──────────────────────────────────────────────────────────
    private final LookupApi  lookupApi;
    private final TaskApi    taskApi;
    private final ProjectApi projectApi;

    // ── STATE: lookup dropdowns ───────────────────────────────────────────────
    private final BehaviorSubject<List<LookupDTO>> taskStatuses =
            BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<LookupDTO>> priorities =
            BehaviorSubject.createDefault(new ArrayList<>());

    // ── STATE: form fields ────────────────────────────────────────────────────
    private final BehaviorSubject<String>    taskName    = BehaviorSubject.createDefault("");
    private final BehaviorSubject<LocalDate> dueDate     = BehaviorSubject.createDefault(LocalDate.MIN);
    private final BehaviorSubject<LookupDTO> status      = BehaviorSubject.createDefault(new LookupDTO());
    private final BehaviorSubject<LookupDTO> priority    = BehaviorSubject.createDefault(new LookupDTO());
    private final BehaviorSubject<String>    description = BehaviorSubject.createDefault("");

    // ── STATE: assignee ───────────────────────────────────────────────────────
    /**
     * Danh sách member của project (cache sau lần load đầu tiên).
     * Observable để Controller subscribe → hiện search popup.
     */
    private final BehaviorSubject<List<ProjectMemberDTO>> projectMembers =
            BehaviorSubject.createDefault(new ArrayList<>());

    /**
     * True nếu user hiện tại có role chứa "Manager" trong project này.
     * Controller subscribe → hiện/ẩn nút "+".
     */
    private final BehaviorSubject<Boolean> canManageAssignees =
            BehaviorSubject.createDefault(false);

    /**
     * Danh sách member đã được chọn để assign sau khi tạo task.
     * Controller subscribe → render chip "Tên ×".
     */
    private final BehaviorSubject<List<ProjectMemberDTO>> selectedAssignees =
            BehaviorSubject.createDefault(new ArrayList<>());

    // ── Config ────────────────────────────────────────────────────────────────
    @Setter private Long     projectId;
    @Setter private Runnable onSuccess;

    // ── Constructor ───────────────────────────────────────────────────────────

    public NewTaskViewModel(AuthSessionProvider sessionProvider) {
        this.lookupApi  = new LookupApi(sessionProvider);
        this.taskApi    = new TaskApi(sessionProvider);
        this.projectApi = new ProjectApi(sessionProvider);
        loadLookups();
    }

    // ── Load lookups ──────────────────────────────────────────────────────────

    private void loadLookups() {
        lookupApi.getAll(LookupType.TASK_STATUS)
                .thenAccept(taskStatuses::onNext)
                .exceptionally(ex -> {
                    System.err.println("[NewTaskViewModel] TASK_STATUS: " + ex.getMessage());
                    return null;
                });

        lookupApi.getAll(LookupType.PRIORITY)
                .thenAccept(priorities::onNext)
                .exceptionally(ex -> {
                    System.err.println("[NewTaskViewModel] PRIORITY: " + ex.getMessage());
                    return null;
                });
    }

    // ── Load project members + xác định role ─────────────────────────────────

    public void loadProjectMembers(Long currentMemberId) {
        if (projectId == null) return;

        projectApi.getMembersOfProject(projectId)
                .thenAccept(members -> {
                    System.out.println(members);
                    projectMembers.onNext(members);

                    // Kiểm tra role: nếu roleName chứa "Manager" → canManage = true
                    boolean isManager = false;
                    if (currentMemberId != null) {
                        isManager = members.stream().anyMatch(m -> {
                            if (!currentMemberId.equals(m.getProjectMemberId())) return false;
                            String role = m.getRoleName() != null
                                    ? m.getRoleName().toUpperCase() : "";
                            return role.contains("Manager") || role.contains("Co-Project Manager");
                        });
                    }
                    canManageAssignees.onNext(isManager);
                })
                .exceptionally(ex -> {
                    System.err.println("[NewTaskViewModel] members: " + ex.getMessage());
                    return null;
                });
    }

    // ── INPUT: form fields ────────────────────────────────────────────────────

    public void setTaskName(String name)    { taskName.onNext(name != null ? name : ""); }
    public void setDueDate(LocalDate date)  { dueDate.onNext(date != null ? date : LocalDate.MIN); }
    public void setStatus(LookupDTO dto)    { status.onNext(dto != null ? dto : new LookupDTO()); }
    public void setPriority(LookupDTO dto)  { priority.onNext(dto != null ? dto : new LookupDTO()); }
    public void setDescription(String desc) { description.onNext(desc != null ? desc : ""); }

    // ── INPUT: assignees ──────────────────────────────────────────────────────

    /** Thêm member vào danh sách sẽ assign sau khi tạo task. */
    public void addSelectedAssignee(ProjectMemberDTO member) {
        if (member == null) return;
        List<ProjectMemberDTO> current = new ArrayList<>(selectedAssignees.getValue());
        boolean exists = current.stream()
                .anyMatch(m -> member.getProjectMemberId() != null && member.getProjectMemberId().equals(m.getProjectMemberId()));
        if (!exists) {
            current.add(member);
            selectedAssignees.onNext(current);
        }
    }

    /** Xóa member khỏi danh sách đã chọn (khi nhấn "×" trên chip). */
    public void removeSelectedAssignee(Long projectMemberId) {
        List<ProjectMemberDTO> current = new ArrayList<>(selectedAssignees.getValue());
        current.removeIf(m -> projectMemberId != null && projectMemberId.equals(m.getProjectMemberId()));
        selectedAssignees.onNext(current);
    }

    // ── OUTPUT: Observables ───────────────────────────────────────────────────

    public Observable<List<LookupDTO>>        taskStatusesObservable()     { return taskStatuses; }
    public Observable<List<LookupDTO>>        prioritiesObservable()       { return priorities; }
    public Observable<Boolean>                canManageAssigneesObservable(){ return canManageAssignees; }
    public Observable<List<ProjectMemberDTO>> projectMembersObservable()   { return projectMembers; }
    public Observable<List<ProjectMemberDTO>> selectedAssigneesObservable(){ return selectedAssignees; }

    public Observable<Boolean> isFormValidObservable() {
        return Observable.combineLatest(taskName, dueDate,
                (name, date) -> !name.trim().isEmpty() && !date.equals(LocalDate.MIN));
    }

    /** Snapshot danh sách member hiện tại (dùng để filter trong search popup). */
    public List<ProjectMemberDTO> getProjectMembersSnapshot() {
        return new ArrayList<>(projectMembers.getValue());
    }

    /** Snapshot assignees đã chọn (dùng để lọc khỏi search popup). */
    public List<ProjectMemberDTO> getSelectedAssigneesSnapshot() {
        return new ArrayList<>(selectedAssignees.getValue());
    }

    // ── SUBMIT ────────────────────────────────────────────────────────────────

    /**
     * Luồng:
     *   1. POST /projects/{projectId}/tasks  → tạo task, nhận TaskResponseDTO có taskId
     *   2. Nếu có assignees đã chọn → POST /tasks/{taskId}/members body:[memberId,...]
     *   3. Gọi onSuccess → TasksViewController.reloadData()
     */
    public void submitTask() {
        TaskCreateRequestDTO dto = new TaskCreateRequestDTO();

        dto.setName(taskName.getValue());
        dto.setProjectId(projectId);
        dto.setDescription(description.getValue());

        // ✅ deadline
        if (!dueDate.getValue().equals(LocalDate.MIN)) {
            dto.setDeadline(dueDate.getValue().atStartOfDay());
        }

        // ✅ status
        if (status.getValue() != null && status.getValue().getId() != null) {
            dto.setTaskStatusId(Long.parseLong(status.getValue().getId()));
        }

        // ✅ priority
        if (priority.getValue() != null && priority.getValue().getId() != null) {
            dto.setPriorityId(Long.parseLong(priority.getValue().getId()));
        }

        // ✅ assignee list
        List<Long> assigneeIds = selectedAssignees.getValue().stream()
                .map(ProjectMemberDTO::getProjectMemberId)
                .toList();

        // 🚀 CALL API
        projectApi.createTaskInProject(projectId, dto)
                .thenCompose(createdTask -> {
                    if (assigneeIds.isEmpty()) {
                        return java.util.concurrent.CompletableFuture.completedFuture(null);
                    }

                    // 🔥 CHỖ QUAN TRỌNG
                    return taskApi.assignMember(createdTask.getTaskId(), assigneeIds);
                })
                .thenAccept(v -> {
                    if (onSuccess != null) Platform.runLater(onSuccess);
                })
                .exceptionally(ex -> {
                    System.err.println("[NewTaskViewModel] submitTask error: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
    }
}