package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.ProjectCreateRequestDTO;
import com.group4.common.dto.ProjectResponseDTO;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.session.AppSessionManager;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NewProjectViewModel {

    private final ProjectApi projectApi = new ProjectApi(AppSessionManager.getInstance());

    // --- 1. STATE ---
    private final BehaviorSubject<String> projectName = BehaviorSubject.createDefault("");
    private final BehaviorSubject<LocalDate> startDate = BehaviorSubject.createDefault(LocalDate.MIN);
    private final BehaviorSubject<LocalDate> endDate = BehaviorSubject.createDefault(LocalDate.MIN);
    private final BehaviorSubject<String> description = BehaviorSubject.createDefault("");

    private final PublishSubject<ProjectResponseDTO> createSuccess = PublishSubject.create();
    private final PublishSubject<String> createError = PublishSubject.create();

    // --- 2. INPUT ---
    public void setProjectName(String name) { projectName.onNext(name); }
    public void setStartDate(LocalDate date) { startDate.onNext(date != null ? date : LocalDate.MIN); }
    public void setEndDate(LocalDate date) { endDate.onNext(date != null ? date : LocalDate.MIN); }
    public void setDescription(String desc) { description.onNext(desc); }

    // --- 3. OUTPUT ---
    public Observable<ProjectResponseDTO> onCreateSuccess() { return createSuccess.hide(); }
    public Observable<String> onCreateError() { return createError.hide(); }

    public Observable<Boolean> isFormValidObservable() {
        return Observable.combineLatest(
                projectName,
                startDate,
                (name, start) -> !name.trim().isEmpty() && !start.equals(LocalDate.MIN)
        );
    }

    // --- 4. GỌI API ---
    public void submitProject() {
        ProjectCreateRequestDTO dto = new ProjectCreateRequestDTO();

        // Gắn 4 trường cơ bản
        dto.setProjectName(projectName.getValue());
        dto.setDescription(description.getValue());

        if (!startDate.getValue().equals(LocalDate.MIN)) {
            dto.setStartDate(LocalDateTime.of(startDate.getValue(), LocalTime.MIDNIGHT));
        }

        if (!endDate.getValue().equals(LocalDate.MIN)) {
            dto.setEndDate(LocalDateTime.of(endDate.getValue(), LocalTime.MAX));
        }

        // ==========================================
        // WORKAROUND CHO DATABASE:
        // Cố định status khi Create là Active (ID 2).
        // (Bạn hãy mở comment dùng hàm set tương ứng có trong ProjectBaseDTO của bạn)
        // ==========================================

        // Nếu dùng ID:
        // dto.setStatusId(2L);

        // Nếu dùng System Code:
        // dto.setSystemCode("ACTIVE");

        // Nếu dùng enum:
        // dto.setStatus("ON_GOING");

        projectApi.createProject(dto).thenAccept(response -> {
            createSuccess.onNext(response);
        }).exceptionally(ex -> {
            createError.onNext(ex.getMessage());
            return null;
        });
    }
}