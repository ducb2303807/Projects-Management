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

    protected final ProjectApi projectApi = new ProjectApi(AppSessionManager.getInstance());

    // --- ĐỔI SANG PROTECTED ĐỂ PROJECT_DETAILS_VIEW_MODEL CÓ THỂ DÙNG ---
    protected final BehaviorSubject<String> projectName = BehaviorSubject.createDefault("");
    protected final BehaviorSubject<LocalDate> startDate = BehaviorSubject.createDefault(LocalDate.MIN);
    protected final BehaviorSubject<LocalDate> endDate = BehaviorSubject.createDefault(LocalDate.MIN);
    protected final BehaviorSubject<String> description = BehaviorSubject.createDefault("");

    private final PublishSubject<ProjectResponseDTO> createSuccess = PublishSubject.create();
    private final PublishSubject<String> createError = PublishSubject.create();

    public void setProjectName(String name) { projectName.onNext(name); }
    public void setStartDate(LocalDate date) { startDate.onNext(date); }
    public void setEndDate(LocalDate date) { endDate.onNext(date); }
    public void setDescription(String desc) { description.onNext(desc); }

    public Observable<ProjectResponseDTO> onCreateSuccess() { return createSuccess.hide(); }
    public Observable<String> onCreateError() { return createError.hide(); }

    // --- 3. VALIDATION ---
    public Observable<Boolean> isFormValidObservable() {
        return Observable.combineLatest(
                projectName,
                startDate,
                (name, start) -> !name.trim().isEmpty() && !start.equals(LocalDate.MIN)
        );
    }

    public void submitProject() {
        ProjectCreateRequestDTO dto = new ProjectCreateRequestDTO();
        dto.setProjectName(projectName.getValue());
        dto.setDescription(description.getValue());

        if (!startDate.getValue().equals(LocalDate.MIN)) {
            dto.setStartDate(LocalDateTime.of(startDate.getValue(), LocalTime.MIDNIGHT));
        }
        if (!endDate.getValue().equals(LocalDate.MIN)) {
            dto.setEndDate(LocalDateTime.of(endDate.getValue(), LocalTime.MAX));
        }

        projectApi.createProject(dto).thenAccept(response -> {
            createSuccess.onNext(response);
        }).exceptionally(ex -> {
            createError.onNext(ex.getMessage());
            return null;
        });
    }
}