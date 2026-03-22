package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.TaskResponseDTO;
import com.group4.projects_management_fe.core.api.ProjectApi;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectTasksViewModel {

    private final ProjectApi projectApi = new ProjectApi(AppSessionManager.getInstance());

    // Nơi chứa dữ liệu gốc từ API
    private final BehaviorSubject<List<TaskResponseDTO>> allTasksSubject = BehaviorSubject.createDefault(new ArrayList<>());

    // Nơi chứa các điều kiện lọc
    private final BehaviorSubject<String> searchSubject = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> sortSubject = BehaviorSubject.createDefault("Newest");

    public void loadTasksForProject(Long projectId) {
        // Đổi tên hàm gọi API ở đây cho khớp với backend của bạn
        projectApi.getTasksByProjectId(projectId, false).thenAccept(tasks -> {
            allTasksSubject.onNext(tasks);
        }).exceptionally(ex -> {
            System.err.println("Lỗi gọi API getTasksByProjectId: " + ex.getMessage());
            return null;
        });
    }

    // Luồng dữ liệu ĐÃ LỌC để UI lắng nghe và vẽ lại
    public Observable<List<TaskResponseDTO>> filteredTasksObservable() {
        return Observable.combineLatest(
                allTasksSubject, searchSubject, sortSubject,
                (tasks, keyword, sortType) -> {
                    // 1. Lọc theo tên (Search)
                    List<TaskResponseDTO> filtered = tasks.stream()
                            .filter(t -> keyword.isEmpty() ||
                                    (t.getName() != null && t.getName().toLowerCase().contains(keyword.toLowerCase())))
                            .collect(Collectors.toList());

                    // 2. Sắp xếp (Sort)
                    switch (sortType) {
                        case "Oldest":
                            filtered.sort(Comparator.comparing(TaskResponseDTO::getCreatedAt));
                            break;
                        case "Deadline (Earliest)":
                            filtered.sort(Comparator.comparing(TaskResponseDTO::getDeadline));
                            break;
                        case "Newest":
                        default:
                            filtered.sort(Comparator.comparing(TaskResponseDTO::getCreatedAt).reversed());
                            break;
                    }
                    return filtered;
                }
        );
    }

    public void setSearchKeyword(String keyword) {
        searchSubject.onNext(keyword != null ? keyword : "");
    }

    public void setSortType(String sortType) {
        sortSubject.onNext(sortType != null ? sortType : "Newest");
    }
}