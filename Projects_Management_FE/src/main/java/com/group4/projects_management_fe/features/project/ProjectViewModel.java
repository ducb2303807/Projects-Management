package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.ProjectResponseDTO;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.session.AppSessionManager;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.List;

public class ProjectViewModel {

    // Truyền trực tiếp AppSessionManager (đã implement AuthSessionProvider) vào API
    private final ProjectApi projectApi = new ProjectApi(AppSessionManager.getInstance());

    private final BehaviorSubject<List<ProjectResponseDTO>> projectsSubject = BehaviorSubject.createDefault(new ArrayList<>());

    public void fetchMyProjects() {
        projectApi.getMyProjects().thenAccept(projects -> {
            projectsSubject.onNext(projects);
            System.out.println("Tải thành công " + projects.size() + " dự án.");
        }).exceptionally(ex -> {
            System.err.println("Lỗi gọi API getMyProjects: " + ex.getMessage());
            return null;
        });
    }

    public Observable<List<ProjectResponseDTO>> projectsObservable() {
        return projectsSubject.hide();
    }
}