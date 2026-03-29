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

    private final BehaviorSubject<List<TaskResponseDTO>> allTasksSubject = BehaviorSubject.createDefault(new ArrayList<>());

    private final BehaviorSubject<String> searchSubject = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> sortSubject = BehaviorSubject.createDefault("Newest");

    private final BehaviorSubject<Boolean> canCreateTaskSubject = BehaviorSubject.createDefault(false);

    public void loadTasksForProject(Long projectId) {
        // Đổi tên hàm gọi API ở đây cho khớp với backend của bạn
        projectApi.getTasksByProjectId(projectId, false).thenAccept(tasks -> {
            allTasksSubject.onNext(tasks);
        }).exceptionally(ex -> {
            System.err.println("Lỗi gọi API getTasksByProjectId: " + ex.getMessage());
            return null;
        });
    }

    public Observable<Boolean> getCanCreateTask() { return canCreateTaskSubject; }

    public void checkUserRole(Long projectId) {
        if (projectId == null) return;

        Long currentUserId = AppSessionManager.getInstance().getCurrentUser().getId();

        projectApi.getMembersOfProject(projectId).thenAccept(members -> {
            boolean isManager = members.stream()
                    .anyMatch(m -> String.valueOf(m.getUserId()).equals(String.valueOf(currentUserId))
                            && ("Project Manager".equalsIgnoreCase(m.getRoleName()) || "Co-Project Manager".equalsIgnoreCase(m.getRoleName())));

            canCreateTaskSubject.onNext(isManager);
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi lấy danh sách member để phân quyền: " + ex.getMessage());
            canCreateTaskSubject.onNext(false);
            return null;
        });
    }

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
                    // 2. Sắp xếp (Sort)
                    switch (sortType) {
                        case "Oldest":
                            filtered.sort(Comparator.comparing(TaskResponseDTO::getCreatedAt));
                            break;
                        case "Deadline (Earliest)":
                            filtered.sort(Comparator.comparing(TaskResponseDTO::getDeadline, Comparator.nullsLast(Comparator.naturalOrder())));
                            break;
                        case "Name A-Z":
                            filtered.sort((t1, t2) -> {
                                String n1 = t1.getName() != null ? t1.getName() : "";
                                String n2 = t2.getName() != null ? t2.getName() : "";
                                return n1.compareToIgnoreCase(n2);
                            });
                            break;
                        case "Name Z-A":
                            filtered.sort((t1, t2) -> {
                                String n1 = t1.getName() != null ? t1.getName() : "";
                                String n2 = t2.getName() != null ? t2.getName() : "";
                                return n2.compareToIgnoreCase(n1);
                            });
                            break;
                        case "Status A-Z":
                            filtered.sort((t1, t2) -> {
                                String s1 = t1.getStatusName() != null ? t1.getStatusName() : "";
                                String s2 = t2.getStatusName() != null ? t2.getStatusName() : "";
                                return s1.compareToIgnoreCase(s2);
                            });
                            break;
                        case "Priority A-Z":
                            filtered.sort((t1, t2) -> {
                                String p1 = t1.getPriorityName() != null ? t1.getPriorityName() : "";
                                String p2 = t2.getPriorityName() != null ? t2.getPriorityName() : "";
                                return p1.compareToIgnoreCase(p2);
                            });
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